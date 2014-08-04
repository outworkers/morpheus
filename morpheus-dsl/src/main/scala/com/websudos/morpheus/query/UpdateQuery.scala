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

sealed trait AssignQuery[T <: Table[T, _], R, QueryType <: SQLQuery[T, R]] {
  protected[this] def assignmentClass(table: T, query: SQLBuiltQuery, rowFunc: Row => R): QueryType

  def fromRow(row: Row): R
  def table: T
  def query: SQLBuiltQuery

  protected[this] def setTo(condition: T => QueryAssignment): QueryType = {
    assignmentClass(table, table.queryBuilder.set(query, condition(table).clause), fromRow)
  }

  protected[this] def andSetTo(condition: T => QueryAssignment): QueryType = {
    assignmentClass(table, table.queryBuilder.andSet(query, condition(table).clause), fromRow)
  }
}

class UpdateQuery[T <: Table[T, _], R](val table: T, val query: SQLBuiltQuery, rowFunc: Row => R)
  extends AssignQuery[T, R, AssignmentsQuery[T, R]] with SQLQuery[T, R] {

  def fromRow(row: Row): R = rowFunc(row)

  protected[this] def assignmentClass(table: T, query: SQLBuiltQuery, rowFunc: (Row) => R): AssignmentsQuery[T, R] = {
    new AssignmentsQuery[T, R](table, query, rowFunc)
  }

  def set(condition: T => QueryAssignment): AssignmentsQuery[T, R] = setTo(condition)
}

class UpdateWhere[T <: Table[T, _], R](table: T, val query: SQLBuiltQuery, rowFunc: Row => R) extends WhereQuery[T, R, UpdateWhere[T, R]](table, query,
  rowFunc) with SQLQuery[T, R]  {

  protected[this] def subclass(table: T, query: SQLBuiltQuery, rowFunc: (Row) => R): UpdateWhere[T, R] = {
    new UpdateWhere[T, R](table, query, rowFunc)
  }

  def and(condition: T => QueryCondition): UpdateWhere[T, R] = andClause(condition)
}


class AssignmentsQuery[T <: Table[T, _], R](val table: T, val query: SQLBuiltQuery, rowFunc: Row => R) extends WhereQuery[T, R, UpdateWhere[T, R]](table, query,
  rowFunc) with AssignQuery[T, R, AssignmentsAndQuery[T, R]] with SQLQuery[T, R]  {

  protected[this] def subclass(table: T, query: SQLBuiltQuery, rowFunc: (Row) => R): UpdateWhere[T, R] = {
    new UpdateWhere[T, R](table, query, rowFunc)
  }

  protected[this] def assignmentClass(table: T, query: SQLBuiltQuery, rowFunc: (Row) => R): AssignmentsAndQuery[T, R] = {
    new AssignmentsAndQuery[T, R](table, query, rowFunc)
  }

  def where(condition: T => QueryCondition): UpdateWhere[T, R] = clause(condition)
  def and(condition: T => QueryAssignment): AssignmentsAndQuery[T, R] = andSetTo(condition)
}

class AssignmentsAndQuery[T <: Table[T, _], R](val table: T, val query: SQLBuiltQuery, rowFunc: Row => R) extends WhereQuery[T, R, UpdateWhere[T, R]](table,
  query, rowFunc) with AssignQuery[T, R, AssignmentsAndQuery[T, R]] with SQLQuery[T, R]  {

  protected[this] def subclass(table: T, query: SQLBuiltQuery, rowFunc: (Row) => R): UpdateWhere[T, R] = {
    new UpdateWhere[T, R](table, query, rowFunc)
  }

  protected[this] def assignmentClass(table: T, query: SQLBuiltQuery, rowFunc: (Row) => R): AssignmentsAndQuery[T, R] = {
    new AssignmentsAndQuery[T, R](table, query, rowFunc)
  }

  def where(condition: T => QueryCondition): UpdateWhere[T, R] = clause(condition)
  def and(condition: T => QueryAssignment): AssignmentsAndQuery[T, R] = andSetTo(condition)
}
