/*
 * Copyright 2013 - 2017 Outworkers, Limited.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.outworkers.morpheus.mysql.query

import com.outworkers.morpheus.mysql.{Row, Syntax}
import com.outworkers.morpheus.engine.query._
import com.outworkers.morpheus.builder.SQLBuiltQuery
import com.outworkers.morpheus.mysql._
import com.outworkers.morpheus.engine
import shapeless.{HList, HNil}

import scala.annotation.implicitNotFound


case class UpdateSyntaxBlock(query: String, tableName: String) extends engine.query.RootUpdateSyntaxBlock(query, tableName) {
  override val syntax = Syntax

  def lowPriority: SQLBuiltQuery = {
    qb.pad.append(syntax.Priorities.lowPriority)
      .pad.append(tableName)
  }

  def ignore: SQLBuiltQuery = {
    qb.pad.append(syntax.ignore)
      .pad.append(tableName)
  }
}


class MySQLRootUpdateQuery[T <: BaseTable[T, _, Row], R](table: T, st: UpdateSyntaxBlock, rowFunc: Row => R)
  extends engine.query.RootUpdateQuery[T, R, Row](table, st, rowFunc) {

  def lowPriority: UpdateQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new UpdateQuery(table, st.lowPriority, rowFunc)
  }

  def ignore: UpdateQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new UpdateQuery(table, st.ignore, rowFunc)
  }
}


class UpdateQuery[T <: BaseTable[T, _, Row],
  R,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: HList
](
  table: T,
  query: SQLBuiltQuery,
  rowFunc: Row => R
) extends engine.query.UpdateQuery[T, R, Row, Group, Order, Limit, Chain, AssignChain, Status](table: T, query, rowFunc) {

  override def where(condition: T => QueryCondition)(
    implicit ev: Chain =:= Unchainned
  ): UpdateQuery[T, R, Group, Order, Limit, Chainned, AssignChain, Status] = {
    new UpdateQuery(table, table.queryBuilder.where(query, condition(table).clause), rowFunc)
  }

  override def where(condition: QueryCondition)(implicit ev: Chain =:= Unchainned): UpdateQuery[T, R, Group, Order, Limit, Chainned,
    AssignChain, Status] = {
    new UpdateQuery(table, table.queryBuilder.where(query, condition.clause), rowFunc)
  }

  override def and(condition: T => QueryCondition)(implicit ev: Chain =:= Chainned): UpdateQuery[T, R, Group, Order, Limit, Chain,
    AssignChainned, Status]  = {
    new UpdateQuery(table, table.queryBuilder.and(query, condition(table).clause), rowFunc)
  }

  override def and(condition: QueryCondition)(implicit ev: Chain =:= Chainned): UpdateQuery[T, R, Group, Order, Limit, Chain, AssignChainned,
    Status]  = {
    new UpdateQuery(table, table.queryBuilder.and(query, condition.clause), rowFunc)
  }

  override def set(condition: T => QueryAssignment)(implicit ev: AssignChain =:= AssignUnchainned, ev1: Status =:= HNil): UpdateQuery[T, R,
    Group, Order, Limit, Chain, AssignChainned, Status] = {
    new UpdateQuery(
      table,
      table.queryBuilder.set(query, condition(table).clause),
      rowFunc
    )
  }

  @implicitNotFound("""You need to use the "set" method before using the "and"""")
  override def andSet(condition: T => QueryAssignment, signChange: Int = 0)(implicit ev: AssignChain =:= AssignChainned): UpdateQuery[T, R,
    Group, Order, Limit, Chain, AssignChainned, Status] = {
    new UpdateQuery(
      table,
      table.queryBuilder.andSet(query, condition(table).clause),
      rowFunc
    )
  }
}
