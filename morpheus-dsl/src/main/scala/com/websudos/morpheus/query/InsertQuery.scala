package com.websudos.morpheus.query

import scala.annotation.implicitNotFound

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

  private[morpheus] def into: Query[T, R, InsertType, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new Query(table, st.into, rowFunc)
  }
}

private[morpheus] class MySQLRootInsertQuery[T <: Table[T, _], R](table: T, st: MySQLInsertSyntaxBlock, rowFunc: Row => R) extends AbstractRootInsertQuery[T,
  R](table, st, rowFunc) {

  def delayed: Query[T, R, InsertType, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new Query(table, st.delayed, rowFunc)
  }

  def lowPriority: Query[T, R, InsertType, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new Query(table, st.lowPriority, rowFunc)
  }

  def highPriority: Query[T, R, InsertType, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new Query(table, st.highPriority, rowFunc)
  }

  def ignore: Query[T, R, InsertType, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new Query(table, st.ignore, rowFunc)
  }

}


class InsertQuery[
  T <: Table[T, _],
  R,
  Type <: QueryType,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: StatusBind
  ](val query: Query[T, R, Type, Group, Order, Limit, Chain, AssignChain, Status], val statements: List[(String, String)] = Nil) {


  /**
   * At this point you may be reading and thinking "WTF", but fear not, it all makes sense. Every call to a "value method" will generate a new Insert Query,
   * but the list of statements in the new query will include a new (String, String) pair, where the first part is the column name and the second one is the
   * serialised value. This is a very simple accumulator that will eventually allow calling the "insert" method on a queryBuilder to produce the final
   * serialisation result, a hopefully valid MySQL insert query.
   *
   * We even try to get really clever here and terminate the query if the number of assignments in the accumulator exceeds the number of columns available in
   * the table, essentially preventing multiple assignments per table. Although we have no way of guaranteeing a single assignment was made for every
   * individual column, it's still a good way to force out some invalid queries.
   *
   * @param insertion The insert condition is a pair of a column with the value to use for it. It looks like this: value(_.someColumn, someValue),
   *                  where the assignment is of course type safe.
   * @param obj The object is the value to use for the column.
   * @param primitive The primitive is the SQL primitive that converts the object into an SQL Primitive. Since the user cannot deal with types that are not
   *                  "marked" as SQL primitives for the particular database in use, we use a simple context bound to enforce this constraint.
   * @param ev The first evidence parameter is a restriction upon the Type phantom type and it tests if the Query is an Insert query. This prevents the user
   *           from jumping around with the implicit conversion mechanism and converting an Update query to an implicit and so on. It also allows us to
   *           guarantee the MySQL syntax is followed with respect to what methods are available on certain types of queries, all with a single Query class.
   * @param ev1 The second evidence parameter is a restriction upon the status of a Query. Certain "exit" points mark the serialisation as Terminated with
   *            respect to the SQL syntax in use. It's a way of saying: there are no further options possible according to the DB you are using.
   * @tparam RR The SQL primitive or rather it's Scala correspondent to use at this time.
   * @return A new InsertQuery, where the list of statements in the Insert has been chained and updated for serialisation.
   */
  @implicitNotFound(msg = "To use the value method this query needs to be an insert query and the query needs to be unterminated.")
  def value[RR](insertion: T => AbstractColumn[RR], obj: RR)(
    implicit primitive: SQLPrimitive[RR],
    ev: Type =:= InsertType,
    ev1: Status =:= Unterminated
    ): InsertQuery[T, R, Type, Group, Order, Limit, Chain, AssignChain, _ <: StatusBind] = {

    // If the number of statements in the accumulator is equal to the number of columns in the table means no more "value" assignments are possible.
    if (statements.size == query.table.columns.size) {
      new InsertQuery[T, R, Type, Group, Order, Limit, Chain, AssignChain, Terminated](query, Tuple2(insertion(query.table).name,
        primitive.toSQL(obj)) :: statements)
    } else {
      new InsertQuery[T, R, Type, Group, Order, Limit, Chain, AssignChain, Unterminated](query, Tuple2(insertion(query.table).name,
        primitive.toSQL(obj)) :: statements)
    }


  }

  /**
   * This is the final end point of an insert query, where through an implicit conversion in ModifyImplicts, the query is converted back to a normal Query
   * for execution or serialisation. Before any of that however, this method will carefully Terminate the query so that no further implicit conversions are
   * possible.
   *
   * E.g the user cannot go back and re-add value methods or any more things after the query is complete with respect to the MySQL syntax. Talk about making
   * illegal programming states unrepresentable.
   * @return A terminat Query, ready for execution.
   */
  private[morpheus] def toQuery: Query[T, R, Type, Group, Order, Limit, Chain, AssignChain, Terminated] = {

    val columns = statements.reverse.map(_._1)
    val values = statements.reverse.map(_._2)

    new Query(query.table, query.table.queryBuilder.insert(query.query, columns, values), query.fromRow)
  }


}
