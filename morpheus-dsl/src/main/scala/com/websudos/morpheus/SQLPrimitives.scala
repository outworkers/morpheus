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

  implicit object LongIsSQLPrimitive extends SQLPrimitive[Long] {

    val sqlType = "long"

    def fromRow(row: Row, name: String): Option[Long] = row(name) map {
      case LongValue(num) => num
      case EmptyValue => 0L
      case _ => throw InvalidTypeDefinitionException()
    }

    def toSQL(value: Long): String = value.toString

  }

  implicit object IntIsSQLPrimitive extends SQLPrimitive[Int] {
    val sqlType = "int"

    def fromRow(row: Row, name: String): Option[Int] = row(name) map {
      case IntValue(num) => num
      case EmptyValue => 0
      case _ => throw InvalidTypeDefinitionException()
    }

    def toSQL(value: Int): String = value.toString
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

  implicit object StringIsSQLPrimitive extends SQLPrimitive[String] {

    val sqlType = "string"

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
