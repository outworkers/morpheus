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

import scala.concurrent.{Future => ScalaFuture}

import com.twitter.finagle.exp.mysql.{Client, Result, Row}
import com.twitter.util.Future
import com.websudos.morpheus.dsl.{ResultSetOperations, Table}
import scala.annotation.implicitNotFound

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
  def forcePad: SQLBuiltQuery = SQLBuiltQuery(queryString + " ")
}

object DefaultSQLOperators {
  val select = "SELECT"
  val distinct = "DISTINCT"
  val lowPriority = "LOW_PRIORITY"
  val ignore = "IGNORE"
  val quick = "QUICK"
  val distinctRow = "DISTINCTROW"
  val where = "WHERE"
  val having = "HAVING"
  val update = "UPDATE"
  val delete = "DELETE"
  val orderBy = "ORDER BY"
  val limit = "LIMIT"
  val and = "AND"
  val or = "OR"
  val set = "SET"
  val in = "IN"
  val from = "FROM"
  val setTo = "setTo"
  val eqs = "="
  val `(` = "("
  val comma = ","
  val `)` = ")"
  val asc = "ASC"
  val desc = "DESC"
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

trait GroupBind
trait ChainBind
trait OrderBind
trait LimitBind

abstract class Groupped extends GroupBind
abstract class Ungroupped extends GroupBind
abstract class Chainned extends ChainBind
abstract class Unchainned extends ChainBind
abstract class Ordered extends OrderBind
abstract class Unordered extends OrderBind
abstract class Limited extends LimitBind
abstract class Unlimited extends LimitBind

/**
 * This bit of magic allows all extending sub-classes to implement the "where" and "and" SQL clauses with all the necessary operators,
 * in a type safe way. By providing the third type argument and a custom way to subclass with the predetermined set of arguments, all queries such as UPDATE,
 * DELETE, ALTER and so on can use the same root implementation of clauses and therefore avoid the violation of DRY.
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
*/
class Query[
  T <: Table[T, _],
  R,
  Group <: GroupBind,
  Ord <: OrderBind,
  Lim <: LimitBind,
  Chain <: ChainBind,
  AC <: AssignBind
](val table: T, val query: SQLBuiltQuery, val rowFunc: Row => R) extends SQLQuery[T, R] {

  def fromRow(row: Row): R = rowFunc(row)

  @implicitNotFound("You cannot use two where clauses on a single query")
  def where(condition: T => QueryCondition)(implicit ev: Chain =:= Unchainned): Query[T, R, Group, Ord, Lim, Chainned, AC] = {
    new Query(table, table.queryBuilder.where(query, condition(table).clause), rowFunc)
  }

  @implicitNotFound("You cannot set two limits on the same query")
  def limit(value: Int)(implicit ev: Lim =:= Unlimited): Query[T, R, Group, Ord, Limited, Chain, AC] = {
    new Query(table, table.queryBuilder.limit(query, value.toString), rowFunc)
  }

  @implicitNotFound("You cannot order a query twice")
  def orderBy(conditions: (T => QueryOrder)*)(implicit ev: Ord =:= Unordered): Query[T, R, Group, Ordered, Lim, Chain, AC] = {
    val applied = conditions map {
      fn => fn(table).clause
    }
    new Query(table, table.queryBuilder.orderBy(query, applied), rowFunc)
  }

  @implicitNotFound("You need to use the where method first")
  def and(condition: T => QueryCondition)(implicit ev: Chain =:= Chainned): Query[T, R, Group, Ord, Lim, Chainned, AC]  = {
    new Query(table, table.queryBuilder.and(query, condition(table).clause), rowFunc)
  }

}
