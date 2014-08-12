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

import scala.annotation.implicitNotFound

import com.twitter.finagle.exp.mysql.Row
import com.websudos.morpheus.SQLPrimitives._
import com.websudos.morpheus.dsl.Table
import com.websudos.morpheus.query.{DefaultSQLDataTypes, SQLBuiltQuery}
import com.websudos.morpheus.{SQLPrimitive, SQLPrimitives}




@implicitNotFound(msg = "Type ${RR} must be a MySQL primitive")
private[morpheus] class PrimitiveColumn[T <: Table[T, R], R, @specialized(Int, Double, Float, Long) RR : SQLPrimitive](t: Table[T, R])
  extends Column[T, R, RR](t) {

  def sqlType: String = SQLPrimitives[RR].sqlType
  def toQueryString(v: RR): String = SQLPrimitives[RR].toSQL(v)

  def qb: SQLBuiltQuery = SQLBuiltQuery(name).pad.append(sqlType)

  def optional(r: Row): Option[RR] =
    implicitly[SQLPrimitive[RR]].fromRow(r, name)
}

class TinyIntColumn[T <: Table[T, R], R](t: Table[T, R]) extends PrimitiveColumn[T, R, Int](t) {

  val primitive = IntIsSQLPrimitive
  override val sqlType: String = DefaultSQLDataTypes.tinyInt
}

class SmallIntColumn[T <: Table[T, R], R](t: Table[T, R]) extends PrimitiveColumn[T, R, Int](t) {
  override val sqlType = DefaultSQLDataTypes.smallInt
}

class MediumIntColumn[T <: Table[T, R], R](t: Table[T, R]) extends PrimitiveColumn[T, R, Int](t) {
  override val sqlType = DefaultSQLDataTypes.mediumInt
}


sealed abstract class LimitedTextColumn[T <: Table[T, R], R](t: Table[T, R], protected[this] val limit: Int) extends PrimitiveColumn[T, R, String](t)
