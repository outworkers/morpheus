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
  def like: String
  def notLike: String
  def in: String
  def notIn: String
  def `<=>`: String
}

abstract class AbstractSQLKeys {

  val primaryKey = "PRIMARY KEY"
  val foreignKey = "FOREIGN KEY"
  val uniqueKey = "UNIQUE KEY"
  val index = "INDEX"
  val notNull = "NOT NULL"
  val autoIncrement = "AUTO_INCREMENT"

  val cascade = "CASCADE"
  val restrict = "RESTRICT"
  val setNull = "SET NULL"
  val noAction = "NO ACTION"
}

abstract class AbstractSQLSyntax extends AbstractSQLKeys {
  val into = "INTO"
  val values = "VALUES"
  val select = "SELECT"
  val distinct = "DISTINCT"
  val ignore = "IGNORE"
  val quick = "QUICK"

  val create = "CREATE"

  val insert = "INSERT"
  val ifNotExists = "IF NOT EXISTS"

  val where = "WHERE"
  val having = "HAVING"
  val update = "UPDATE"
  val delete = "DELETE"
  val orderBy = "ORDER BY"
  val groupBy = "GROUP BY"
  val limit = "LIMIT"
  val and = "AND"
  val or = "OR"
  val set = "SET"
  val from = "FROM"
  val table = "TABLE"
  val eqs = "="
  val `(` = "("
  val comma = ","
  val `)` = ")"
  val asc = "ASC"
  val desc = "DESC"
  val references = "REFERENCES"
  val onDelete = "ON DELETE"
  val onUpdate = "ON UPDATE"

  val leftJoin = "LEFT JOIN"
  val rightJoin = "RIGHT JOIN"
  val innerJoin = "INNER JOIN"
  val outerJoin = "OUTER JOIN"
}

abstract class AbstractSQLDataTypes {
  val tinyInt = "TINYINT"
  val smallInt = "SMALLINT"
  val mediumInt = "MEDIUMINT"
  val bigInt = "BIGINT"
  val int = "INT"
  val decimal = "DECIMAL"

  val float = "FLOAT"
  val double = "DOUBLE"

  val char = "CHAR"
  val varchar = "VARCHAR"


  val tinyText = "TINYTEXT"
  val text = "TEXT"
  val mediumText = "MEDIUMTEXT"
  val longText = "LONGTEXT"
  val binary = "BINARY"
  val varbinary = "VARBINARY"

  val tinyBlob = "TINYBLOB"
  val blob = "BLOB"
  val mediumBlob = "MEDIUMBLOB"
  val longBlob = "LONGBLOB"

  val date = "DATE"
  val dateTime = "DATETIME"
  val time = "TIME"
  val timestamp = "TIMESTAMP"
  val year = "YEAR"

  val enum = "ENUM"
  val set = "SET"
}

object DefaultSQLDataTypes extends AbstractSQLDataTypes

object DefaultSQLSyntax extends AbstractSQLSyntax


/**
 * This is used to represent a syntax block where multiple operations are possible at the same point in the code.
 * For instance, this is used to create a select block, where up to 10 operators can follow a select statement.
 */
private[morpheus] trait AbstractSyntaxBlock {
  def syntax: AbstractSQLSyntax
}

/**
 * The AbstractQueryBuilder is designed to define the basic
 * A QueryBuilder singleton will exist for every database supported by Morpheus.
 *
 * Every specific table implementation will automatically select the appropriate QueryBuilder while the user doesn't have to do anything.
 * Every imports package will carefully swap out the table implementation with the relevant one, so the user doesn't have to bother doing anything crazy like
 * using different base table implementations for different databases.
 */
