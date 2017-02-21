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

package com.outworkers.morpheus.mysql

import java.util.Date

import com.outworkers.morpheus._
import com.outworkers.morpheus._
import org.joda.time.DateTime

import scala.util.Try

trait DataTypes {

  def apply[RR](implicit ev: DataType[RR]): DataType[RR] = ev

  implicit object IntPrimitive extends DefaultIntPrimitive {
    def fromRow(row: MySQLRow, name: String): Try[Int] = row.int(name)
  }

  implicit object FloatPrimitive extends DefaultFloatPrimitive {
    def fromRow(row: MySQLRow, name: String): Try[Float] = row.float(name)
  }

  implicit object DoublePrimitive extends DefaultDoublePrimitive {
    def fromRow(row: MySQLRow, name: String): Try[Double] = row.double(name)
  }

  implicit object LongPrimitive extends DefaultLongPrimitive {
    def fromRow(row: MySQLRow, name: String): Try[Long] = row.long(name)
  }

  implicit object DatePrimitive extends DefaultDatePrimitive {
    def fromRow(row: MySQLRow, name: String): Try[Date] = row.date(name)
  }

  implicit object DateTimePrimitive extends DefaultDateTimePrimitive {
    def fromRow(row: MySQLRow, name: String): Try[DateTime] = row.datetime(name)
  }

  implicit object StringPrimitive extends DefaultStringPrimitive {
    def fromRow(row: MySQLRow, name: String): Try[String] = row.string(name)
  }
}
