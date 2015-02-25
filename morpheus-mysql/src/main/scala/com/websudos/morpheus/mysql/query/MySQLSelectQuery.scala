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

package com.websudos.morpheus.mysql.query

import com.twitter.util.Future
import com.websudos.morpheus.Client
import com.websudos.morpheus.builder.SQLBuiltQuery
import com.websudos.morpheus.mysql._
import com.websudos.morpheus.query._

import scala.annotation.implicitNotFound
import scala.concurrent.{Future => ScalaFuture}


private[morpheus] class MySQLSelectSyntaxBlock(
  query: String, tableName: String,
  columns: List[String] = List("*")) extends AbstractSelectSyntaxBlock(query, tableName, columns) {
  override val syntax = MySQLSyntax

  def distinctRow: SQLBuiltQuery = {
    qb.pad.append(syntax.distinctRow)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.appendEscape(tableName)
  }

  def highPriority: SQLBuiltQuery = {
    qb.pad.append(syntax.highPriority)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.appendEscape(tableName)
  }

  def straightJoin: SQLBuiltQuery = {
    qb.pad.append(syntax.straightJoin)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.appendEscape(tableName)
  }

  def sqlSmallResult: SQLBuiltQuery = {
    qb.pad.append(syntax.sqlSmallResult)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.appendEscape(tableName)
  }

  def sqlBigResult: SQLBuiltQuery = {
    qb.pad.append(syntax.sqlBigResult)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.appendEscape(tableName)
  }

  def sqlBufferResult: SQLBuiltQuery = {
    qb.pad.append(syntax.sqlBufferResult)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.appendEscape(tableName)
  }

  def sqlCache: SQLBuiltQuery = {
    qb.pad.append(syntax.sqlCache)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.appendEscape(tableName)
  }

  def sqlNoCache: SQLBuiltQuery = {
    qb.pad.append(syntax.sqlNoCache)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.appendEscape(tableName)
  }

  def sqlCalcFoundRows: SQLBuiltQuery = {
    qb.pad.append(syntax.sqlCalcFoundRows)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.appendEscape(tableName)
  }
}


private[morpheus] class MySQLRootSelectQuery[T <: BaseTable[T, _, MySQLRow], R](table: T, st: MySQLSelectSyntaxBlock, rowFunc: MySQLRow => R)
  extends AbstractRootSelectQuery[T, R, MySQLRow](table, st, rowFunc) {

  type BaseSelectQuery = MySQLSelectQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated]

  def distinctRow: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.distinctRow, rowFunc)
  }

  def highPriority: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.highPriority, rowFunc)
  }

  def straightJoin: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.straightJoin, rowFunc)
  }

  def sqlSmallResult: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.sqlSmallResult, rowFunc)
  }

  def sqlBigResult: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.sqlBigResult, rowFunc)
  }

  def sqlBufferResult: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.sqlBufferResult, rowFunc)
  }

  def sqlCache: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.sqlCache, rowFunc)
  }

  def sqlNoCache: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.sqlNoCache, rowFunc)
  }

  def sqlCalcFoundRows: BaseSelectQuery = {
    new MySQLSelectQuery(table, st.sqlCalcFoundRows, rowFunc)
  }
}


class MySQLSelectQuery[T <: BaseTable[T, _, MySQLRow],
  R,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: StatusBind
](table: T, query: SQLBuiltQuery, rowFunc: MySQLRow => R)
  extends SelectQuery[T, R, MySQLRow, Group, Order, Limit, Chain, AssignChain, Status](table, query, rowFunc)
  with SQLResultsQuery[T, R, MySQLRow, MySQLResult, Limit] {

  @implicitNotFound("You cannot use two where clauses on a single query")
  override def where(condition: T => QueryCondition)(implicit ev: Chain =:= Unchainned): MySQLSelectQuery[T, R, Group, Order, Limit, Chainned,
    AssignChain, Status] = {
    new MySQLSelectQuery(table, table.queryBuilder.where(query, condition(table).clause), rowFunc)
  }

  @implicitNotFound("You cannot use two where clauses on a single query")
  override def where(condition: QueryCondition)(implicit ev: Chain =:= Unchainned): MySQLSelectQuery[T, R, Group, Order, Limit, Chainned,
    AssignChain, Status] = {
    new MySQLSelectQuery(table, table.queryBuilder.where(query, condition.clause), rowFunc)
  }

  @implicitNotFound("You need to use the where method first")
  override def and(condition: T => QueryCondition)(implicit ev: Chain =:= Chainned): MySQLSelectQuery[T, R, Group, Order, Limit, Chain,
    AssignChainned, Status]  = {
    new MySQLSelectQuery(table, table.queryBuilder.and(query, condition(table).clause), rowFunc)
  }

  @implicitNotFound("You need to use the where method first")
  override def and(condition: QueryCondition)(implicit ev: Chain =:= Chainned): MySQLSelectQuery[T, R, Group, Order, Limit, Chain, AssignChainned,
    Status] = {
    new MySQLSelectQuery(table, table.queryBuilder.and(query, condition.clause), rowFunc)
  }

  /**
   * Returns the first row from the select ignoring everything else.
   * @param client The MySQL client in use.
   * @return
   */
  override def one()(implicit client: Client[MySQLRow, MySQLResult], ev: Limit =:= Unlimited): ScalaFuture[Option[R]] = {
    twitterToScala(get)
  }

  /**
   * Get the result of an operation as a Twitter Future.
   * @param client The MySQL client in use.
   * @return A Twitter future wrapping the result.
   */
  override def get()(implicit client: Client[MySQLRow, MySQLResult], ev: Limit =:= Unlimited): Future[Option[R]] = {
    client.select(limit(1).queryString)(fromRow) map (_.headOption)
  }
}
