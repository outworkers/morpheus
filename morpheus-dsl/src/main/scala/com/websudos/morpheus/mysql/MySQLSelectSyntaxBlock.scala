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

import com.websudos.morpheus.query.{ AbstractSelectSyntaxBlock, SQLBuiltQuery }

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
