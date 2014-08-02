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

private[morpheus] trait SQLPrimitive[T] {

  def sqlType: String

  def fromRow(row: Row, name: String): Option[T]

  def toSQL(value: T): AnyRef
}

trait SQLPrimitives {

  def apply[RR: SQLPrimitive]: SQLPrimitive[RR] = implicitly[SQLPrimitive[RR]]

  implicit object LongIsSQLPrimitive extends SQLPrimitive[Long] {

    val sqlType = "long"

    def fromRow(row: Row, name: String): Option[Long] = row(name) map {
      case LongValue(num) => num
      case EmptyValue => 0L
    }

    def toSQL(value: Long): AnyRef = value.asInstanceOf[AnyRef]

  }

  implicit object StringIsSQLPrimitive extends SQLPrimitive[String] {

    val sqlType = "string"

    def fromRow(row: Row, name: String) = row(name) match {
      case Some(value) => value match {
        case StringValue(str) => Some(str)
        case EmptyValue => Some("")
        case NullValue => None
      }

      case None => None
    }

    def toSQL(value: String): AnyRef = value.asInstanceOf[AnyRef]
  }

}

object SQLPrimitives extends SQLPrimitives
