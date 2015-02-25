/*
 * Copyright 2014 websudos ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.websudos.morpheus.mysql.query

import com.websudos.morpheus.builder.SQLBuiltQuery
import com.websudos.morpheus.mysql._
import com.websudos.morpheus.query._

private[morpheus] class MySQLInsertSyntaxBlock(query: String, tableName: String) extends RootInsertSyntaxBlock(query, tableName) {
  override val syntax = MySQLSyntax

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


class MySQLRootInsertQuery[T <: BaseTable[T, _, MySQLRow], R](table: T, st: MySQLInsertSyntaxBlock, rowFunc: MySQLRow => R)
  extends RootInsertQuery[T, R, MySQLRow](table, st, rowFunc) {

  def delayed: MySQLInsertQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new MySQLInsertQuery(table, st.delayed, rowFunc)
  }

  def lowPriority: MySQLInsertQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new MySQLInsertQuery(table, st.lowPriority, rowFunc)
  }

  def highPriority: MySQLInsertQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new MySQLInsertQuery(table, st.highPriority, rowFunc)
  }

  def ignore: MySQLInsertQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new MySQLInsertQuery(table, st.ignore, rowFunc)
  }

}

class MySQLInsertQuery[T <: BaseTable[T, _, MySQLRow],
  R,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: StatusBind
](table: T, query: SQLBuiltQuery, rowFunc: MySQLRow => R)
  extends InsertQuery[T, R, MySQLRow, Group, Order, Limit, Chain, AssignChain, Status](table: T, query, rowFunc) {

}
