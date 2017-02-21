/*
 * Copyright 2013 - 2017 Outworkers, Limited.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.outworkers

package morpheus
package mysql

import com.outworkers.morpheus.mysql.query.{MySQLInsertQuery, MySQLRootInsertQuery, MySQLRootUpdateQuery, UpdateQuery}
import com.outworkers.morpheus.engine.query.{AssignBind, AssignUnchainned}
import com.outworkers.morpheus.builder.SQLBuiltQuery
import com.outworkers.morpheus.column.{AbstractColumn, AbstractModifyColumn, Column, SelectColumn}
import com.outworkers.morpheus.engine.query._
import com.outworkers.morpheus.{Row => MorpheusRow}
import shapeless.{HList, HNil}

import scala.util.Try

trait Implicits extends DefaultSQLEngines {

  implicit class SelectColumnRequired[
    Owner <: BaseTable[Owner, Record, TableRow],
    Record, TableRow <: MorpheusRow, T
  ](col: Column[Owner, Record, TableRow, T]) extends SelectColumn[T](SQLBuiltQuery(col.name)) {
    def apply(r: morpheus.Row): T = col.apply(r)
  }

  implicit class ModifyColumn[RR : DataType](col: AbstractColumn[RR]) extends AbstractModifyColumn[RR](col)
  implicit class OrderingColumn[RR : DataType](col: AbstractColumn[RR]) extends AbstractOrderingColumn[RR](col)

  implicit def selectOperatorClauseToSelectColumn[T](
    clause: SelectOperatorClause[T]
  ): SelectColumn[T] = new SelectColumn[T](clause.qb) {
    def apply(row: MorpheusRow): T = clause.fromRow(row)
  }

  /**
   * This defines an implicit conversion from a RootInsertQuery to an InsertQuery,
   * making the INSERT syntax block invisible to the end user.
   * This is used to automatically "exit" the INSERT syntax block with the default "INSERT INTO" option,
   * while picking no other SQL options such as IGNORE or
   * LOW_PRIORITY.
   *
   * This is making the following queries equivalent:
   * - Table.insert.into.queryString = "INSERT INTO table"
   * - Table.insert = "INSERT INTO table"
   * @param root The RootSelectQuery to convert.
   * @tparam T The table owning the record.
   * @tparam R The record type.
   * @return An executable SelectQuery.
   */
  implicit def rootInsertQueryToQuery[T <: BaseTable[T, _, mysql.Row], R](
    root: MySQLRootInsertQuery[T, R]
  ): MySQLInsertQuery[T, R, Ungroupped,
    Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new MySQLInsertQuery(
      root.table,
      root.st.into,
      root.rowFunc
    )
  }

  /**
   * This defines an implicit conversion from a RootUpdateQuery to an UpdateQuery,
   * making the UPDATE syntax block invisible to the end user.
   * Much like a decision block, a UpdateSyntaxBlock needs a decision branch to follow, may that be nothing, LOW_PRIORITY or IGNORE.
   *
   * The one catch is that this form of "exit" from an un-executable RootUpdateQuery will directly translate the query to an "UPDATE tableName"
   * query, meaning no UPDATE operators will be used in the default serialisation.
   *
   * The simple assumption made here is that since the user didn't use any other provided method,
   * such as "lowPriority" or "ignore" the desired behaviour is
   * a full select.
   *
   * @param root The RootSelectQuery to convert.
   * @tparam T The table owning the record.
   * @tparam R The record type.
   * @return An executable SelectQuery.
   */
  implicit def defaultUpdateQueryToUpdateQuery[
    T <: BaseTable[T, R, Row],
    R
  ](root: MySQLRootUpdateQuery[T, R]): UpdateQuery[T, R,
    Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new UpdateQuery(
      root.table,
      root.st.all,
      root.rowFunc
    )
  }

  implicit def updateQueryToAssignmentsQuery[T <: BaseTable[T, R, Row],
    R,
    Group <: GroupBind,
    Order <: OrderBind,
    Limit <: LimitBind,
    Chain <: ChainBind,
    AssignChain <: AssignBind,
    Status <: HList
  ](
    query: UpdateQuery[T, R, Group, Order, Limit, Chain, AssignChain, Status]
  ): UpdateQuery[T, R, Group, Order, Limit, Chain, AssignChain, Status] = {
    new UpdateQuery[T, R, Group, Order, Limit, Chain, AssignChain, Status](query.table, query.query, query.rowFunc)
  }

  def enumPrimitive[T <: Enumeration](enum: T): DataType[T#Value] = {
    new DataType[T#Value] {

      private[this] val primitive = implicitly[DataType[String]]

      override val sqlType: String = primitive.sqlType

      override def serialize(value: T#Value): String = primitive.serialize(value.toString)

      override def deserialize(row: MorpheusRow, name: String): Try[T#Value] = {
        primitive.deserialize(row, name) map (x => enum.withName(x))
      }
    }
  }


}
