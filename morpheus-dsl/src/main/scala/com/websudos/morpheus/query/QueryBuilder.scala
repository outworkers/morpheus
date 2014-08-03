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
 * The hierarchical implementation of operators is designed to account for potential variations between SQL databases.
 * Every specific implementation can provide it's own set of operators and string encoding for them based on the specific semantics.
 *
 * A QueryBuilder singleton will exist for every database, and every QueryBuilder will select a specific set of operators.
 */
trait SQLOperatorSet {
  def eq: String
  def lt: String
  def lte: String
  def gt: String
  def gte: String
  def `!=`: String
  def `<>`: String
}

object MySQLOperatorSet extends SQLOperatorSet {
  val eq = "="
  val lt = "<"
  val lte = "<="
  val gt = ">"
  val gte = ">="
  val != = "!="
  val <> = "<>"
}

/**
 * This is used to represent a syntax block where multiple operations are possible at the same point in the code.
 * For instance, this is used to create a select block, where up to 10 operators can follow a select statement.
 */
sealed trait AbstractSyntaxBlock {

}

case class SelectSyntaxBlock[T <: Table[T, _], R](qb: SQLBuiltQuery, tableName: String, fromRow: Row => R, statements: List[String] = List("*")) {

  def from: SQLBuiltQuery = {
    qb.pad.append(s"FROM $tableName")
  }

  def `*`: SQLBuiltQuery = {
    qb.pad.append(s"${statements.mkString(" ")} FROM $tableName")
  }

  def all: SQLBuiltQuery = `*` _

  def distinct: SQLBuiltQuery = {
    qb.pad.append(s"DISTINCT ${statements.mkString(", ")} FROM $tableName")
  }

  def distinctRow: SQLBuiltQuery = {
    qb.pad.append(s"DISTINCTROW ${statements.mkString(", ")} FROM $tableName")
  }

}


/**
 * The AbstractQueryBuilder is designed to define the basic
 * A QueryBuilder singleton will exist for every database supported by Morpheus.
 *
 * Every specific table implementation will automatically select the appropriate QueryBuilder while the user doesn't have to do anything.
 * Every imports package will carefully swap out the table implementation with the relevant one, so the user doesn't have to bother doing anything crazy like
 * using different base table implementations for different databases.
 */
sealed trait AbstractQueryBuilder {

  def operators: SQLOperatorSet

  def eqs(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(s"$name ${operators.eq} $value")
  }

  def lt(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(s"$name ${operators.lt} $value")
  }

  def lte(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(s"$name ${operators.lte} $value")
  }

  def gt(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(s"$name ${operators.gt} $value")
  }

  def gte(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(s"$name ${operators.gte} $value")
  }

  def !=(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(s"$name ${operators.`!=`} $value")
  }

  def <>(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(s"$name ${operators.`<>`} $value")
  }

  def select(tableName: String): SQLBuiltQuery = {
    SQLBuiltQuery(s"SELECT * FROM $tableName")
  }

  def select(tableName: String, names: String*): SQLBuiltQuery = {
    SQLBuiltQuery(s"SELECT ${names.mkString(" ")} FROM $tableName")
  }

  def where(qb: SQLBuiltQuery, condition: SQLBuiltQuery): SQLBuiltQuery = {
    qb.pad.append(DefaultSQLOperators.where).pad.append(condition)
  }

  def and(qb: SQLBuiltQuery, condition: SQLBuiltQuery): SQLBuiltQuery = {
    qb.pad.append(SQLBuiltQuery("AND ").append(condition))
  }

  def or(qb: SQLBuiltQuery, condition: SQLBuiltQuery): SQLBuiltQuery = {
    qb.pad.append(SQLBuiltQuery("OR ").append(condition))
  }

  def in(name: String, values: List[String]): SQLBuiltQuery = {
    SQLBuiltQuery(name).pad.append(DefaultSQLOperators.in.pad)
      .append(DefaultSQLOperators.`(`)
      .append(values.mkString(", "))
      .append(DefaultSQLOperators.`)`)
  }

}


object MySQLQueryBuilder extends AbstractQueryBuilder {
  val operators = MySQLOperatorSet
}

