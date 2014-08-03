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

import com.websudos.morpheus.SQLPrimitive
import com.websudos.morpheus.column.AbstractColumn


/**
 * This is a wrapper clause for primary conditions.
 * They wrap the Clause used in a "WHERE" or "AND" query.
 *
 * Only indexed columns can produce a QueryCondition via "WHERE" and "AND" operators.
 * @param clause The clause to use.
 */
case class QueryCondition(clause: SQLBuiltQuery) {

  def or(condition: QueryCondition): QueryCondition = {
    QueryCondition(MySQLQueryBuilder.or(clause, condition.clause))
  }
}


/**
 * A class enforcing columns used in where clauses to be indexed.
 * Using an implicit mechanism, only columns that are indexed can be converted into Indexed columns.
 * This enforces a Cassandra limitation at compile time.
 * It prevents a user from querying and using where operators on a column without any index.
 * @param col The column to cast to an IndexedColumn.
 * @tparam T The type of the value the column holds.
 */
sealed abstract class AbstractQueryColumn[T: SQLPrimitive](col: AbstractColumn[T]) {

  /**
   * The equals operator. Will return a match if the value equals the database value.
   * @param value The value to search for in the database.
   * @return A QueryCondition, wrapping a QueryBuilder clause.
   */
  def eqs(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.eqs(col.name, col.toQueryString(value)))
  }

  def lt(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.lt(col.name, col.toQueryString(value)))
  }

  def lte(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.lte(col.name, col.toQueryString(value)))
  }

  def gt(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.gt(col.name, col.toQueryString(value)))
  }

  def gte(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.gte(col.name, col.toQueryString(value)))
  }

  def !=(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.!=(col.name, col.toQueryString(value)))
  }

  def <>(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.!=(col.name, col.toQueryString(value)))
  }


}


class QueryColumn[T: SQLPrimitive](col: AbstractColumn[T]) extends AbstractQueryColumn[T](col)
