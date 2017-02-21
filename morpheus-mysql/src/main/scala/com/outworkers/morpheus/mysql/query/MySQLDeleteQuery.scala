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
import com.outworkers.morpheus.engine.query.{AssignBind, AssignUnchainned}
import com.outworkers.morpheus.builder.{DefaultSQLSyntax, SQLBuiltQuery}
import com.outworkers.morpheus.mysql._
import com.outworkers.morpheus.engine.query._
import shapeless.{HList, HNil}

case class MySQLDeleteSyntaxBlock(query: String, tableName: String) extends RootDeleteSyntaxBlock(query, tableName) {

  override val syntax = Syntax

  private[this] def deleteOption(option: String, table: String): SQLBuiltQuery = {
    qb.pad.append(option)
      .forcePad.append(DefaultSQLSyntax.from)
      .forcePad.append(table)
  }


  def lowPriority: SQLBuiltQuery = {
    deleteOption(syntax.Priorities.lowPriority, tableName)
  }

  def ignore: SQLBuiltQuery = {
    deleteOption(syntax.DeleteOptions.ignore, tableName)
  }

  def quick: SQLBuiltQuery = {
    deleteOption(syntax.DeleteOptions.quick, tableName)
  }
}

private[morpheus] class MySQLRootDeleteQuery[T <: BaseTable[T, _, Row], R](table: T, st: MySQLDeleteSyntaxBlock, rowFunc: Row => R)
  extends RootDeleteQuery[T, R, Row](table, st, rowFunc) {

  def lowPriority: MySQLDeleteQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new MySQLDeleteQuery(table, st.lowPriority, rowFunc)
  }

  def ignore: MySQLDeleteQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, HNil] = {
    new MySQLDeleteQuery(table, st.ignore, rowFunc)
  }
}

class MySQLDeleteQuery[T <: BaseTable[T, _, Row],
  R,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: HList
](table: T, query: SQLBuiltQuery, rowFunc: Row => R) extends DeleteQuery[T, R, Row, Group, Order, Limit, Chain, AssignChain, Status](table, query,
  rowFunc) {

}
