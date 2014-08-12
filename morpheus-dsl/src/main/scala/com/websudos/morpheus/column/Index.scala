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

package com.websudos.morpheus.column

import com.twitter.finagle.exp.mysql.Row
import com.websudos.morpheus.dsl.Table
import com.websudos.morpheus.query.{DefaultSQLSyntax, SQLBuiltQuery}


private[morpheus] trait IndexColumn[T] {
  def apply(r: Row): T = throw new Exception(s"Index column is not a value column. This apply method cannot extract anything from it.")
}

/**
 * This implements an SQL index column. With this the user can define indexes on a table.
 * An Index is also an implementation of an Indexed column, meaning it holds no concrete value to be extracted.
 * @param columns The columns that form the
 * @tparam T
 * @tparam R
 */
class Index[T <: Table[T, R], R](columns: AbstractColumn[_]*) extends AbstractColumn[String] with IndexColumn[String] {

  def qb: SQLBuiltQuery = {
    SQLBuiltQuery(DefaultSQLSyntax.index)
      .forcePad.append(DefaultSQLSyntax.`(`)
      .append(columns.map(_.name).mkString(", "))
      .append(DefaultSQLSyntax.`)`)
  }

  override def sqlType: String = "string"

  override def toQueryString(v: String): String = v

  override def table: Table[_, _] = columns.head.table
}

/**
 * This is a simple mechanism of providing a pre-defined set of FOREIGN KEY constraints.
 * The available values currently follow the MySQL syntax definition available {@link http://dev.mysql.com/doc/refman/5.6/en/create-table-foreign-keys.html}.
 *
 * What follows below is actually the implementation of an Enumeration, however `scala.util.Enum` is not proffered in situations like this,
 * or ever for that matter. The API is poor and weird compared to using a simple set of `case object` definitions.
 * @param value String
 */
sealed class ForeignKeyConstraint(val value: String)


/**
 * This trait encloses all default variations of a FOREIGN KEY constraint as per the MySQL documentation.
 * It is a trait so that the default set of MySQL imports.
 *
 */
private[morpheus] trait DefaultForeignKeyConstraints {
  case object Restrict extends ForeignKeyConstraint(DefaultSQLSyntax.restrict)
  case object Cascade extends ForeignKeyConstraint(DefaultSQLSyntax.cascade)
  case object SetNull extends ForeignKeyConstraint(DefaultSQLSyntax.setNull)
  case object NoAction extends ForeignKeyConstraint(DefaultSQLSyntax.noAction)
}

private[morpheus] object DefaultForeignKeyConstraints extends DefaultForeignKeyConstraints


/**
 * This is the implementation of a ForeignKey column. This is not a value column, therefore the `apply` method is overridden to throw an exception. It is used
 * at reflection time and schema generation time to correctly create the schema for a given table.
 *
 * By default the action performed is DefaultForeignKeyConstraints.NoAction, with respect to the MySQL behaviour.
 *
 * @param table The table owning the columns to which this foreign key holds a reference to.
 * @param columns The columns this foreign key references.
 * @tparam T The type of the owner table.
 * @tparam R The type of the record.
 */
abstract class ForeignKey[T <: Table[T, R], R](table: T, columns: Column[T, R, _]*) extends AbstractColumn[String] with IndexColumn[String] {

  def qb: SQLBuiltQuery = {
    val default = SQLBuiltQuery(DefaultSQLSyntax.foreignKey)
      .forcePad.append(DefaultSQLSyntax.references)
      .forcePad.append(table.tableName)
      .forcePad.append(columns.map(_.name).mkString(", "))

    val stage2 = if (onUpdate != DefaultForeignKeyConstraints.NoAction) {
      default.forcePad.append(DefaultSQLSyntax.onUpdate).forcePad.append(onUpdate.value)
    } else {
      default
    }

    val stage3 = if (onDelete != DefaultForeignKeyConstraints.NoAction) {
      stage2.forcePad.append(DefaultSQLSyntax.onDelete).forcePad.append(onDelete.value)
    } else {
      stage2
    }
    stage3
  }

  def onUpdate: ForeignKeyConstraint = DefaultForeignKeyConstraints.NoAction

  def onDelete: ForeignKeyConstraint = DefaultForeignKeyConstraints.NoAction

}
