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

/**
 * The hierarchical implementation of operators is designed to account for potential variations between SQL databases.
 * Every specific implementation can provide it's own set of operators and string encoding for them based on the specific semantics.
 *
 * A QueryBuilder singleton will exist for every database, and every QueryBuilder will select a specific set of operators.
 */
trait SQLOperatorSet {
  val eq = "="
  val lt = "<"
  val lte = "<="
  val gt = ">"
  val gte = ">="
  val != = "!="
  val <> = "<>"
  val like = "LIKE"
  val notLike = "NOT LIKE"
  val in = "IN"
  val notIn = "NOT IN"
  val <=> = "<=>"

  val ascii = "ASCII"
  val bin = "BIN"
  val bitLength = "BIT_LENGTH"
  val charLength = "CHAR_LENGTH"
  val characterLength = "CHARACTER_LENGTH"

  val concat = "CONCAT"
  val concatWs = "CONCAT_WS"

  val elt = "ELT"
  val exportSet = "EXPORT_SET"
  val field = "FIELD"
  val findInSet = "FIND_IN_SET"
  val format = "FORMAT"
  val fromBase64 = "FROM_BASE64"
  val hex = "HEX"
  val instr = "INSTR"
  val lcase = "LCASE"
  val left = "LEFT"
  val loadFile = "LOAD_FILE"
  val locate = "LOCATE"
  val lower = "LOWER"
  val lpad = "LPAD"
  val ltrim = "LTRIM"
  val makeSet = "MAKE_SET"
  val `match` = "MATCH"
  val mid = "MID"
  val notRegexp = "NOT REGEXP"
  val oct = "OCT"
  val octetLength = "OCTET_LENGTH"
  val ord = "ORD"
  val position = "POSITION"
  val quote = "QUOTE"
  val regexp = "REGEXP"
  val repeat = "REPEAT"
  val replace = "REPLACE"
  val reverse = "REVERSE"
  val right = "RIGHT"
  val rlike = "RLIKE"
  val rpad = "RPAD"
  val rtrim = "RTRIM"
  val soundex = "SOUNDEX"
  val soundsLike = "SOUNDS LIKE"
  val space = "SPACE"
  val strcmp = "STRCMP"
  val substr = "SUBSTR"
  val substringIndex = "SUBSTRING_INDEX"
  val substring = "SUBSTRING"
  val toBase64 = "TO_BASE64"
  val trim = "TRIM"
  val ucase = "UCASE"
  val unhex = "UNHEX"
  val upper = "UPPER"
  val weightString = "WEIGHT_STRING"
}

trait AbstractSQLKeys {

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
  val temporary = "TEMPORARY"

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

  val between = "BETWEEN"
  val not = "NOT"
  val notBetween = "NOT BETWEEN"
  val exists = "EXISTS"
  val notExists = "NOT EXISTS"
  val on = "ON"

  val engine = "ENGINE"

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
  val long = "LONG"

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

object DefaultSQLOperatorSet extends SQLOperatorSet

object DefaultSQLDataTypes extends AbstractSQLDataTypes

object DefaultSQLSyntax extends AbstractSQLSyntax

private[morpheus] object DefaultQueryBuilder extends AbstractQueryBuilder {
  val syntax = DefaultSQLSyntax
  val operators: SQLOperatorSet = DefaultSQLOperatorSet
}


/**
 * This is used to represent a syntax block where multiple operations are possible at the same point in the code.
 * For instance, this is used to create a select block, where up to 10 operators can follow a select statement.
 */
private[morpheus] trait AbstractSyntaxBlock {
  def syntax: AbstractSQLSyntax
}

/**
 * The AbstractQueryBuilder is designed to define the basic behaviour of an SQL query builder.
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
    SQLBuiltQuery(name)
      .forcePad.append(operators.eq)
      .forcePad.append(value)
  }

  def lt(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(name).forcePad.append(operators.lt).forcePad.append(value)
  }

  def lte(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(name).forcePad.append(operators.lte).forcePad.append(value)
  }

  def gt(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(name).forcePad.append(operators.gt).forcePad.append(value)
  }

  def gte(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(name).forcePad.append(operators.gte).forcePad.append(value)
  }

  def !=(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(name).forcePad.append(operators.`!=`).forcePad.append(value)
  }

  def <>(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(name).forcePad.append(operators.`<>`).forcePad.append(value)
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
    SQLBuiltQuery(name).pad.append(operators.in).wrap(values.mkString(", "))
  }

  def notIn(name: String, values: List[String]): SQLBuiltQuery = {
    SQLBuiltQuery(name).pad.append(operators.notIn).wrap(values.mkString(", "))
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
    qb.wrap(columns.mkString(", "))
      .forcePad.append(syntax.values)
      .wrap(values.mkString(", "))
  }

  def leftJoin(qb: SQLBuiltQuery, tableName: String): SQLBuiltQuery = {
    qb.pad
      .append(syntax.leftJoin)
      .forcePad.append(tableName)
  }

  def rightJoin(qb: SQLBuiltQuery, tableName: String): SQLBuiltQuery = {
    qb.pad
      .append(syntax.rightJoin)
      .forcePad.append(tableName)
  }

  def innerJoin(qb: SQLBuiltQuery, tableName: String): SQLBuiltQuery = {
    qb.pad
      .append(syntax.innerJoin)
      .forcePad.append(tableName)
  }

  def outerJoin(qb: SQLBuiltQuery, tableName: String): SQLBuiltQuery = {
    qb.pad
      .append(syntax.outerJoin)
      .forcePad.append(tableName)
  }

  def ifNotExists(qb: SQLBuiltQuery): SQLBuiltQuery = {
    qb.pad.append(syntax.ifNotExists)
  }

  def between(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(name)
      .forcePad.append(syntax.between)
      .forcePad.append(value)
  }

  def notBetween(name: String, value: String): SQLBuiltQuery = {
    SQLBuiltQuery(name)
      .forcePad.append(syntax.notBetween)
      .forcePad.append(value)
  }

  def on(qb: SQLBuiltQuery, clause: SQLBuiltQuery): SQLBuiltQuery = {
    qb.pad.append(syntax.on).forcePad.append(clause)
  }

  def exists(select: SQLBuiltQuery) = {
    SQLBuiltQuery(syntax.exists).wrap(select)
  }

  def notExists(select: SQLBuiltQuery) = {
    SQLBuiltQuery(syntax.notExists).wrap(select)
  }

  def ascii(value: String): SQLBuiltQuery = {
    SQLBuiltQuery(operators.ascii).wrap(value)
  }

  def bitLength(value: String): SQLBuiltQuery = {
    SQLBuiltQuery(operators.bitLength).wrap(value)
  }

  def charLength(value: String): SQLBuiltQuery = {
    SQLBuiltQuery(operators.charLength).wrap(value)
  }

  def characterLength(value: String): SQLBuiltQuery = {
    SQLBuiltQuery(operators.characterLength).wrap(value)
  }

  def concat(values: List[String]): SQLBuiltQuery = {
    SQLBuiltQuery(operators.concat).wrap(values.mkString(", "))
  }

  def concatWs(values: List[String]): SQLBuiltQuery = {
    SQLBuiltQuery(operators.concatWs).wrap(values.mkString(", "))
  }

  def bin(value: String): SQLBuiltQuery = {
    SQLBuiltQuery(operators.bin).wrap(value)
  }

  def engine(qb: SQLBuiltQuery, value: String): SQLBuiltQuery = {
    qb.pad.append(syntax.engine).forcePad.append(value)
  }

}




