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

import org.joda.time.DateTime

import com.twitter.finagle.exp.mysql._
import com.websudos.morpheus._

trait MySQLPrimitives {

  def apply[RR: SQLPrimitive]: SQLPrimitive[RR] = implicitly[SQLPrimitive[RR]]

  implicit object IntPrimitive extends DefaultIntPrimitive {
    def fromRow(row: MySQLRow, name: String): Option[Int] = row.res(name) map {
      case IntValue(num) => num
      case EmptyValue => 0
      case _ => throw InvalidTypeDefinitionException()
    }
  }

  implicit object FloatPrimitive extends DefaultFloatPrimitive {
    def fromRow(row: MySQLRow, name: String): Option[Float] = row.res(name) map {
      case FloatValue(num) => num
      case EmptyValue => 0
      case _ => throw InvalidTypeDefinitionException()
    }
  }

  implicit object DoublePrimitive extends DefaultDoublePrimitive {
    def fromRow(row: MySQLRow, name: String): Option[Double] = row.res(name) map {
      case DoubleValue(num) => num
      case EmptyValue => 0
      case _ => throw InvalidTypeDefinitionException()
    }
  }

  implicit object LongPrimitive extends DefaultLongPrimitive {
    def fromRow(row: MySQLRow, name: String): Option[Long] = row.res(name) map {
      case LongValue(num) => num
      case EmptyValue => 0L
      case _ => throw InvalidTypeDefinitionException()
    }
  }

  implicit object DatePrimitive extends DefaultDatePrimitive {
    def fromRow(row: MySQLRow, name: String): Option[Date] = row.res(name) map {
      case DateValue(date) => date
      case _ => throw InvalidTypeDefinitionException(s"Couldn't not parse a Date from column $name.")
    }
  }

  implicit object DateTimePrimitive extends DefaultDateTimePrimitive {
    def fromRow(row: MySQLRow, name: String): Option[DateTime] = row.res(name) map {
      case DateValue(date) => new DateTime(date)
      case _ => throw InvalidTypeDefinitionException(s"Couldn't not parse a DateTime from column $name.")
    }
  }

  implicit object StringPrimitive extends DefaultStringPrimitive {
    def fromRow(row: MySQLRow, name: String): Option[String] = row.res(name) match {
      case Some(value) => value match {
        case StringValue(str) => Some(str)
        case EmptyValue => Some("")
        case NullValue => None
        case _ => throw InvalidTypeDefinitionException()
      }
      case None => None
    }
  }
}
