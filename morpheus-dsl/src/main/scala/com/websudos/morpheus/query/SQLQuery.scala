/*
 *
 *  * Copyright 2014 websudos ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.websudos.morpheus.query

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future => ScalaFuture}

import com.twitter.finagle.exp.mysql.{Client, Result, Row}
import com.twitter.util.Future
import com.websudos.morpheus.dsl.{ResultSetOperations, Table}

case class SQLBuiltQuery(queryString: String) {
  def append(st: String): SQLBuiltQuery = SQLBuiltQuery(queryString + st)
  def append(st: SQLBuiltQuery): SQLBuiltQuery = append(st.queryString)

  def prepend(st: String): SQLBuiltQuery = SQLBuiltQuery(st + queryString)
  def prepend(st: SQLBuiltQuery): SQLBuiltQuery = prepend(st.queryString)

  def prependIfAbsent(st: String): SQLBuiltQuery = if (queryString.startsWith(st)) {
    this
  } else {
    prepend(st)
  }

  def prependIfAbsent(st: SQLBuiltQuery): SQLBuiltQuery = prependIfAbsent(st.queryString)

  def appendIfAbsent(st: String): SQLBuiltQuery = if (queryString.endsWith(st)) {
    this
  } else {
    append(st)
  }

  def appendIfAbsent(st: SQLBuiltQuery): SQLBuiltQuery = appendIfAbsent(st.queryString)

  def removeIfLast(st: SQLBuiltQuery): SQLBuiltQuery = if (queryString.endsWith(st.queryString)) {
    SQLBuiltQuery(queryString.dropRight(st.queryString.length))
  } else  {
    this
  }

  def spaced: Boolean = queryString.endsWith(" ")
  def pad: SQLBuiltQuery = if (spaced) this else SQLBuiltQuery(queryString + " ")
}

object DefaultSQLOperators {
  val select = SQLBuiltQuery("SELECT")
  val where = SQLBuiltQuery("WHERE")
  val and = SQLBuiltQuery("AND")
  val in = SQLBuiltQuery("IN")
  val `(` = SQLBuiltQuery("(")
  val `)` = SQLBuiltQuery(")")
}

trait SQLQuery[T <: Table[T, _], R] extends ResultSetOperations {
  protected[morpheus] val query: SQLBuiltQuery

  /**
   * A simple forwarding method to prevent some extra boiler-plate during tests.
   * This will serialise an existing query to the relevant SQL string.
   * @return A string representing the query encoded in SQL.
   */
  def queryString: String = query.queryString

  /**
   * This method is used when the query is not returning a data result, such as an UPDATE query.
   * While it will accurately monitor the execution of the query and the Future will complete when the task is "done" in SQL,
   * the type-safe mapping of the result is not necessary at this point.
   *
   * This method duplicates the API to provide an alternative to people who don't use Twitter Futures or any Twitter libraries in their stack. Until now
   * anyway. Twitter has been gossiping about making com.twitter.util.Future extend scala.concurrent.Future, but until such times a dual API is best.
   *
   * @param client The Finagle MySQL client in the scope of which to execute the query.
   * @return A Scala Future wrapping a default Finagle MySQL query result object.
   */
  def future()(implicit client: Client): ScalaFuture[Result] = {
    queryToScalaFuture(query.queryString)
  }

  /**
   * This method is used when the query is not returning a data result, such as an UPDATE query.
   * While it will accurately monitor the execution of the query and the Future will complete when the task is "done" in SQL,
   * the type-safe mapping of the result is not necessary at this point.
   *
   * @param client The Finagle MySQL client in the scope of which to execute the query.
   * @return A Scala Future wrapping a default Finagle MySQL query result object.
   */
  def execute()(implicit  client: Client): Future[Result] = {
    queryToFuture(query.queryString)
  }

}


trait SQLResultsQuery[T <: Table[T, _], R] extends SQLQuery[T, R] {
  def fromRow(r: Row): R

  /**
   * Returns the first row from the select ignoring everything else.
   * @param client The MySQL client in use.
   * @return
   */
  def one()(implicit client: Client): ScalaFuture[Option[R]]

  /**
   * Get the result of an operation as a Twitter Future.
   * @param client The MySQL client in use.
   * @return A Twitter future wrapping the result.
   */
  def get()(implicit client: Client): Future[Option[R]]
}


