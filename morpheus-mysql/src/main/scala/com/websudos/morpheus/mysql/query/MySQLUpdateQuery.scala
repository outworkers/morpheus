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

import com.websudos.morpheus.builder.SQLBuiltQuery
import com.websudos.morpheus.dsl.BaseTable
import com.websudos.morpheus.mysql._
import com.websudos.morpheus.query._

import scala.annotation.implicitNotFound


case class MySQLUpdateSyntaxBlock(query: String, tableName: String) extends RootUpdateSyntaxBlock(query, tableName) {
  override val syntax = MySQLSyntax

  def lowPriority: SQLBuiltQuery = {
    qb.pad.append(syntax.lowPriority)
      .pad.append(tableName)
  }

  def ignore: SQLBuiltQuery = {
    qb.pad.append(syntax.ignore)
      .pad.append(tableName)
  }
}


class MySQLRootUpdateQuery[T <: BaseTable[T, _, MySQLRow], R](table: T, st: MySQLUpdateSyntaxBlock, rowFunc: MySQLRow => R)
  extends RootUpdateQuery[T, R, MySQLRow](table, st, rowFunc) {

  def lowPriority: MySQLUpdateQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new MySQLUpdateQuery(table, st.lowPriority, rowFunc)
  }

  def ignore: MySQLUpdateQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new MySQLUpdateQuery(table, st.ignore, rowFunc)
  }
}


class MySQLUpdateQuery[T <: BaseTable[T, _, MySQLRow],
  R,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: StatusBind
](table: T, query: SQLBuiltQuery, rowFunc: MySQLRow => R) extends UpdateQuery[T, R, MySQLRow, Group, Order, Limit, Chain, AssignChain, Status](table: T, query,
  rowFunc) {

  @implicitNotFound("You cannot use two where clauses on a single query")
  override def where(condition: T => QueryCondition)(implicit ev: Chain =:= Unchainned): MySQLUpdateQuery[T, R, Group, Order, Limit, Chainned,
    AssignChain, Status] = {
    new MySQLUpdateQuery(table, table.queryBuilder.where(query, condition(table).clause), rowFunc)
  }

  @implicitNotFound("You cannot use two where clauses on a single query")
  override def where(condition: QueryCondition)(implicit ev: Chain =:= Unchainned): MySQLUpdateQuery[T, R, Group, Order, Limit, Chainned,
    AssignChain, Status] = {
    new MySQLUpdateQuery(table, table.queryBuilder.where(query, condition.clause), rowFunc)
  }

  @implicitNotFound("You need to use the where method first")
  override def and(condition: T => QueryCondition)(implicit ev: Chain =:= Chainned): MySQLUpdateQuery[T, R, Group, Order, Limit, Chain,
    AssignChainned, Status]  = {
    new MySQLUpdateQuery(table, table.queryBuilder.and(query, condition(table).clause), rowFunc)
  }

  @implicitNotFound("You need to use the where method first")
  override def and(condition: QueryCondition)(implicit ev: Chain =:= Chainned): MySQLUpdateQuery[T, R, Group, Order, Limit, Chain, AssignChainned,
    Status]  = {
    new MySQLUpdateQuery(table, table.queryBuilder.and(query, condition.clause), rowFunc)
  }

  @implicitNotFound("You can't use 2 SET parts on a single UPDATE query")
  override def set(condition: T => QueryAssignment)(implicit ev: AssignChain =:= AssignUnchainned, ev1: Status =:= Unterminated): MySQLUpdateQuery[T, R,
    Group, Order, Limit, Chain, AssignChainned, Status] = {
    new MySQLUpdateQuery(
      table,
      table.queryBuilder.set(query, condition(table).clause),
      rowFunc
    )
  }

  @implicitNotFound("""You need to use the "set" method before using the "and"""")
  override def andSet(condition: T => QueryAssignment, signChange: Int = 0)(implicit ev: AssignChain =:= AssignChainned): MySQLUpdateQuery[T, R,
    Group, Order, Limit, Chain, AssignChainned, Status] = {
    new MySQLUpdateQuery(
      table,
      table.queryBuilder.andSet(query, condition(table).clause),
      rowFunc
    )
  }

  private[morpheus] override def terminate: MySQLUpdateQuery[T, R, Group, Order, Limit, Chain, AssignChainned, Terminated] = {
    new MySQLUpdateQuery(
      table,
      query,
      rowFunc
    )
  }
}
