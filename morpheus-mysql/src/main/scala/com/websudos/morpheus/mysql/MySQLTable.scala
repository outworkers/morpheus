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

package com.websudos.morpheus.mysql

import com.twitter.finagle.exp.mysql.Row
import com.websudos.morpheus.dsl.{SelectTable, BaseTable => BaseTable}

abstract class MySQLTable[Owner <: BaseTable[Owner, Record], Record] extends BaseTable[Owner, Record] with SelectTable[Owner, Record,
  MySQLRootSelectQuery, MySQLSelectSyntaxBlock] {

  val queryBuilder = MySQLQueryBuilder

  val syntax = MySQLSyntax

  protected[this] def createRootSelect[A <: BaseTable[A, _], B](table: A, block: MySQLSelectSyntaxBlock, rowFunc: Row => B): MySQLRootSelectQuery[A,
    B] = {
    new MySQLRootSelectQuery[A, B](table, block, rowFunc)
  }

  protected[this] def createSelectSyntaxBlock(query: String, tableName: String, cols: List[String] = List("*")): MySQLSelectSyntaxBlock = {
    new MySQLSelectSyntaxBlock(query, tableName, cols)
  }

  def update: MySQLRootUpdateQuery[Owner, Record] = new MySQLRootUpdateQuery(
    this.asInstanceOf[Owner],
    MySQLUpdateSyntaxBlock(syntax.update, tableName),
    fromRow
  )

  def delete: MySQLRootDeleteQuery[Owner, Record] = new MySQLRootDeleteQuery(
    this.asInstanceOf[Owner],
    MySQLDeleteSyntaxBlock(syntax.delete, tableName),
    fromRow
  )

  def insert: MySQLRootInsertQuery[Owner, Record] = new MySQLRootInsertQuery(
    this.asInstanceOf[Owner],
    new MySQLInsertSyntaxBlock(syntax.insert, tableName),
    fromRow
  )

  def create: MySQLRootCreateQuery[Owner, Record] = new MySQLRootCreateQuery(
    this.asInstanceOf[Owner],
    new MySQLCreateSyntaxBLock(syntax.create, tableName),
    fromRow
  )

}
