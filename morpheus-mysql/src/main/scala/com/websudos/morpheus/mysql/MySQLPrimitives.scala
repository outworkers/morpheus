/*
 * Copyright 2014 websudos ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.websudos.morpheus.mysql

import java.util.Date

import com.websudos.morpheus._
import org.joda.time.DateTime

trait MySQLPrimitives {

  def apply[RR : SQLPrimitive]: SQLPrimitive[RR] = implicitly[SQLPrimitive[RR]]

  implicit object IntPrimitive extends DefaultIntPrimitive {
    def fromRow(row: MySQLRow, name: String): Option[Int] = Some(row.int(name))
  }

  implicit object FloatPrimitive extends DefaultFloatPrimitive {
    def fromRow(row: MySQLRow, name: String): Option[Float] = Some(row.float(name))
  }

  implicit object DoublePrimitive extends DefaultDoublePrimitive {
    def fromRow(row: MySQLRow, name: String): Option[Double] = Some(row.double(name))
  }

  implicit object LongPrimitive extends DefaultLongPrimitive {
    def fromRow(row: MySQLRow, name: String): Option[Long] = Some(row.long(name))
  }

  implicit object DatePrimitive extends DefaultDatePrimitive {
    def fromRow(row: MySQLRow, name: String): Option[Date] = Some(row.date(name))
  }

  implicit object DateTimePrimitive extends DefaultDateTimePrimitive {
    def fromRow(row: MySQLRow, name: String): Option[DateTime] = Some(row.datetime(name))
  }

  implicit object StringPrimitive extends DefaultStringPrimitive {
    def fromRow(row: MySQLRow, name: String): Option[String] = Some(row.string(name))
  }
}
