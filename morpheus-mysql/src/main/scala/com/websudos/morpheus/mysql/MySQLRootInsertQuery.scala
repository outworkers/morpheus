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

package com.websudos.morpheus.mysql

import com.websudos.morpheus.query.{RootInsertSyntaxBlock, SQLBuiltQuery, _}

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


private[morpheus] class MySQLRootInsertQuery[T <: BaseTable[T, _], R](table: T, st: MySQLInsertSyntaxBlock, rowFunc: Row => R) extends RootInsertQuery[T,
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
