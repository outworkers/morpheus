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

import com.websudos.morpheus.builder.AbstractSQLSyntax
import com.websudos.morpheus.mysql._
import com.websudos.morpheus.query.{RootCreateQuery, RootCreateSyntaxBlock}


class MySQLCreateSyntaxBLock(query: String, tableName: String) extends RootCreateSyntaxBlock(query, tableName) {
  override def syntax: AbstractSQLSyntax = MySQLSyntax
}

class MySQLRootCreateQuery[T <: BaseTable[T, _, MySQLRow], R](table: T, st: RootCreateSyntaxBlock, rowFunc: MySQLRow => R)
  extends RootCreateQuery[T, R, MySQLRow](table, st, rowFunc){

}
