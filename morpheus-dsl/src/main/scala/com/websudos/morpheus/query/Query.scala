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

package com.websudos.morpheus.query

import com.websudos.morpheus.builder.SQLBuiltQuery

import scala.annotation.implicitNotFound

import com.websudos.morpheus.Row
import com.websudos.morpheus.column.SelectColumn
import com.websudos.morpheus.dsl.BaseTable

sealed trait GroupBind
final abstract class Groupped extends GroupBind
final abstract class Ungroupped extends GroupBind

sealed trait ChainBind
final abstract class Chainned extends ChainBind
final abstract class Unchainned extends ChainBind

sealed trait OrderBind
final abstract class Ordered extends OrderBind
final abstract class Unordered extends OrderBind

sealed trait LimitBind
final abstract class Limited extends LimitBind
final abstract class Unlimited extends LimitBind

sealed trait StatusBind
final abstract class Terminated extends StatusBind
final abstract class Unterminated extends StatusBind

/**
 * This bit of magic allows all extending sub-classes to implement the "where" and "and" SQL clauses with all the necessary operators,
 * in a type safe way. By providing the third type argument and a custom way to subclass with the predetermined set of arguments, all queries such as UPDATE,
 * DELETE, ALTER and so on can use the same root implementation of clauses and therefore avoid the violation of DRY.
 *
 * The reason why the "clause" and "andClause" methods below are protected is so that extending classes can decide when and how to expose "where" and "and"
 * SQL methods to the DSL user. Used mainly to make queries like "select.where(_.a = b).where(_.c = d)" impossible,
 * or in other words make illegal programming states unrepresentable. There is an awesome book about how to do this in Scala,
 * I will link to it as soon as the book is published.
 *
 * @param table The table owning the record.
 * @param query The root SQL query to start building from.
 * @param rowFunc The function mapping a row to a record.
 * @tparam T The type of the table owning the record.
 * @tparam R The type of the record held in the table.
 */
class Query[T <: BaseTable[T, _, TableRow],
  R,
  TableRow <: Row,
  Group <: GroupBind,
  Ord <: OrderBind,
  Lim <: LimitBind,
  Chain <: ChainBind,
  AC <: AssignBind,
  Status <: StatusBind
](val table: T, val query: SQLBuiltQuery, val rowFunc: TableRow => R) extends SQLQuery[T, R, TableRow] {

  def fromRow(row: TableRow): R = rowFunc(row)

  @implicitNotFound("You cannot set two limits on the same query")
  final def limit(value: Int)(implicit ev: Lim =:= Unlimited): Query[T, R, TableRow, Group, Ord, Limited, Chain, AC, Status] = {
    new Query(table, table.queryBuilder.limit(query, value.toString), rowFunc)
  }

  @implicitNotFound("You cannot ORDER a query more than once")
  final def orderBy(conditions: (T => QueryOrder)*)(implicit ev: Ord =:= Unordered): Query[T, R, TableRow, Group, Ordered, Lim, Chain, AC, Status] = {
    val applied = conditions map {
      fn => fn(table).clause
    }
    new Query(table, table.queryBuilder.orderBy(query, applied), rowFunc)
  }

  @implicitNotFound("You cannot GROUP a query more than once or GROUP after you ORDER a query")
  final def groupBy(columns: (T => SelectColumn[_])*)(implicit ev1: Group =:= Ungroupped, ev2: Ord =:= Unordered): Query[T, R, TableRow, Groupped, Ord, Lim,
    Chain, AC, Status] = {
    new Query(table, table.queryBuilder.groupBy(query, columns map { _(table).queryString }), rowFunc)
  }


}

object Query {
  def apply[T <: BaseTable[T, _, TableRow], R, TableRow <: Row](table: T, query: SQLBuiltQuery, rowFunc: TableRow => R): Query[T, R, TableRow,
    Ungroupped,
    Unordered,
    Unlimited,
    Unchainned,
    AssignUnchainned, Unterminated] = {
    new Query(table, query, rowFunc)
  }
}
