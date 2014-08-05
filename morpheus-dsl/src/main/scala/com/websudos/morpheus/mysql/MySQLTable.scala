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

abstract class MySQLTable[Owner <: MySQLTable[Owner, Record], Record] extends Table[Owner, Record] {
  val queryBuilder = MySQLQueryBuilder

  protected[this] def createRootSelect[A <: Table[A, _], B](table: A, block: SelectSyntaxBlock[A, B], rowFunc: Row => B): MySQLRootSelectQuery[A, B] = {
    new MySQLRootSelectQuery[A, B](table, block, rowFunc)

  }

  def update: MySQLRootUpdateQuery[Owner, Record] = new MySQLRootUpdateQuery(
    this.asInstanceOf[Owner],
    UpdateSyntaxBlock(DefaultSQLOperators.update, tableName, fromRow),
    fromRow
  )

  def delete: MySQLRootDeleteQuery[Owner, Record] = new MySQLRootDeleteQuery(
    this.asInstanceOf[Owner],
    DeleteSyntaxBlock(DefaultSQLOperators.delete, tableName, fromRow),
    fromRow
  )

}