/**
 * This bit of magic allows all extending sub-classes to implement the "where" and "and" SQL clauses with all the necessary operators,
 * in a type safe way. By providing the third type argument and a custom way to subclass with the predetermined set of arguments, all queries such as UPDATE,
 * DELETE, ALTER and so on can use the same root implementation of clauses and void the violation of Dry.
 *
 * The reason why the "clause" and "andClause" methods below are protected is so that extending classes can decide when and how to expose "where" and "and"
 * SQL methods to the DSL user. Used mainly to make queries like "select.where(_.a = b).where(_.c = d)" impossible,
 * or in other words make illegal programming states unrepresentable. There is an awesome book about how to do this in Scala,
 * I will link to it as soon as the book is published.
 *
 * @param table The table owning the record.
 * @param query The root SQL query to start building from.
 * @param rowFunc The function mapping a row to a record.
 * @tparam T The type of the table owning the record.
 * @tparam R The type of the record held in the table.
 * @tparam QueryType The query type to subclass with and obtain as a result of a "where" or "and" application, requires all extending subclasses to supply a
 *                   type that will subclass an SQLQuery[T, R]
*/
private[morpheus] abstract class WhereQuery[T <: Table[T, _], R, QueryType <: SQLQuery[T, R]](table: T, query: SQLBuiltQuery, rowFunc: Row => R) {
  protected[this] def subclass(table: T, query: SQLBuiltQuery, rowFunc: Row => R): QueryType

  def fromRow(row: Row): R = rowFunc(row)

  protected[this] def clause(condition: T => QueryCondition): QueryType = {
    subclass(table, table.queryBuilder.where(query, condition(table).clause), rowFunc)
  }

  protected[this] def andClause(condition: T => QueryCondition): QueryType = {
    subclass(table, table.queryBuilder.and(query, condition(table).clause), rowFunc)
  }

}


trait BaseSelectQuery[T <: Table[T, _], R] extends SQLResultsQuery[T, R] {

  def fetch()(implicit client: Client): ScalaFuture[Seq[R]] = {
    twitterToScala(client.select(query.queryString)(fromRow))
  }

  def collect()(implicit client: Client): Future[Seq[R]] = {
    client.select(query.queryString)(fromRow)
  }

  def one()(implicit client: Client): ScalaFuture[Option[R]] = {
    fetch.map(_.headOption)
  }

  def get()(implicit client: Client): Future[Option[R]] = {
    collect().map(_.headOption)
  }

}

/**
 * This is the implementation of a root select query, a wrapper around an abstract syntax block.
 * The basic select of select methods can be seen in {@link com.websudos.morpheus.dsl.SelectTable}
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
class RootSelectQuery[T <: Table[T, _], R](val table: T, val st: SelectSyntaxBlock[T, _], val rowFunc: Row => R) {

  def fromRow(r: Row): R = rowFunc(r)

  def distinct: SelectQuery[T, R] = {
    new SelectQuery(table, st.distinct, rowFunc)
  }

  def distinctRow: SelectQuery[T, R] = {
    new SelectQuery(table, st.distinctRow, rowFunc)
  }

  def all: SelectQuery[T, R] = {
    new SelectQuery(table, st.*, rowFunc)
  }

}

class SelectQuery[T <: Table[T, _], R](table: T, val query: SQLBuiltQuery, rowFunc: Row => R) extends WhereQuery[T, R, SelectWhere[T,
  R]](table, query, rowFunc) with BaseSelectQuery[T, R] {


  protected[this] def subclass(table: T, query: SQLBuiltQuery, rowFunc: Row => R): SelectWhere[T, R] = new SelectWhere[T, R](table, query, rowFunc)

  def where(condition: T => QueryCondition): SelectWhere[T, R] = clause(condition)
}

class SelectWhere[T <: Table[T, _], R](table: T, val query: SQLBuiltQuery, rowFunc: Row => R) extends WhereQuery[T, R, SelectWhere[T,
  R]](table, query, rowFunc) with BaseSelectQuery[T, R] {

  protected[this] def subclass(table: T, query: SQLBuiltQuery, rowFunc: Row => R): SelectWhere[T, R] = new SelectWhere[T, R](table, query, rowFunc)

  def and(condition: T => QueryCondition): SelectWhere[T, R] = andClause(condition)
}


private[morpheus] trait SelectImplicits {

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
  implicit def rootSelectQueryToSelectQuery[T <: Table[T, _], R](root: RootSelectQuery[T, R]): SelectQuery[T, R] = {
    new SelectQuery[T, R](
      root.table,
      root.st.*,
      root.rowFunc
    )
  }
}
