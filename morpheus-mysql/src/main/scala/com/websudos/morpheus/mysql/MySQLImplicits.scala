/*
 * Copyright 2015 websudos ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.websudos.morpheus.mysql

import com.websudos.morpheus.{ Row => MorpheusRow }
import com.websudos.morpheus.builder.SQLBuiltQuery
import com.websudos.morpheus.column.{AbstractColumn, AbstractModifyColumn, Column, SelectColumn}
import com.websudos.morpheus.mysql.query.{MySQLUpdateQuery, MySQLInsertQuery, MySQLRootInsertQuery, MySQLRootUpdateQuery}
import com.websudos.morpheus.query._

trait MySQLImplicits extends DefaultSQLEngines {

  implicit class SelectColumnRequired[Owner <: BaseTable[Owner, Record, TableRow], Record, TableRow <: MorpheusRow,  T](col: Column[Owner, Record, TableRow, T])
    extends SelectColumn[T](SQLBuiltQuery(col.name)) {
    def apply(r: MorpheusRow): T = col.apply(r)
  }


  implicit class ModifyColumn[RR: SQLPrimitive](col: AbstractColumn[RR]) extends AbstractModifyColumn[RR](col)
  implicit class OrderingColumn[RR: SQLPrimitive](col: AbstractColumn[RR]) extends AbstractOrderingColumn[RR](col)

  implicit def selectOperatorClauseToSelectColumn[T](clause: SelectOperatorClause[T]): SelectColumn[T] = new SelectColumn[T](clause.qb) {
    def apply(row: MorpheusRow): T = clause.fromRow(row)
  }

  /**
   * This defines an implicit conversion from a RootInsertQuery to an InsertQuery, making the INSERT syntax block invisible to the end user.
   * This is used to automatically "exit" the INSERT syntax block with the default "INSERT INTO" option, while picking no other SQL options such as IGNORE or
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
  implicit def rootInsertQueryToQuery[T <: BaseTable[T, _, MySQLRow], R](root: MySQLRootInsertQuery[T, R]): MySQLInsertQuery[T, R, Ungroupped,
    Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new MySQLInsertQuery(
      root.table,
      root.st.into,
      root.rowFunc
    )
  }

  /**
   * This defines an implicit conversion from a RootUpdateQuery to an UpdateQuery, making the UPDATE syntax block invisible to the end user.
   * Much like a decision block, a UpdateSyntaxBlock needs a decision branch to follow, may that be nothing, LOW_PRIORITY or IGNORE.
   *
   * The one catch is that this form of "exit" from an un-executable RootUpdateQuery will directly translate the query to an "UPDATE tableName"
   * query, meaning no UPDATE operators will be used in the default serialisation.
   *
   * The simple assumption made here is that since the user didn't use any other provided method, such as "lowPriority" or "ignore" the desired behaviour is
   * a full select.
   *
   * @param root The RootSelectQuery to convert.
   * @tparam T The table owning the record.
   * @tparam R The record type.
   * @return An executable SelectQuery.
   */
  implicit def defaultUpdateQueryToUpdateQuery[T <: BaseTable[T, R, MySQLRow], R](root: MySQLRootUpdateQuery[T, R]): MySQLUpdateQuery[T, R,
    Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new MySQLUpdateQuery(
      root.table,
      root.st.all,
      root.rowFunc
    )
  }

  implicit def updateQueryToAssignmentsQuery[T <: BaseTable[T, R, MySQLRow],
    R,
    Group <: GroupBind,
    Order <: OrderBind,
    Limit <: LimitBind,
    Chain <: ChainBind,
    AssignChain <: AssignBind,
    Status <: StatusBind
  ](query: MySQLUpdateQuery[T, R, Group, Order, Limit, Chain, AssignChain, Status]): MySQLUpdateQuery[T, R, Group, Order, Limit, Chain,
    AssignChain, Status] = {
    new MySQLUpdateQuery[T, R, Group, Order, Limit, Chain, AssignChain, Status](query.table, query.query, query.rowFunc)
  }

}
