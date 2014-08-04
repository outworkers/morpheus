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

private[morpheus] abstract class AssignChainned
private[morpheus] abstract class AssignUnchainned


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

  def lowPriority: UpdateQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new UpdateQuery(table, st.lowPriority, rowFunc)
  }

  def ignore: UpdateQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new UpdateQuery(table, st.ignore, rowFunc)
  }

  private[morpheus] def all: UpdateQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new UpdateQuery(table, st.all, rowFunc)
  }
}

/**
 * This bit of magic allows all extending sub-classes to implement the "set" and "and" SQL clauses with all the necessary operators,
 * in a type safe way. By providing the third type argument and a custom way to subclass with the predetermined set of arguments,
 * all DSL representations of an UPDATE query can use the implementation without violating DRY.
 *
 * The reason why the "setTo" and "andSetTo" methods below are protected is so that extending classes can decide when and how to expose "where" and "and"
 * SQL methods to the DSL user. Used mainly to make queries like "select.set(_.a setTo b).set(_.c = d)" impossible,
 * or in other words make illegal programming states unrepresentable. There is an awesome book about how to do this in Scala,
 * I will link to it as soon as the book is published.
 *
 * @tparam T The type of the table owning the record.
 * @tparam R The type of the record held in the table.
 * @tparam QueryType The query type to subclass with and obtain as a result of a "set" or "and" application, requires all extending subclasses to supply a
 *                   type that will subclass an SQLQuery[T, R]
 *
 */
sealed trait AssignQuery[T <: Table[T, _], R, QueryType <: SQLQuery[T, R], AssignChain] {
  protected[this] def assignmentClass[AC](table: T, query: SQLBuiltQuery, rowFunc: Row => R): QueryType

  def fromRow(row: Row): R
  def table: T
  def query: SQLBuiltQuery

  def set(condition: T => QueryAssignment)(implicit ev: AssignChain =:= AssignUnchainned): QueryType = {
    assignmentClass[AssignChainned](table, table.queryBuilder.set(query, condition(table).clause), fromRow)
  }

  def and(condition: T => QueryAssignment)(implicit ev: AssignChain =:= AssignChainned): QueryType = {
    assignmentClass(table, table.queryBuilder.andSet(query, condition(table).clause), fromRow)
  }
}

class UpdateQuery[T <: Table[T, _], R, G, O, L, C, AC](val table: T, val query: SQLBuiltQuery, rowFunc: Row => R)
  extends AssignQuery[T, R, AssignmentsQuery[T, R, G, O, L, C, AC], AC] with SQLQuery[T, R] {

  def fromRow(row: Row): R = rowFunc(row)

  protected[this] def assignmentClass[ModifiedChain](table: T, query: SQLBuiltQuery, rowFunc: (Row) => R): AssignmentsQuery[T, R, G, O, L, C, ModifiedChain] = {
    new AssignmentsQuery[T, R, G, O, L, C, ModifiedChain](table, query, rowFunc)
  }
}


class AssignmentsQuery[T <: Table[T, _], R, G, O, L, C, AC](val table: T, val query: SQLBuiltQuery, rowFunc: Row => R) extends WhereQuery[T, R, AssignmentsQuery[T, R, G, O, L, C, AC], G, O, L, C](table, query,
  rowFunc) with AssignQuery[T, R, AssignmentsQuery[T, R, G, O, L, C, AC], AC] with SQLQuery[T, R]  {

  protected[this] def subclass[
    Group,
    Order,
    Limit,
    Chain
  ](table: T, query: SQLBuiltQuery, rowFunc: Row => R): AssignmentsQuery[T, R, Group, Order, Limit, Chain, AC] = {
    new AssignmentsQuery[T, R, Group, Order, Limit, Chain, AC](table, query, rowFunc)
  }

  protected[this] def assignmentClass[ModifiedChain](table: T, query: SQLBuiltQuery, rowFunc: (Row) => R): AssignmentsQuery[T, R, G, O, L, C, ModifiedChain] = {
    new AssignmentsQuery[T, R, G, O, L, C, ModifiedChain](table, query, rowFunc)
  }
}