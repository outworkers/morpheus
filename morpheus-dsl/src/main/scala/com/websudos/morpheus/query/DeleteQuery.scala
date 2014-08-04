package com.websudos.morpheus.query

import com.websudos.morpheus.dsl.Table
import com.twitter.finagle.exp.mysql.Row

case class DeleteSyntaxBlock[T <: Table[T, _], R](query: String, tableName: String, fromRow: Row => R, columns: List[String] = List("*")) {

  private[this] val qb = SQLBuiltQuery(query)

  def all: SQLBuiltQuery = {
    qb.pad.append(DefaultSQLOperators.from)
      .forcePad.append(tableName)
  }

  def lowPriority: SQLBuiltQuery = {
    qb.pad.append(DefaultSQLOperators.lowPriority)
      .forcePad.append(DefaultSQLOperators.from)
      .forcePad.append(tableName)
  }

  def ignore: SQLBuiltQuery = {
    qb.pad.append(DefaultSQLOperators.ignore)
      .forcePad.append(DefaultSQLOperators.from)
      .forcePad.append(tableName)
  }

  def quick: SQLBuiltQuery = {
    qb.pad.append(DefaultSQLOperators.quick)
      .forcePad.append(DefaultSQLOperators.from)
      .forcePad.append(tableName)
  }
}

/**
 * This is the implementation of a root UPDATE query, a wrapper around an abstract syntax block.
 *
 * This is used as the entry point to an SQL query, and it requires the user to provide "one more method" to fully specify a SELECT query.
 * The implicit conversion from a RootSelectQuery to a SelectQuery will automatically pick the "all" strategy below.
 *
 * @param table The table owning the record.
 * @param st The Abstract syntax block describing the possible decisions.
 * @param rowFunc The function used to map a result to a type-safe record.
 * @tparam T The type of the owning table.
 * @tparam R The type of the record.
 */
private[morpheus] class RootDeleteQuery[T <: Table[T, _], R](val table: T, val st: DeleteSyntaxBlock[T, _], val rowFunc: Row => R) {

  def fromRow(r: Row): R = rowFunc(r)

  def lowPriority: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.lowPriority, rowFunc)
  }

  def ignore: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.ignore, rowFunc)
  }

  private[morpheus] def all: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.all, rowFunc)
  }
}
