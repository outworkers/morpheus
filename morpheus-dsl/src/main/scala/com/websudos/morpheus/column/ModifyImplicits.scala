package com.websudos.morpheus.column

import com.twitter.finagle.exp.mysql.Row
import com.websudos.morpheus.dsl.Table
import com.websudos.morpheus.query._
import com.websudos.morpheus.SQLPrimitive

private[morpheus] trait ModifyImplicits extends LowPriorityImplicits {

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
  implicit def rootUpdateQueryToSelectQuery[T <: Table[T, _], R](root: AbstractRootUpdateQuery[T, R]): Query[T, R, Ungroupped, Unordered, Unlimited,
    Unchainned, AssignUnchainned] = {
    new Query(
      root.table,
      root.st.all,
      root.rowFunc
    )
  }

  implicit def rootUpdateQueryToAssignQuery[T <: Table[T, _], R](root: AbstractRootUpdateQuery[T, R]): AssignmentsQuery[T, R, Ungroupped, Unordered, Unlimited,
    Unchainned, AssignUnchainned] = {
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
  implicit def rootDeleteQueryToDeleteQuery[T <: Table[T, _], R](root: AbstractRootDeleteQuery[T, R]): Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned,
    AssignUnchainned] = {
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
  implicit def rootSelectQueryToSelectQuery[T <: Table[T, _], R](root: AbstractRootSelectQuery[T, R]): Query[T, R, Ungroupped, Unordered, Unlimited,
    Unchainned, AssignUnchainned] = {
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
  implicit def rootInsertQueryToQuery[T <: Table[T, _], R](root: AbstractRootInsertQuery[T, R]): Query[T, R, Ungroupped, Unordered, Unlimited,
    Unchainned, AssignUnchainned] = {
    new Query(
      root.table,
      root.st.into,
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
  implicit def rootInsertQueryToInsertQuery[T <: Table[T, _], R](root: AbstractRootInsertQuery[T, R]): InsertQuery[T, R, Ungroupped, Unordered, Unlimited,
    Unchainned, AssignUnchainned] = {
    new InsertQuery(
      new Query(
        root.table,
        root.st.into,
        root.rowFunc
      )
    )
  }

  implicit def queryToAssignmentsQuery[
    T <: Table[T, _],
    R,
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    C <: ChainBind,
    AC <: AssignBind
  ](query: Query[T, R, G, O, L, C, AC]): AssignmentsQuery[T, R, G, O, L, C, AC] = {
    new AssignmentsQuery(query)
  }

  implicit def assignmentToQuery[
    T <: Table[T, _],
    R,
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    C <: ChainBind,
    AC <: AssignBind
  ](assignment: AssignmentsQuery[T, R, G, O, L, C, AC]): Query[T, R, G, O, L, C, AC] = assignment.query

  implicit def queryToSelectQuery[
    T <: Table[T, _],
    R,
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    C <: ChainBind,
    AC <: AssignBind
  ](query: Query[T, R, G, O, L, C, AC]): SelectQuery[T, R, G, O, L, C, AC] = {
    new SelectQuery(query)
  }

  implicit def selectQuerytToQuery[
    T <: Table[T, _],
    R,
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    C <: ChainBind,
    AC <: AssignBind
  ](assignment: SelectQuery[T, R, G, O, L, C, AC]): Query[T, R, G, O, L, C, AC] = assignment.query

  implicit def queryInsertQuery[
  T <: Table[T, _],
  R,
  G <: GroupBind,
  O <: OrderBind,
  L <: LimitBind,
  C <: ChainBind,
  AC <: AssignBind
  ](query: Query[T, R, G, O, L, C, AC]): InsertQuery[T, R, G, O, L, C, AC] = {
    new InsertQuery(query)
  }

  implicit def insertQuerytToQuery[
    T <: Table[T, _],
    R,
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    C <: ChainBind,
    AC <: AssignBind
  ](assignment: InsertQuery[T, R, G, O, L, C, AC]): Query[T, R, G, O, L, C, AC] = assignment.toQuery
}
