package com.websudos.morpheus.query

import com.twitter.finagle.exp.mysql.Row
import com.websudos.morpheus.SQLPrimitive
import com.websudos.morpheus.column.AbstractColumn
import com.websudos.morpheus.dsl.Table
import com.websudos.morpheus.mysql.MySQLSyntax


private[morpheus] abstract class AbstractInsertSyntaxBlock(query: String, tableName: String) extends AbstractSyntaxBlock {

  protected[this] val qb = SQLBuiltQuery(query)

  def into: SQLBuiltQuery = {
    qb.forcePad.append(syntax.into)
      .forcePad.append(tableName)
  }

}

private[morpheus] class MySQLInsertSyntaxBlock(query: String, tableName: String) extends AbstractInsertSyntaxBlock(query, tableName) {
  val syntax = MySQLSyntax

  def delayed: SQLBuiltQuery = {
    qb.pad.append(syntax.delayed)
      .forcePad.append(syntax.into)
      .forcePad.append(tableName)
  }

  def lowPriority: SQLBuiltQuery = {
    qb.pad.append(syntax.lowPriority)
      .forcePad.append(syntax.into)
      .forcePad.append(tableName)
  }

  def highPriority: SQLBuiltQuery = {
    qb.pad.append(syntax.highPriority)
      .forcePad.append(syntax.into)
      .forcePad.append(tableName)
  }

  def ignore: SQLBuiltQuery = {
    qb.pad.append(syntax.ignore)
      .forcePad.append(syntax.into)
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
private[morpheus] abstract class AbstractRootInsertQuery[T <: Table[T, _], R](val table: T, val st: AbstractInsertSyntaxBlock, val rowFunc: Row => R) {

  def fromRow(r: Row): R = rowFunc(r)

  private[morpheus] def into: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.into, rowFunc)
  }
}

private[morpheus] class MySQLRootInsertQuery[T <: Table[T, _], R](table: T, st: MySQLInsertSyntaxBlock, rowFunc: Row => R) extends AbstractRootInsertQuery[T,
  R](table, st, rowFunc) {

  def delayed: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.delayed, rowFunc)
  }

  def lowPriority: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.lowPriority, rowFunc)
  }

  def highPriority: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.highPriority, rowFunc)
  }

  def ignore: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.ignore, rowFunc)
  }

}


class InsertQuery[
  T <: Table[T, _],
  R,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind
  ](val query: Query[T, R, Group, Order, Limit, Chain, AssignChain], val statements: List[(String, String)] = Nil) {


  def value[RR : SQLPrimitive](condition: T => AbstractColumn[RR], obj: RR): InsertQuery[T, R, Group, Order, Limit, Chain, AssignChain] = {
    new InsertQuery(query, Tuple2(condition(query.table).name, implicitly[SQLPrimitive[RR]].toSQL(obj)) :: statements)
  }

  private[morpheus] def toQuery: Query[T, R, Group, Order, Limit, Chain, AssignChain] = {


    val columns = statements.reverse.map(_._1)
    val values = statements.reverse.map(_._2)

    new Query(query.table, query.table.queryBuilder.insert(query.query, columns, values), query.fromRow)
  }


}
