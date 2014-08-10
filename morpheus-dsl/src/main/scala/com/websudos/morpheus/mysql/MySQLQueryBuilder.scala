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

import com.websudos.morpheus.{SQLPrimitives, SQLPrimitive}
import com.websudos.morpheus.column.AbstractColumn
import com.websudos.morpheus.query.{AbstractSQLSyntax, SQLOperatorSet, AbstractQueryColumn, AbstractQueryBuilder}


trait MySQLPrimitives extends SQLPrimitives {}


object MySQLSyntax extends AbstractSQLSyntax {
  val distinctRow = "DISTINCTROW"
  val lowPriority = "LOW_PRIORITY"
  val highPriority = "HIGH_PRIORITY"
  val delayed = "DELAYED"
  val straightJoin = "STRAIGHT_JOIN"
  val sqlSmallResult = "SQL_SMALL_RESULT"
  val sqlBigResult = "SQL_BIG_RESULT"
  val sqlBufferResult = "SQL_BUFFER_RESULT"
  val sqlCache = "SQL_CACHE"
  val sqlNoCache = "SQL_NO_CACHE"
  val sqlCalcFoundRows = "SQL_CALC_FOUND_ROWS"
}


object MySQLOperatorSet extends SQLOperatorSet {
  val eq = "="
  val lt = "<"
  val lte = "<="
  val gt = ">"
  val gte = ">="
  val != = "!="
  val <> = "<>"
  val like = "LIKE"
  val notLike = "NOT LIKE"
  val in = "IN"
  val notIn = "NOT IN"
  val <=> = "<=>"
}

object MySQLQueryBuilder extends AbstractQueryBuilder {
  val operators = MySQLOperatorSet
  val syntax = MySQLSyntax
}

private[morpheus] class MySQLQueryColumn[T : SQLPrimitive](col: AbstractColumn[T]) extends AbstractQueryColumn[T](col)
