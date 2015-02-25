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

import com.websudos.morpheus.builder.{SQLBuiltQuery, DefaultSQLDataTypes}
import com.websudos.morpheus.{Row, SQLPrimitive}
import com.websudos.morpheus.dsl.BaseTable

sealed abstract class NumericColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row, ValueType : Numeric : SQLPrimitive](t: BaseTable[T, R, TableRow], limit: Int =
0) extends PrimitiveColumn[T, R, TableRow, ValueType](t) {

  protected[this] def numericType: String

  override def sqlType: String = if (limit > 0) s"$numericType($limit)" else numericType

  override def qb: SQLBuiltQuery = SQLBuiltQuery(name).pad.append(sqlType)

  def unsigned: Boolean = false
}

abstract class AbstractTinyIntColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev:
SQLPrimitive[Int])
  extends NumericColumn[T, R, TableRow, Int](t, limit) {

  override protected[this] val numericType: String = DefaultSQLDataTypes.tinyInt
}

abstract class AbstractSmallIntColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev:
SQLPrimitive[Int])
extends NumericColumn[T, R, TableRow, Int](t, limit) {

  override protected[this] val numericType: String = DefaultSQLDataTypes.smallInt
}

abstract class AbstractMediumIntColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev:
SQLPrimitive[Int])
  extends NumericColumn[T, R, TableRow, Int](t, limit) {

  override protected[this] val numericType: String = DefaultSQLDataTypes.mediumInt
}

abstract class AbstractIntColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow], limit: Int = 0)(implicit ev:
SQLPrimitive[Int])
  extends NumericColumn[T, R, TableRow, Int](t, limit) {
  override protected[this] val numericType: String = DefaultSQLDataTypes.int
}

abstract class AbstractYearColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row](t: BaseTable[T, R, TableRow])(implicit ev: SQLPrimitive[Int])
  extends PrimitiveColumn[T, R, TableRow, Int](t) {
  override val sqlType = DefaultSQLDataTypes.year
}


