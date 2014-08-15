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

package com.websudos.morpheus.column

import com.twitter.finagle.exp.mysql.Row
import com.websudos.morpheus.dsl.Table
import com.websudos.morpheus.query.{DefaultSQLSyntax, SQLBuiltQuery}

private[morpheus] trait IndexColumn[T] {
  def apply(r: Row): T = throw new Exception(s"Index column is not a value column. This apply method cannot extract anything from it.")
}

/**
 * This implements an SQL index column. With this the user can define indexes on a table.
 * An Index is also an implementation of an Indexed column, meaning it holds no concrete value to be extracted.
 * @param columns The columns that form the
 * @tparam T
 * @tparam R
 */
class Index[T <: Table[T, R], R](columns: AbstractColumn[_]*) extends AbstractColumn[String] with IndexColumn[String] {

  def qb: SQLBuiltQuery = {
    SQLBuiltQuery(DefaultSQLSyntax.index)
      .forcePad.append(DefaultSQLSyntax.`(`)
      .append(columns.map(_.name).mkString(", "))
      .append(DefaultSQLSyntax.`)`)
  }

  override def sqlType: String = "string"

  override def toQueryString(v: String): String = v

  override def table: Table[_, _] = columns.head.table
}
