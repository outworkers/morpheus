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
  def append(st: String): SQLBuiltQuery = new SQLBuiltQuery(queryString + st)
  def append(st: SQLBuiltQuery): SQLBuiltQuery = new SQLBuiltQuery(queryString + st.queryString)

  def prepend(st: String): SQLBuiltQuery = new SQLBuiltQuery(st + queryString)
  def prepend(st: SQLBuiltQuery): SQLBuiltQuery = new SQLBuiltQuery(st.queryString + queryString)

  def spaced: Boolean = queryString.endsWith(" ")
  def pad: SQLBuiltQuery = if (spaced) this else SQLBuiltQuery(queryString + " ")
}

object DefaultSQLOperators {
  val select = SQLBuiltQuery("SELECT")
}

trait SQLQuery extends ResultSetOperations {
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


trait SQLResultsQuery[T <: Table[T, _], R] extends SQLQuery {
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


abstract class BaseSelectQuery[T <: Table[T, _], R](table: T, protected[morpheus] val query: SQLBuiltQuery, rowFunc: Row => R) extends SQLResultsQuery[T, R] {
  def fromRow(r: Row): R = rowFunc(r)

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

  protected[this] def clause(condition: T => QueryCondition): SelectWhere[T, R] = {
    new SelectWhere[T, R](table, table.queryBuilder.where(query, condition(table).clause), rowFunc)
  }

  protected[this] def andClause(condition: T => QueryCondition): SelectWhere[T, R] = {
    new SelectWhere[T, R](table, table.queryBuilder.and(query, condition(table).clause), rowFunc)
  }

}

class RootSelectQuery[T <: Table[T, _], R](val table: T, val st: SelectSyntaxBlock[T, _], val rowFunc: Row => R) {

  def fromRow(r: Row): R = rowFunc(r)

  def distinct: SelectQuery[T, R] = {
    new SelectQuery(table, st.distinct, rowFunc)
  }

  def distinctRow: SelectQuery[T, R] = {
    new SelectQuery(table, st.distinctRow, rowFunc)
  }
}

class SelectQuery[T <: Table[T, _], R](table: T, query: SQLBuiltQuery, rowFunc: Row => R) extends BaseSelectQuery[T, R](table, query, rowFunc) {

  def where(condition: T => QueryCondition): SelectWhere[T, R] = clause(condition)
}

class SelectWhere[T <: Table[T, _], R](table: T, query: SQLBuiltQuery, rowFunc: Row => R) extends BaseSelectQuery[T, R](table, query, rowFunc) {
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
