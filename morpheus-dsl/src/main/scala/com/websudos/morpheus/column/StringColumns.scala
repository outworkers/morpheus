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

package com.websudos.morpheus.column

import com.websudos.morpheus.builder.DefaultSQLDataTypes
import com.websudos.morpheus.dsl.BaseTable
import com.websudos.morpheus.{Row, SQLPrimitive}

abstract class LimitedTextColumn[
  T <: BaseTable[T, R, TableRow],
  R, TableRow <: Row,
  RR : SQLPrimitive
](t: BaseTable[T, R, TableRow], protected[this] val limit: Int) extends PrimitiveColumn[T, R, TableRow, RR](t)

abstract class UnlimitedTextColumn[
  T <: BaseTable[T, R, TableRow],
  R, TableRow <: Row,
  RR : SQLPrimitive
](t: BaseTable[T, R, TableRow]) extends PrimitiveColumn[T, R, TableRow, RR](t)


abstract class AbstractCharColumn[
  T <: BaseTable[T, R, TableRow],
  R,
  TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = KnownTypeLimits.charLimit)
  (implicit ev: SQLPrimitive[String]) extends LimitedTextColumn[T, R, TableRow, String](t, limit) {

  override def sqlType: String = s"${DefaultSQLDataTypes.char}($limit)"
}

abstract class AbstractVarcharColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = KnownTypeLimits
  .varcharLimit)(implicit ev: SQLPrimitive[String]) extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = s"${DefaultSQLDataTypes.varchar}($limit)"
}

abstract class AbstractTinyTextColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
  extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.tinyText
}

abstract class AbstractTextColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
  extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.text
}

abstract class AbstractMediumTextColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
  extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.mediumText
}

abstract class AbstractLongTextColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
  extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.longText
}

abstract class AbstractTinyBlobColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
  extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.tinyBlob
}

abstract class AbstractBlobColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
  extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.blob
}

abstract class AbstractMediumBlobColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
  extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.mediumBlob
}

abstract class AbstractLongBlobColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
  extends LimitedTextColumn[T, R, TableRow, String](t, limit) {
  override def sqlType: String = DefaultSQLDataTypes.longBlob
}

