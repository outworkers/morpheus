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
 * - Explicit consent must be obtained from the copyright owner, Websudos Limited before any redistribution is made.
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

package com.outworkers.morpheus.mysql.query

import com.outworkers.morpheus.DataType
import com.outworkers.morpheus.mysql.{MySQLRow, MySQLSyntax}
import com.outworkers.morpheus.builder.SQLBuiltQuery
import com.outworkers.morpheus.column.AbstractColumn
import com.outworkers.morpheus.dsl.BaseTable
import com.outworkers.morpheus.query._
import com.outworkers.morpheus.query.parts.{ColumnsPart, Defaults, LightweightPart, ValuePart}
import shapeless.{HList, HNil}

import scala.annotation.implicitNotFound

private[morpheus] class MySQLInsertSyntaxBlock(query: String, tableName: String) extends RootInsertSyntaxBlock(query, tableName) {
  override val syntax = MySQLSyntax

  private[this] def insertOption(option: String, table: String): SQLBuiltQuery = {
    qb.pad.append(option)
      .forcePad.append(syntax.into)
      .forcePad.append(table)
  }


  def delayed: SQLBuiltQuery = {
    insertOption(syntax.InsertOptions.delayed, tableName)
  }

  def lowPriority: SQLBuiltQuery = {
    insertOption(syntax.Priorities.lowPriority, tableName)
  }

  def highPriority: SQLBuiltQuery = {
    insertOption(syntax.Priorities.highPriority, tableName)
  }

  def ignore: SQLBuiltQuery = {
    insertOption(syntax.InsertOptions.ignore, tableName)
  }
}


class MySQLRootInsertQuery[T <: BaseTable[T, _, MySQLRow], R](table: T, st: MySQLInsertSyntaxBlock, rowFunc: MySQLRow => R)
  extends RootInsertQuery[T, R, MySQLRow](table, st, rowFunc) {

  def delayed: MySQLInsertQuery.Default[T, R] = {
    new MySQLInsertQuery(table, st.delayed, rowFunc)
  }

  def lowPriority: MySQLInsertQuery.Default[T, R] = {
    new MySQLInsertQuery(table, st.lowPriority, rowFunc)
  }

  def highPriority: MySQLInsertQuery.Default[T, R] = {
    new MySQLInsertQuery(table, st.highPriority, rowFunc)
  }

  def ignore: MySQLInsertQuery.Default[T, R] = {
    new MySQLInsertQuery(table, st.ignore, rowFunc)
  }

}

class MySQLInsertQuery[T <: BaseTable[T, _, MySQLRow],
  R,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: HList
](table: T,
  override val init: SQLBuiltQuery,
  rowFunc: MySQLRow => R,
  columnsPart: ColumnsPart = Defaults.EmptyColumnsPart,
  valuePart: ValuePart = Defaults.EmptyValuePart,
  lightweightPart: LightweightPart = Defaults.EmptyLightweightPart
) extends InsertQuery[T, R, MySQLRow, Group, Order, Limit, Chain, AssignChain, Status](table: T, init, rowFunc) {

  override def query: SQLBuiltQuery = (columnsPart merge valuePart merge lightweightPart) build init

  override protected[this] def create[
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    S <: ChainBind,
    C <: AssignBind,
    P <: HList
  ](t: T, q: SQLBuiltQuery, r: MySQLRow => R): QueryType[G, O, L, S, C, P] = {
    new MySQLInsertQuery(t, q, r, columnsPart, valuePart, lightweightPart)
  }

  /**
    * At this point you may be reading and thinking "WTF", but fear not, it all makes sense.
    * Every call to a "value method" will generate a new Insert Query,
    * but the list of statements in the new query will include a new (String, String) pair,
    * where the first part is the column name and the second one is the
    * serialised value
    *
    * This is a very simple accumulator that will eventually allow calling the "insert" method on a queryBuilder to produce the final
    * serialisation result, a hopefully valid MySQL insert query.
    *
    * @param insertion The insert condition is a pair of a column with the value to use for it.
    *                  It looks like this: value(_.someColumn, someValue), where the assignment is of course type safe.
    * @param obj The object is the value to use for the column.
    * @tparam RR The SQL primitive or rather it's Scala correspondent to use at this time.
    * @return A new InsertQuery, where the list of statements in the Insert has been chained and updated for serialisation.
    */
  @implicitNotFound(msg = "To use the value method this query needs to be an insert query and the query needs to be unterminated. You probably have more " +
    "value calls than columns in your table, which would result in an invalid MySQL query.")
  override def value[RR : DataType](
    insertion: T => AbstractColumn[RR], obj: RR
  ): MySQLInsertQuery[T, R, Group, Order, Limit, Chain, AssignChain, Status] = {
    new MySQLInsertQuery[T, R, Group, Order, Limit, Chain, AssignChain, Status](
      table,
      init,
      fromRow,
      columnsPart append SQLBuiltQuery(insertion(table).name),
      valuePart append SQLBuiltQuery(implicitly[DataType[RR]].serialize(obj)),
      lightweightPart
    )
  }
}

object MySQLInsertQuery {
  type Default[T <: BaseTable[T, _, MySQLRow], R] = MySQLInsertQuery[
    T,
    R,
    Ungroupped,
    Unordered,
    Unlimited,
    Unchainned,
    AssignUnchainned,
    HNil
    ]
}
