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


private[morpheus] abstract class BaseQueryCondition(val clause: SQLBuiltQuery)

/**
 * This is a wrapper clause for primary conditions.
 * They wrap the Clause used in a "WHERE" or "AND" query.
 *
 * Only indexed columns can produce a QueryCondition via "WHERE" and "AND" operators.
 * @param clause The clause to use.
 */
case class QueryCondition(override val clause: SQLBuiltQuery, count: Int = 0) extends BaseQueryCondition(clause) {

  /**
   * This rather misterious implementation is used to handle enclosing parentheses for an unknown number of OR operator usages.
   * Since an unlimited number of OR operators and conditions can be chained to form a single WHERE or AND clause,
   * we need a way to delimite the full clause by enclosing parentheses without knowing how many OR clauses there are or without knowing what the internals
   * of a clause look like. Clauses like the IN clause have their own set of parentheses.
   * An example: {@code SELECT* FROM something WHERE (a = 5 OR a in (5, 10, 15)) }.
   *
   * Using the count parameter we can count the number of combinations in a manner invisible to the user. If the count is 0,
   * append the left '(' and the right ')' and for everyone thereafter, remove the ')', add the new clause, add a ')',
   * effectively always moving the right ')' to the end of the full WHERE or AND clause.
   * This is useful since we can't know how many OR clauses will be chained to form a single WHERE or AND clause.
   *
   * @param condition The QueryCondition to OR with.
   * @return A new QueryCondition, where the underlying query has been OR-ed.
   */
  def or(condition: QueryCondition): QueryCondition = {
    if (count == 0) {
      QueryCondition(MySQLQueryBuilder.or(
        clause.prepend(DefaultSQLOperators.`(`),
        condition.clause.append(DefaultSQLOperators.`)`)),
        count + 1
      )
    } else {
      QueryCondition(MySQLQueryBuilder.or(
        SQLBuiltQuery(clause.queryString.dropRight(1)),
        condition.clause.append(DefaultSQLOperators.`)`)),
        count + 1
      )
    }


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

  def < = lt _

  def lte(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.lte(col.name, col.toQueryString(value)))
  }

  def <= = lte _

  def gt(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.gt(col.name, col.toQueryString(value)))
  }

  def > = gt _

  def gte(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.gte(col.name, col.toQueryString(value)))
  }

  def >= = gte _

  def !=(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.!=(col.name, col.toQueryString(value)))
  }

  def <>(value: T): QueryCondition = {
    QueryCondition(col.table.queryBuilder.!=(col.name, col.toQueryString(value)))
  }

  def in(values: List[T]) : QueryCondition = {
    val primitive = implicitly[SQLPrimitive[T]]
    QueryCondition(col.table.queryBuilder.in(col.name, values.map(primitive.toSQL)))
  }


}


class QueryColumn[T: SQLPrimitive](col: AbstractColumn[T]) extends AbstractQueryColumn[T](col)
