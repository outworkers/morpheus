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

import com.websudos.morpheus.SQLPrimitive
import com.websudos.morpheus.SQLPrimitives.IntIsSQLPrimitive
import com.websudos.morpheus.dsl.Table
import com.websudos.morpheus.query.{SQLBuiltQuery, DefaultSQLDataTypes}


sealed abstract class NumericColumn[T <: Table[T, R], R, ValueType : Numeric : SQLPrimitive](t: Table[T, R], limit: Int = 0) extends PrimitiveColumn[T, R,
  ValueType](t) {

  protected[this] def numericType: String

  override def sqlType = if (limit > 0) s"$numericType($limit)" else numericType

  override def qb: SQLBuiltQuery = SQLBuiltQuery(name).pad.append(sqlType)
}

class TinyIntColumn[T <: Table[T, R], R](t: Table[T, R], limit: Int = 0) extends NumericColumn[T, R, Int](t, limit) {
  val primitive = IntIsSQLPrimitive
  override protected[this] val numericType: String = DefaultSQLDataTypes.tinyInt
}

class SmallIntColumn[T <: Table[T, R], R](t: Table[T, R], limit: Int = 0) extends NumericColumn[T, R, Int](t, limit) {
  override protected[this] val numericType: String = DefaultSQLDataTypes.smallInt
}

class MediumIntColumn[T <: Table[T, R], R](t: Table[T, R], limit: Int = 0) extends NumericColumn[T, R, Int](t, limit) {

  override protected[this] val numericType: String = DefaultSQLDataTypes.mediumInt
}

class IntColumn[T <: Table[T, R], R](t: Table[T, R], limit: Int = 0) extends NumericColumn[T, R, Int](t, limit) {
  override protected[this] val numericType: String = DefaultSQLDataTypes.int
}

class YearColumn[T <: Table[T, R], R](t: Table[T, R]) extends PrimitiveColumn[T, R, Int](t) {
  override val sqlType = DefaultSQLDataTypes.year
}


