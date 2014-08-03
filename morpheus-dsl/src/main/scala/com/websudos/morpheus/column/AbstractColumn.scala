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
import com.websudos.morpheus.{ SQLPrimitive, SQLPrimitives }
import com.websudos.morpheus.dsl.Table

private[morpheus] trait AbstractColumn[@specialized(Int, Double, Float, Long, Boolean, Short) T] {

  type Value = T

  lazy val name: String = getClass.getSimpleName.replaceAll("\\$+", "").replaceAll("(anonfun\\d+.+\\d+)|", "")

  def sqlType: String

  def table: Table[_, _]

  def toQueryString(v: T): String
}


abstract class Column[Owner <: Table[Owner, Record], Record, T](val table: Table[Owner, Record]) extends AbstractColumn[T] {

  def optional(r: Row): Option[T]

  def apply(r: Row): T = optional(r).getOrElse(throw new Exception(s"can't extract required value for column '$name'"))
}


@implicitNotFound(msg = "Type ${RR} must be a Cassandra primitive")
class PrimitiveColumn[T <: Table[T, R], R, @specialized(Int, Double, Float, Long) RR: SQLPrimitive](t: Table[T, R])
  extends Column[T, R, RR](t) {

  def sqlType: String = SQLPrimitives[RR].sqlType
  def toQueryString(v: RR): AnyRef = SQLPrimitives[RR].toSQL(v)

  def optional(r: Row): Option[RR] =
    implicitly[SQLPrimitive[RR]].fromRow(r, name)
}
