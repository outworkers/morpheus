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

package com.websudos.morpheus.keys

import com.websudos.morpheus.column.{NumericColumn, AbstractColumn}
import com.websudos.morpheus.query.{DefaultSQLSyntax, SQLBuiltQuery}

private[morpheus] trait Key[ValueType, KeyType <: Key[ValueType, KeyType]] {
  self: AbstractColumn[ValueType] =>

  override def qb: SQLBuiltQuery = {
    SQLBuiltQuery(name).pad.append(sqlType)
      .forcePad.append(keyToQueryString)
      .pad.append(notNull match {
      case true => DefaultSQLSyntax.notNull
      case false => ""
    }).pad.append(autoIncrement match {
      case true => DefaultSQLSyntax.autoIncrement
      case false => ""
    }).trim
  }


  protected[this] def keyToQueryString: String
  protected[this] val autoIncrement = false
}

trait PrimaryKey[ValueType] extends Key[ValueType, PrimaryKey[ValueType]] {
  self: AbstractColumn[ValueType] =>

  protected[this] def keyToQueryString = DefaultSQLSyntax.primaryKey
}

trait UniqueKey[ValueType] extends Key[ValueType, UniqueKey[ValueType]] {
  self: AbstractColumn[ValueType] =>

  protected[this] def keyToQueryString = DefaultSQLSyntax.uniqueKey

}

trait NotNull {
  self: AbstractColumn[_] =>

  override val notNull = true
}

trait Autoincrement {
  self: Key[Int, _] with AbstractColumn[Int] =>

  override protected[this] val autoIncrement = true
}

trait Zerofill[ValueType] {
  self: NumericColumn[_, _, ValueType] =>

}

trait Unsigned[ValueType] {
  self: NumericColumn[_, _, ValueType] =>
}
