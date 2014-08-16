/*
 * Copyright 2014 websudos ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.websudos.morpheus.column

import com.twitter.finagle.exp.mysql.Row
import com.websudos.morpheus.dsl.Table
import com.websudos.morpheus.query._
import com.websudos.morpheus.SQLPrimitive

private[morpheus] trait ModifyImplicits extends LowPriorityImplicits with JoinImplicits {

  implicit class SelectColumnRequired[Owner <: Table[Owner, Record], Record, T](col: Column[Owner, Record, T]) extends SelectColumn[T](col) {
    def apply(r: Row): T = col.apply(r)
  }


  implicit class ModifyColumn[RR: SQLPrimitive](col: AbstractColumn[RR]) extends AbstractModifyColumn[RR](col)
  implicit class OrderingColumn[RR: SQLPrimitive](col: AbstractColumn[RR]) extends AbstractOrderingColumn[RR](col)

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
  implicit def rootUpdateQueryToUpdateQuery[T <: Table[T, _], R](root: AbstractRootUpdateQuery[T, R]): Query[T, R, UpdateType, Ungroupped, Unordered, Unlimited,
    Unchainned, AssignUnchainned, Unterminated
  ] = {
    new Query(
      root.table,
      root.st.all,
      root.rowFunc
    )
  }

  implicit def rootUpdateQueryToAssignQuery[T <: Table[T, _], R](root: AbstractRootUpdateQuery[T, R]): AssignmentsQuery[T, R, UpdateType, Ungroupped,
    Unordered,
    Unlimited,
    Unchainned, AssignUnchainned, Unterminated
  ] = {
    new AssignmentsQuery(
      new Query(
        root.table,
        root.st.all,
        root.rowFunc
      )
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
  implicit def rootDeleteQueryToDeleteQuery[T <: Table[T, _], R](root: AbstractRootDeleteQuery[T, R]): Query[T,
    R,
    DeleteType,
    Ungroupped,
    Unordered,
    Unlimited,
    Unchainned,
    AssignUnchainned,
    Unterminated
  ] = {
    new Query(
      root.table,
      root.st.all,
      root.rowFunc
    )
  }

  /**
   * This defines an implicit conversion from a RootSelectQuery to a SelectQuery, making the SELECT syntax block invisible to the end user.
   * Much like a decision block, a SelectSyntaxBlock needs a decision branch to follow, may that be DISTINCT, ALL or DISTINCTROW as per the SQL spec.
   *
   * The one catch is that this form of "exit" from an un-executable RootSelectQuery will directly translate the query to a "SELECT fields* FROM tableName"
   * query, meaning no SELECT operators will be used in the serialisation.
   *
   * The simple assumption made here is that since the user didn't use any other provided method, such as "all", "distinct" or "distinctrow",
   * the desired behaviour is a full select.
   *
   * @param root The RootSelectQuery to convert.
   * @tparam T The table owning the record.
   * @tparam R The record type.
   * @return An executable SelectQuery.
   */
  implicit def rootSelectQueryToSelectQuery[T <: Table[T, _], R](root: AbstractRootSelectQuery[T, R]): Query[T, R, SelectType, Ungroupped, Unordered, Unlimited,
    Unchainned, AssignUnchainned, Unterminated] = {
    new Query(
      root.table,
      root.st.*,
      root.rowFunc
    )
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
  implicit def rootInsertQueryToQuery[T <: Table[T, _], R](root: AbstractRootInsertQuery[T, R]): Query[T, R, InsertType, Ungroupped, Unordered, Unlimited,
    Unchainned, AssignUnchainned, Unterminated] = {
    new Query(
      root.table,
      root.st.into,
      root.rowFunc
    )
  }

  implicit def rootCreateQueryToQuery[T <: Table[T, _], R](root: AbstractRootCreateQuery[T, R]): Query[T, R, CreateType, Ungroupped, Unordered, Unlimited,
    Unchainned, AssignUnchainned, Unterminated] = {
    new Query(
      root.table,
      root.st.default,
      root.rowFunc
    )
  }

  /**
   * This defines an implicit conversion from a RootInsertQuery to an InsertQuery, making the INSERT syntax block invisible to the end user.
   * This allows chaining a "value" method call directly after "Table.insert".
   *
   * @param root The RootSelectQuery to convert.
   * @tparam T The table owning the record.
   * @tparam R The record type.
   * @return An executable SelectQuery.
   */
  implicit def rootInsertQueryToInsertQuery[T <: Table[T, _], R](root: AbstractRootInsertQuery[T, R]): InsertQuery[T, R, InsertType, Ungroupped, Unordered,
    Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new InsertQuery(
      new Query(
        root.table,
        root.st.into,
        root.rowFunc
      )
    )
  }

  implicit def queryToAssignmentsQuery[T <: Table[T, _],
    R,
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    C <: ChainBind,
    AC <: AssignBind,
    Status <: StatusBind
  ](query: Query[T, R, UpdateType, G, O, L, C, AC, Status]): AssignmentsQuery[T, R, UpdateType, G, O, L, C, AC, Status] = {
    new AssignmentsQuery(query)
  }

  implicit def assignmentToQuery[T <: Table[T, _],
    R,
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    C <: ChainBind,
    AC <: AssignBind,
    Status <: StatusBind
  ](assignment: AssignmentsQuery[T, R, UpdateType, G, O, L, C, AC, Status]): Query[T, R, UpdateType, G, O, L, C, AssignChainned, Terminated] =
    assignment.terminate

  implicit def queryToSelectQuery[T <: Table[T, _],
    R,
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    C <: ChainBind,
    AC <: AssignBind,
    Status <: StatusBind
  ](query: Query[T, R, SelectType, G, O, L, C, AC, Status]): SelectQuery[T, R, SelectType, G, O, L, C, AC, Status] = {
    new SelectQuery(query)
  }

  implicit def selectQueryToQuery[T <: Table[T, _],
    R,
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    C <: ChainBind,
    AC <: AssignBind,
    Status <: StatusBind
  ](assignment: SelectQuery[T, R, SelectType, G, O, L, C, AC, Status]): Query[T, R, SelectType, G, O, L, C, AC, Terminated] = assignment.terminate

  implicit def queryInsertQuery[T <: Table[T, _],
    R,
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    C <: ChainBind,
    AC <: AssignBind,
    Status <: StatusBind
  ](query: Query[T, R, InsertType, G, O, L, C, AC, Status]): InsertQuery[T, R, InsertType, G, O, L, C, AC, Status] = {
    new InsertQuery(query)
  }

  implicit def insertQueryToQuery[T <: Table[T, _],
    R,
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    C <: ChainBind,
    AC <: AssignBind,
    Status <: StatusBind
  ](assignment: InsertQuery[T, R, InsertType, G, O, L, C, AC, Status]): Query[T, R, InsertType, G, O, L, C, AC, Terminated] = assignment.toQuery

  implicit def queryToCreateQuery[T <: Table[T, _],
    R,
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    C <: ChainBind,
    AC <: AssignBind,
    Status <: StatusBind
  ](query: Query[T, R, CreateType, G, O, L, C, AC, Status]): CreateQuery[T, R, CreateType, G, O, L, C, AC, Status] = {
    new CreateQuery(query)
  }

  implicit def createQueryToQuery[T <: Table[T, _],
    R,
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    C <: ChainBind,
    AC <: AssignBind,
    Status <: StatusBind
  ](assignment: CreateQuery[T, R, CreateType, G, O, L, C, AC, Status]): Query[T, R, CreateType, G, O, L, C, AC, Terminated] = assignment.terminate
}
