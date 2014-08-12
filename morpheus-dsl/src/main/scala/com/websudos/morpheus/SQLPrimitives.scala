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

package com.websudos.morpheus


import java.util.Date

import org.joda.time.DateTime

import com.twitter.finagle.exp.mysql._

case class InvalidTypeDefinitionException(msg: String = "Invalid SQL type declared for column") extends RuntimeException(msg)

// sealed case class BooleanValue(f: Boolean) extends Value

trait SQLPrimitive[T] {

  def sqlType: String

  def fromRow(row: Row, name: String): Option[T]

  def toSQL(value: T): String
}

trait SQLPrimitives {

  def apply[RR: SQLPrimitive]: SQLPrimitive[RR] = implicitly[SQLPrimitive[RR]]

  implicit object IntIsSQLPrimitive extends SQLPrimitive[Int] {
    val sqlType = "INT"

    def fromRow(row: Row, name: String): Option[Int] = row(name) map {
      case IntValue(num) => num
      case EmptyValue => 0
      case _ => throw InvalidTypeDefinitionException()
    }

    def toSQL(value: Int): String = value.toString
  }

  implicit object FloatIsSQLPrimitive extends SQLPrimitive[Float] {
    override def sqlType: String = "FLOAT"

    override def fromRow(row: Row, name: String): Option[Float] = row(name) map {
      case FloatValue(num) => num
      case EmptyValue => 0
      case _ => throw InvalidTypeDefinitionException()
    }

    override def toSQL(value: Float): String = value.toString
  }

  implicit object DoubleIsSQLPrimitive extends SQLPrimitive[Double] {
    override def sqlType: String = "DOUBLE"

    override def fromRow(row: Row, name: String): Option[Double] = row(name) map {
      case DoubleValue(num) => num
      case EmptyValue => 0
      case _ => throw InvalidTypeDefinitionException()
    }

    override def toSQL(value: Double): String = value.toString
  }

  implicit object LongIsSQLPrimitive extends SQLPrimitive[Long] {

    val sqlType = "LONG"

    def fromRow(row: Row, name: String): Option[Long] = row(name) map {
      case LongValue(num) => num
      case EmptyValue => 0L
      case _ => throw InvalidTypeDefinitionException()
    }

    def toSQL(value: Long): String = value.toString

  }


  implicit object DateIsSQLPrimitive extends SQLPrimitive[Date] {
    override val sqlType = "DATE"

    def fromRow(row: Row, name: String): Option[Date] = row(name) map {
      case DateValue(date) => date
      case _ => throw InvalidTypeDefinitionException(s"Couldn't not parse a Date from column $name.")
    }

    def toSQL(value: Date): String = value.toString
  }

  /*
  implicit object BooleanIsSQLPrimitive extends SQLPrimitive[Boolean] {
    val sqlType = "boolean"

    def fromRow(row: Row, name: String): Option[Boolean] = row(name) map {
      case Value(true) => true
      case false => false
      case _ => throw InvalidTypeDefinitionException()
    }

    def toSQL(value: Boolean): String = value.toString
  }*/

  implicit object DateTimeIsSQLPrimitive extends SQLPrimitive[DateTime] {
    override val sqlType: String = "DATE"

    override def fromRow(row: Row, name: String): Option[DateTime] = row(name) map {
      case DateValue(date) => new DateTime(date)
      case _ => throw InvalidTypeDefinitionException(s"Couldn't not parse a DateTime from column $name.")
    }

    override def toSQL(value: DateTime): String = value.toString
  }

  implicit object StringIsSQLPrimitive extends SQLPrimitive[String] {

    override val sqlType = "STRING"

    def fromRow(row: Row, name: String) = row(name) match {
      case Some(value) => value match {
        case StringValue(str) => Some(str)
        case EmptyValue => Some("")
        case NullValue => None
        case _ => throw InvalidTypeDefinitionException()
      }

      case None => None
    }

    def toSQL(value: String): String = "'" + value + "'"
  }
}



object SQLPrimitives extends SQLPrimitives
