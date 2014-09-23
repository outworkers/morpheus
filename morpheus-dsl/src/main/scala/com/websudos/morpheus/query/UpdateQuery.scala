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

import scala.annotation.implicitNotFound

import com.websudos.morpheus.dsl.BaseTable


private[morpheus] class RootUpdateSyntaxBlock(query: String, tableName: String) extends AbstractSyntaxBlock {

  protected[this] val qb = SQLBuiltQuery(query)

  def all: SQLBuiltQuery = {
    qb.pad.appendEscape(tableName)
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
private[morpheus] class RootUpdateQuery[T <: BaseTable[T, _], R](val table: T, val st: RootUpdateSyntaxBlock, val rowFunc: Row => R) {

  protected[this] type BaseUpdateQuery = Query[T, R, UpdateType, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated]

  private[morpheus] def all: BaseUpdateQuery = {
    new Query(table, st.all, rowFunc)
  }
}

trait AssignBind
sealed abstract class AssignChainned extends AssignBind
sealed abstract class AssignUnchainned extends AssignBind


/**
 * This bit of magic allows all extending sub-classes to implement the "set" and "and" SQL clauses with all the necessary operators,
 * in a type safe way. By providing the third type argument and a custom way to subclass with the predetermined set of arguments,
 * all DSL representations of an UPDATE query can use the implementation without violating DRY.
 *
 * @tparam T The type of the table owning the record.
 * @tparam R The type of the record held in the table.
 */
class AssignmentsQuery[T <: BaseTable[T, _],
  R,
  Type <: QueryType,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: StatusBind
](val query: Query[T, R, Type, Group, Order, Limit, Chain, AssignChain, Status]) {

  @implicitNotFound("You can't use 2 SET parts on a single UPDATE query")
  final def set(condition: T => QueryAssignment)(implicit ev: AssignChain =:= AssignUnchainned, ev1: Status =:= Unterminated): AssignmentsQuery[T, R, Type,
    Group, Order,
    Limit,
    Chain,
    AssignChainned, Status] = {
    new AssignmentsQuery[T, R, Type, Group, Order, Limit, Chain, AssignChainned, Status](
      new Query[T, R, Type, Group, Order, Limit, Chain, AssignChainned, Status](
        query.table,
        query.table.queryBuilder.set(query.query, condition(query.table).clause),
        query.rowFunc
      )
    )
  }

  @implicitNotFound("""You need to use the "set" method before using the "and"""")
  final def and(condition: T => QueryAssignment, signChange: Int = 0)(implicit ev: AssignChain =:= AssignChainned): AssignmentsQuery[T, R, Type, Group, Order,
    Limit,
    Chain, AssignChainned, Status] = {
    new AssignmentsQuery[T, R, Type, Group, Order, Limit, Chain, AssignChainned, Status](
      new Query[T, R, Type, Group, Order, Limit, Chain, AssignChainned, Status](
        query.table,
        query.table.queryBuilder.andSet(query.query, condition(query.table).clause),
        query.rowFunc
      )
    )
  }

  private[morpheus] final def terminate: Query[T, R, UpdateType, Group, Order, Limit, Chain, AssignChainned, Terminated] = {
    new Query(
      query.table,
      query.query,
      query.rowFunc
    )
  }

}
