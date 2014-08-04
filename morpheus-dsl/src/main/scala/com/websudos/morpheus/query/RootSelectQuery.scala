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

package com.websudos.morpheus.query

import com.twitter.finagle.exp.mysql.Row
import com.websudos.morpheus.dsl.Table

case class SelectSyntaxBlock[T <: Table[T, _], R](query: String, tableName: String, fromRow: Row => R, columns: List[String] = List("*")) {

  private[this] val qb = SQLBuiltQuery(query)

  def `*`: SQLBuiltQuery = {
    qb.pad.append(columns.mkString(" "))
      .pad.append(DefaultSQLOperators.from)
      .pad.append(tableName)
  }

  def all: SQLBuiltQuery = this.`*`

  def distinct: SQLBuiltQuery = {
    qb.pad.append(DefaultSQLOperators.distinct)
      .pad.append(columns.mkString(", "))
      .pad.append(DefaultSQLOperators.from)
      .pad.append(tableName)
  }

  def distinctRow: SQLBuiltQuery = {
    qb.pad.append(DefaultSQLOperators.distinctRow)
      .pad.append(columns.mkString(", "))
      .pad.append(DefaultSQLOperators.from)
      .pad.append(tableName)
  }
}

/**
 * This is the implementation of a root select query, a wrapper around an abstract syntax block.
 * The basic select of select methods can be seen in {@link com.websudos.morpheus.dsl.SelectTable}
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
private[morpheus] class RootSelectQuery[T <: Table[T, _], R](val table: T, val st: SelectSyntaxBlock[T, _], val rowFunc: Row => R) {

  def fromRow(r: Row): R = rowFunc(r)

  def distinct: SelectQuery[T, R] = {
    new SelectQuery(table, st.distinct, rowFunc)
  }

  def distinctRow: SelectQuery[T, R] = {
    new SelectQuery(table, st.distinctRow, rowFunc)
  }

  def all: SelectQuery[T, R] = {
    new SelectQuery(table, st.*, rowFunc)
  }
}


class SelectQuery[T <: Table[T, _], R](table: T, val query: SQLBuiltQuery, rowFunc: Row => R) extends WhereQuery[T, R, SelectWhere[T,
  R]](table, query, rowFunc) with BaseSelectQuery[T, R] {


  protected[this] def subclass(table: T, query: SQLBuiltQuery, rowFunc: Row => R): SelectWhere[T, R] = new SelectWhere[T, R](table, query, rowFunc)

  def where(condition: T => QueryCondition): SelectWhere[T, R] = clause(condition)
}

class SelectWhere[T <: Table[T, _], R](table: T, val query: SQLBuiltQuery, rowFunc: Row => R) extends WhereQuery[T, R, SelectWhere[T,
  R]](table, query, rowFunc) with BaseSelectQuery[T, R] {

  protected[this] def subclass(table: T, query: SQLBuiltQuery, rowFunc: Row => R): SelectWhere[T, R] = new SelectWhere[T, R](table, query, rowFunc)

  def and(condition: T => QueryCondition): SelectWhere[T, R] = andClause(condition)
}