private[morpheus] trait AbstractQueryBuilder {

  def operators: SQLOperatorSet
  def syntax: AbstractSQLSyntax

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

  def <=>(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(name)
      .pad.append(operators.`<=>`)
      .forcePad.append(value)
  }

  def like(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(name)
      .pad.append(operators.like)
      .forcePad.append(value)
  }

  def notLike(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(name)
      .pad.append(operators.notLike)
      .forcePad.append(value)
  }

  def in(name: String, values: List[String]): SQLBuiltQuery = {
    SQLBuiltQuery(name)
      .pad.append(operators.in)
      .forcePad.append(syntax.`(`)
      .append(values.mkString(", "))
      .append(syntax.`)`)
  }

  def notIn(name: String, values: List[String]): SQLBuiltQuery = {
    SQLBuiltQuery(name)
      .pad.append(operators.notIn)
      .forcePad.append(syntax.`(`)
      .append(values.mkString(", "))
      .append(syntax.`)`)
  }


  def select(tableName: String): SQLBuiltQuery = {
    SQLBuiltQuery(syntax.select)
      .forcePad.append("*").forcePad
      .append(syntax.from)
      .forcePad.append(tableName)
  }

  def select(tableName: String, names: String*): SQLBuiltQuery = {
    SQLBuiltQuery(syntax.select)
      .pad.append(names.mkString(" "))
      .forcePad.append(syntax.from)
      .forcePad.append(tableName)
  }

  def where(qb: SQLBuiltQuery, condition: SQLBuiltQuery): SQLBuiltQuery = {
    qb.pad.append(syntax.where).forcePad.append(condition)
  }

  def orderBy(qb: SQLBuiltQuery, conditions: Seq[SQLBuiltQuery]): SQLBuiltQuery = {
    qb.pad
      .append(syntax.orderBy)
      .forcePad.append(conditions.map(_.queryString).mkString(", "))
  }

  def groupBy(qb: SQLBuiltQuery, columns: Seq[String]): SQLBuiltQuery = {
    qb.pad
      .append(syntax.groupBy)
      .forcePad.append(columns.mkString(", "))
  }

  def having(qb: SQLBuiltQuery, condition: SQLBuiltQuery): SQLBuiltQuery = {
    qb.pad.append(syntax.having).pad.append(condition)
  }

  def limit(qb: SQLBuiltQuery, value: String): SQLBuiltQuery = {
    qb.pad.append(syntax.limit)
      .forcePad.append(value)
  }

  def and(qb: SQLBuiltQuery, condition: SQLBuiltQuery): SQLBuiltQuery = {
    qb.pad
      .append(syntax.and)
      .forcePad.append(condition)
  }

  def or(qb: SQLBuiltQuery, condition: SQLBuiltQuery): SQLBuiltQuery = {
    qb.pad.append(syntax.or).forcePad.append(condition)
  }

  def update(tableName: String): SQLBuiltQuery = {
    SQLBuiltQuery(syntax.update).forcePad
  }

  def setTo(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(name)
      .pad.append(operators.eq)
      .forcePad.append(value)
  }

  def set(qb: SQLBuiltQuery, condition: SQLBuiltQuery): SQLBuiltQuery = {
    qb.pad.append(syntax.set)
      .forcePad.append(condition)
  }

  def andSet(qb: SQLBuiltQuery, condition: SQLBuiltQuery): SQLBuiltQuery = {
    qb.append(syntax.comma)
      .forcePad.append(condition)
  }

  def asc(name: String): SQLBuiltQuery = {
    SQLBuiltQuery(name).forcePad.append(syntax.asc)
  }

  def desc(name: String): SQLBuiltQuery = {
    SQLBuiltQuery(name).forcePad.append(syntax.desc)
  }

  def insert(qb: SQLBuiltQuery, columns: List[String], values: List[String]): SQLBuiltQuery = {
    qb.pad.append(syntax.`(`)
      .append(columns.mkString(", "))
      .append(syntax.`)`)
      .forcePad.append(syntax.values)
      .forcePad.append(syntax.`(`)
      .append(values.mkString(", "))
      .append(syntax.`)`)
  }

  def leftJoin(qb: SQLBuiltQuery, join: SQLBuiltQuery): SQLBuiltQuery = {
    qb.pad
      .append(syntax.leftJoin)
      .forcePad.append(join)
  }

  def rightJoin(qb: SQLBuiltQuery, join: SQLBuiltQuery): SQLBuiltQuery = {
    qb.pad
      .append(syntax.rightJoin)
      .forcePad.append(join)
  }

  def innerJoin(qb: SQLBuiltQuery, join: SQLBuiltQuery): SQLBuiltQuery = {
    qb.pad
      .append(syntax.innerJoin)
      .forcePad.append(join)
  }

  def outerJoin(qb: SQLBuiltQuery, join: SQLBuiltQuery): SQLBuiltQuery = {
    qb.pad
      .append(syntax.outerJoin)
      .forcePad.append(join)
  }
}




