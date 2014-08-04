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
import scala.annotation.implicitNotFound


case class UpdateSyntaxBlock[T <: Table[T, _], R](query: String, tableName: String, fromRow: Row => R, columns: List[String] = List("*")) {

  private[this] val qb = SQLBuiltQuery(query)

  def all: SQLBuiltQuery = {
    qb.pad.append(tableName)
  }

  def lowPriority: SQLBuiltQuery = {
    qb.pad.append(DefaultSQLOperators.lowPriority)
      .pad.append(tableName)
  }

  def ignore: SQLBuiltQuery = {
    qb.pad.append(DefaultSQLOperators.ignore)
      .pad.append(tableName)
  }
}

sealed trait AssignBind
sealed abstract class AssignChainned extends AssignBind
sealed abstract class AssignUnchainned extends AssignBind


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
private[morpheus] class RootUpdateQuery[T <: Table[T, _], R](val table: T, val st: UpdateSyntaxBlock[T, _], val rowFunc: Row => R) {

  def fromRow(r: Row): R = rowFunc(r)

  def lowPriority: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.lowPriority, rowFunc)
  }

  def ignore: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.ignore, rowFunc)
  }

  private[morpheus] def all: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.all, rowFunc)
  }
}

/**
 * This bit of magic allows all extending sub-classes to implement the "set" and "and" SQL clauses with all the necessary operators,
 * in a type safe way. By providing the third type argument and a custom way to subclass with the predetermined set of arguments,
 * all DSL representations of an UPDATE query can use the implementation without violating DRY.
 *
 * @tparam T The type of the table owning the record.
 * @tparam R The type of the record held in the table.
 */
class AssignmentsQuery[
  T <: Table[T, _],
  R,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind
](val query: Query[T, R, Group, Order, Limit, Chain, AssignChain]) {

  @implicitNotFound("You can't use 2 SET parts on a single UPDATE query")
  def set(condition: T => QueryAssignment)(implicit ev: AssignChain =:= AssignUnchainned): AssignmentsQuery[T, R, Group, Order, Limit, Chain, AssignChainned] = {
    new AssignmentsQuery[T, R, Group, Order, Limit, Chain, AssignChainned](
      new Query[T, R, Group, Order, Limit, Chain, AssignChainned](
        query.table,
        query.table.queryBuilder.set(query.query, condition(query.table).clause),
        query.rowFunc
      )
    )
  }

  @implicitNotFound("""You need to use the "set" method before using the "and"""")
  def and(condition: T => QueryAssignment, signChange: Int = 0)(implicit ev: AssignChain =:= AssignChainned): AssignmentsQuery[T, R, Group, Order, Limit, Chain, AssignChainned] = {
    new AssignmentsQuery[T, R, Group, Order, Limit, Chain, AssignChainned](
      new Query[T, R, Group, Order, Limit, Chain, AssignChainned](
        query.table,
        query.table.queryBuilder.andSet(query.query, condition(query.table).clause),
        query.rowFunc
      )
    )
  }

}