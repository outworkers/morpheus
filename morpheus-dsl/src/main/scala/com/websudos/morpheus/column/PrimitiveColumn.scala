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

import java.util.Date

import com.websudos.morpheus.builder.SQLBuiltQuery
import com.websudos.morpheus.dsl.BaseTable
import com.websudos.morpheus.{Row, SQLPrimitive}
import org.joda.time.DateTime

import scala.annotation.implicitNotFound

private[morpheus] object KnownTypeLimits {
  val varcharLimit = 65536
  val textLimit = 65536
  val mediumTextLimit = 65536
  val charLimit = 255
  val longTextLimit = 65536
}

@implicitNotFound(msg = "Type ${RR} must be a MySQL primitive")
private[morpheus] class PrimitiveColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row, @specialized(Int, Double, Float, Long) RR : SQLPrimitive](t: BaseTable[T,
  R, TableRow]) extends Column[T, R, TableRow, RR](t) {

  def sqlType: String = implicitly[SQLPrimitive[RR]].sqlType

  def toQueryString(v: RR): String = implicitly[SQLPrimitive[RR]].toSQL(v)

  def qb: SQLBuiltQuery = SQLBuiltQuery(name).pad.append(sqlType)

  def optional(r: Row): Option[RR] = implicitly[SQLPrimitive[RR]].fromRow(r, name)
}

class AbstractLongColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: SQLPrimitive[Long])
  extends PrimitiveColumn[Owner, Record, TableRow, Long](table)

class AbstractDateColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: SQLPrimitive[Date])
  extends PrimitiveColumn[Owner, Record, TableRow, Date](table)

class AbstractDateTimeColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: SQLPrimitive[DateTime])
  extends PrimitiveColumn[Owner, Record, TableRow, DateTime](table)

class AbstractDoubleTimeColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: SQLPrimitive[Double])
  extends PrimitiveColumn[Owner, Record, TableRow, Double](table)

class AbstractFloatTimeColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: SQLPrimitive[Float])
  extends PrimitiveColumn[Owner, Record, TableRow, Float](table)
