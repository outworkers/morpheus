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

package com.websudos.morpheus.mysql

import com.twitter.finagle.exp.mysql.Row
import com.websudos.morpheus.dsl.Table
import com.websudos.morpheus.query._


private[morpheus] class MySQLSelectSyntaxBlock(
                                                query: String, tableName: String,
                                                columns: List[String] = List("*")) extends AbstractSelectSyntaxBlock(query, tableName, columns) {

  val syntax = MySQLSyntax

  def distinctRow: SQLBuiltQuery = {
    qb.pad.append(syntax.distinctRow)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.append(tableName)
  }

  def highPriority: SQLBuiltQuery = {
    qb.pad.append(syntax.highPriority)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.append(tableName)
  }

  def straightJoin: SQLBuiltQuery = {
    qb.pad.append(syntax.straightJoin)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.append(tableName)
  }
  def sqlSmallResult: SQLBuiltQuery = {
    qb.pad.append(syntax.sqlSmallResult)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.append(tableName)
  }

  def sqlBigResult: SQLBuiltQuery = {
    qb.pad.append(syntax.sqlBigResult)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.append(tableName)
  }

  def sqlBufferResult: SQLBuiltQuery = {
    qb.pad.append(syntax.sqlBufferResult)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.append(tableName)
  }

  def sqlCache: SQLBuiltQuery = {
    qb.pad.append(syntax.sqlCache)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.append(tableName)
  }

  def sqlNoCache: SQLBuiltQuery = {
    qb.pad.append(syntax.sqlNoCache)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.append(tableName)
  }

  def sqlCalcFoundRows: SQLBuiltQuery = {
    qb.pad.append(syntax.sqlCalcFoundRows)
      .pad.append(columns.mkString(", "))
      .pad.append(syntax.from)
      .pad.append(tableName)
  }
}


private[morpheus] class MySQLRootSelectQuery[T <: Table[T, _], R](table: T, st: MySQLSelectSyntaxBlock, rowFunc: Row => R)
  extends AbstractRootSelectQuery[T, R](table, st, rowFunc) {

  type BaseSelectQuery = Query[T, R, SelectType, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated]

  def distinctRow: BaseSelectQuery = {
    new Query(table, st.distinctRow, rowFunc)
  }

  def highPriority: BaseSelectQuery = {
    new Query(table, st.highPriority, rowFunc)
  }

  def straightJoin: BaseSelectQuery = {
    new Query(table, st.straightJoin, rowFunc)
  }

  def sqlSmallResult: BaseSelectQuery = {
    new Query(table, st.sqlSmallResult, rowFunc)
  }

  def sqlBigResult: BaseSelectQuery = {
    new Query(table, st.sqlBigResult, rowFunc)
  }

  def sqlBufferResult: BaseSelectQuery = {
    new Query(table, st.sqlBufferResult, rowFunc)
  }

  def sqlCache: BaseSelectQuery = {
    new Query(table, st.sqlCache, rowFunc)
  }

  def sqlNoCache: BaseSelectQuery = {
    new Query(table, st.sqlNoCache, rowFunc)
  }

  def sqlCalcFoundRows: BaseSelectQuery = {
    new Query(table, st.sqlCalcFoundRows, rowFunc)
  }
}
