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
package com.websudos.morpheus.query

import com.websudos.morpheus.column.Row
import com.websudos.morpheus.dsl.BaseTable

private[morpheus] class RootDeleteSyntaxBlock(query: String, tableName: String) extends AbstractSyntaxBlock {

  protected[this] val qb: SQLBuiltQuery = SQLBuiltQuery(query)

  def all: SQLBuiltQuery = {
    qb.pad.append(DefaultSQLSyntax.from)
      .forcePad.appendEscape(tableName)
  }

  def syntax: AbstractSQLSyntax = DefaultSQLSyntax
}

/**
 * This is the implementation of a root UPDATE query, a wrapper around an abstract syntax block.
 *
 * This is used as the entry point to an SQL query, and it requires the user to provide "one more method" to fully specify a SELECT query.
 * The implicit conversion from a RootSelectQuery to a SelectQuery will automatically pick the "all" strategy below.
 *
 * @param table The table owning the record.
 * @param st The Abstract syntax block describing the possible decisions.
 * @param rowFunc The function used to map a result to a type-safe record.
 * @tparam T The type of the owning table.
 * @tparam R The type of the record.
 */
private[morpheus] class RootDeleteQuery[T <: BaseTable[T, _], R](val table: T, val st: RootDeleteSyntaxBlock, val rowFunc: Row => R) {

  def fromRow(r: Row): R = rowFunc(r)

  protected[this] type BaseDeleteQuery = Query[T, R, DeleteType, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated]

  private[morpheus] final def all: BaseDeleteQuery = {
    new Query(table, st.all, rowFunc)
  }

}


