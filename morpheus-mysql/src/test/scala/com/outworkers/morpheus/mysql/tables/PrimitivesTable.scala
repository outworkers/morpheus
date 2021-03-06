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
package com.outworkers.morpheus.mysql.tables

import com.outworkers.morpheus.keys.Autoincrement
import org.joda.time.DateTime
import com.outworkers.morpheus.mysql.dsl._
import com.outworkers.morpheus.mysql.query.InsertQuery

case class PrimitiveRecord(
  id: Int,
  num: Int,
  double: Double,
  real: Double,
  float: Float,
  long: Long,
  date: java.util.Date,
  dateTime: DateTime,
  str: String
)

class PrimitivesTable extends Table[PrimitivesTable, PrimitiveRecord] {
  object id extends IntColumn(this) with PrimaryKey with Autoincrement with NotNull
  object num extends IntColumn(this)
  object doubleColumn extends DoubleColumn(this)
  object floatColumn extends FloatColumn(this, 10)
  object realColumn extends RealColumn(this)
  object longColumn extends LongColumn(this)
  object dateColumn extends DateColumn(this)
  object datetimeColumn extends DateTimeColumn(this)
  object strColumn extends VarcharColumn(this, 256)

  override def tableName: String = "primitives_table"

  /**
    * The most notable and honorable of functions in this file, this is what allows our DSL to provide type-safety.
    * It works by requiring a user to define a type-safe mapping between a buffered Result and the above refined Record.
    *
    * Objects delimiting pre-defined columns also have a pre-defined "apply" method, allowing the user to simply autofill the type-safe mapping by using
    * pre-existing definitions.
    *
    * @param row The row incoming as a result from a MySQL query.
    * @return A Record instance.
    */
  override def fromRow(row: Row): PrimitiveRecord = {
    PrimitiveRecord(
      id(row),
      num(row),
      doubleColumn(row),
      realColumn(row),
      floatColumn(row),
      longColumn(row),
      dateColumn(row),
      datetimeColumn(row),
      strColumn(row)
    )
  }

  def store(rec: PrimitiveRecord): InsertQuery.Default[PrimitivesTable, PrimitiveRecord] = {
    insert
      .value(_.id, rec.id)
      .value(_.num, rec.num)
      .value(_.doubleColumn, rec.double)
      .value(_.realColumn, rec.real)
      .value(_.floatColumn, rec.float)
      .value(_.longColumn, rec.long)
      .value(_.dateColumn, rec.date)
      .value(_.datetimeColumn, rec.dateTime)
      .value(_.strColumn, rec.str)
  }
}

object PrimitivesTable extends PrimitivesTable