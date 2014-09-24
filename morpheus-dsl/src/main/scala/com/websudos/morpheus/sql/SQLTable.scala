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

package com.websudos.morpheus.sql


import com.websudos.morpheus.dsl.{ SelectTable }
import com.websudos.morpheus.query._

abstract class SQLTable[Owner <: BaseTable[Owner, Record], Record] extends BaseTable[Owner, Record] with SelectTable[Owner, Record,
  AbstractRootSelectQuery, AbstractSelectSyntaxBlock] {

  val queryBuilder = DefaultQueryBuilder

  val syntax = DefaultSQLSyntax

  protected[this] def createRootSelect[A <: BaseTable[A, _], B](table: A, block: AbstractSelectSyntaxBlock, rowFunc: Row => B): AbstractRootSelectQuery[A,
    B] = {
    new AbstractRootSelectQuery[A, B](table, block, rowFunc)
  }

  protected[this] def createSelectSyntaxBlock(query: String, tableName: String, cols: List[String] = List("*")): AbstractSelectSyntaxBlock = {
    new AbstractSelectSyntaxBlock(query, tableName, cols)
  }

  def update: RootUpdateQuery[Owner, Record] = new RootUpdateQuery(
    this.asInstanceOf[Owner],
    new RootUpdateSyntaxBlock(syntax.update, tableName),
    fromRow
  )

  def delete: RootDeleteQuery[Owner, Record] = new RootDeleteQuery(
    this.asInstanceOf[Owner],
    new RootDeleteSyntaxBlock(syntax.delete, tableName),
    fromRow
  )

  def insert: RootInsertQuery[Owner, Record] = new RootInsertQuery(
    this.asInstanceOf[Owner],
    new RootInsertSyntaxBlock(syntax.insert, tableName),
    fromRow
  )

  def create: RootCreateQuery[Owner, Record] = new RootCreateQuery(
    this.asInstanceOf[Owner],
    new RootCreateSyntaxBlock(syntax.create, tableName),
    fromRow
  )

}
