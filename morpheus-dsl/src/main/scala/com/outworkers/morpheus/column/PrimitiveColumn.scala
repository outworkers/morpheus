/*
 * Copyright 2013 - 2017 Outworkers, Limited.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Explicit consent must be obtained from the copyright owner, Websudos Limited before any redistribution is made.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.outworkers.morpheus.column

import java.util.Date

import com.outworkers.morpheus.DataType
import com.outworkers.morpheus.builder.SQLBuiltQuery
import com.outworkers.morpheus.dsl.BaseTable
import com.outworkers.morpheus.Row
import org.joda.time.DateTime

import scala.annotation.implicitNotFound
import scala.util.Try

private[morpheus] object KnownTypeLimits {
  val varcharLimit = 65536
  val textLimit = 65536
  val mediumTextLimit = 65536
  val charLimit = 255
  val longTextLimit = 65536
}

@implicitNotFound(msg = "Type ${RR} must be a MySQL primitive")
private[morpheus] class PrimitiveColumn[T <: BaseTable[T, R, TableRow], R, TableRow <: Row, @specialized(Int, Double, Float, Long) RR : DataType](t: BaseTable[T,
  R, TableRow]) extends Column[T, R, TableRow, RR](t) {

  def sqlType: String = implicitly[DataType[RR]].sqlType

  def toQueryString(v: RR): String = implicitly[DataType[RR]].serialize(v)

  def qb: SQLBuiltQuery = SQLBuiltQuery(name).pad.append(sqlType)

  def optional(r: Row): Try[RR] = implicitly[DataType[RR]].deserialize(r, name)
}

class AbstractLongColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: DataType[Long])
  extends PrimitiveColumn[Owner, Record, TableRow, Long](table)

class AbstractDateColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: DataType[Date])
  extends PrimitiveColumn[Owner, Record, TableRow, Date](table)

class AbstractDateTimeColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: DataType[DateTime])
  extends PrimitiveColumn[Owner, Record, TableRow, DateTime](table)

class AbstractDoubleTimeColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: DataType[Double])
  extends PrimitiveColumn[Owner, Record, TableRow, Double](table)

class AbstractFloatTimeColumn[
  Owner <: BaseTable[Owner, Record, TableRow],
  Record,
  TableRow <: Row
](table: BaseTable[Owner, Record, TableRow])(implicit ev: DataType[Float])
  extends PrimitiveColumn[Owner, Record, TableRow, Float](table)
