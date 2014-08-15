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

import com.twitter.finagle.exp.mysql.Row
import com.websudos.morpheus.dsl.Table

private[morpheus]abstract class AbstractCreateSyntaxBlock(query: String, tableName: String) extends AbstractSyntaxBlock {

  protected[this] val qb: SQLBuiltQuery = SQLBuiltQuery(query)

  def syntax: AbstractSQLSyntax

  def default: SQLBuiltQuery = {
    qb.pad.append(DefaultSQLSyntax.table)
      .forcePad.append(tableName)
  }

  def ifNotExists: SQLBuiltQuery = {
    qb.pad.append(DefaultSQLSyntax.table)
      .forcePad.append(syntax.ifNotExists)
      .forcePad.append(tableName)
  }
}

/**
 * This is the implementation of a root CREATE query, a wrapper around an abstract CREATE syntax block.
 *
 * This is used as the entry point to an SQL CREATE query, and it requires the user to provide "one more method" to fully specify a CREATE query.
 * The implicit conversion from a RootCreateQuery to a CreateQuery will automatically pick the "default" strategy below.
 *
 * @param table The table owning the record.
 * @param st The Abstract syntax block describing the possible decisions.
 * @param rowFunc The function used to map a result to a type-safe record.
 * @tparam T The type of the owning table.
 * @tparam R The type of the record.
 */
private[morpheus] abstract class AbstractRootCreateQuery[T <: Table[T, _], R](val table: T, val st: AbstractCreateSyntaxBlock, val rowFunc: Row => R) {

  def fromRow(r: Row): R = rowFunc(r)

  protected[this] type BaseCreateQuery = Query[T, R, CreateType, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated]

  private[morpheus] def default: BaseCreateQuery = {
    new Query(table, st.default, rowFunc)
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
class CreateQuery[
  T <: Table[T, _],
  R,
  Type <: QueryType,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: StatusBind
](val query: Query[T, R, Type, Group, Order, Limit, Chain, AssignChain, Status]) {


  private[morpheus] final def terminate: Query[T, R, CreateType, Group, Order, Limit, Chain, AssignChain, Terminated] = {
    new Query(
      query.table,
      query.query,
      query.rowFunc
    )
  }

}




