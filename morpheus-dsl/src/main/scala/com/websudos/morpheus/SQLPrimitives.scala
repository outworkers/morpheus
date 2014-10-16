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

package com.websudos.morpheus

import java.util.Date

import org.joda.time.DateTime

import com.websudos.morpheus.query.{DefaultQueryBuilder, DefaultSQLDataTypes}
import com.twitter.util.Try

case class InvalidTypeDefinitionException(msg: String = "Invalid SQL type declared for column") extends RuntimeException(msg)


/**
 * The innocent reader may now try and ask the question "What does this do?".
 * The type class approach is meant to limit the amount of Data types we inherently recognise as defaults or primitives.
 *
 * This allows for a very simple limitation mechanism where we exploit the basic Scala implicit resolution mechanism to perform efficient type restrictions
 * and encoding/decoding to/from database results.
 * If a type needs serialisation, we simply request for type evidence via the below type class using {@code}implicity[SQLPrimitive[T]]{code}.
 *
 * If proof exists that the type T has an SQLPrimitive type class associated with it, then the type is considered primitive with regards to the current SQL
 * database in use. This means we will allow for variation of primitive type implementations between different SQL databases. The challenge arises from
 * needing to parse Primitives implicitly in the DSL module itself to create the basic extendable functionality.
 *
 * We then need to allow other database specific implementations to override these implementations and pass the new ones explicity. As this
 * encapsulates very important information, such as how to decode a specific type from a given database implementation when a query is performed,
 * the breach of this contract means we might as well start another project.
 *
 * Any below weirdness or poor choice of semantics is probably the only possible way through which the above was achieved
 * This involves defining the default extendable types as normal classes instead of traits to allow their instantiation as overridable vals.
 *
 * @tparam T The primitive SQL data type to create a type class for.
 */
trait SQLPrimitive[T] {

  def sqlType: String

  def toSQL(value: T): String

  def fromRow(row: Row, name: String): Option[T]
}

class DefaultIntPrimitive extends SQLPrimitive[Int] {
  override def sqlType: String = DefaultSQLDataTypes.int

  override def toSQL(value: Int): String = value.toString

  def fromRow(row: Row, name: String): Option[Int] = Try(row.int(name)).toOption
}

class DefaultFloatPrimitive extends SQLPrimitive[Float] {
  override def sqlType: String = DefaultSQLDataTypes.float

  override def toSQL(value: Float): String = value.toString

  def fromRow(row: Row, name: String): Option[Float] = Try(row.float(name)).toOption
}

class DefaultDoublePrimitive extends SQLPrimitive[Double] {
  override def sqlType: String = DefaultSQLDataTypes.double

  override def toSQL(value: Double): String = value.toString

  def fromRow(row: Row, name: String): Option[Double] = Try(row.double(name)).toOption
}

class DefaultLongPrimitive extends SQLPrimitive[Long] {
  override def sqlType: String = DefaultSQLDataTypes.long

  override def toSQL(value: Long): String = value.toString

  def fromRow(row: Row, name: String): Option[Long] = Try(row.long(name)).toOption
}

class DefaultDatePrimitive extends SQLPrimitive[Date] {
  def sqlType: String = DefaultSQLDataTypes.date

  def toSQL(value: Date): String = value.toString

  def fromRow(row: Row, name: String): Option[Date] = Try(new Date(row.long(name))).toOption
}

class DefaultDateTimePrimitive extends SQLPrimitive[DateTime] {
  def sqlType: String = DefaultSQLDataTypes.dateTime

  def toSQL(value: DateTime): String = value.toString

  def fromRow(row: Row, name: String): Option[DateTime] = Try(new DateTime(row.long(name))).toOption
}

class DefaultStringPrimitive extends SQLPrimitive[String] {

  override def sqlType: String = DefaultSQLDataTypes.text

  override def toSQL(value: String): String = DefaultQueryBuilder.escape(value)

  def fromRow(row: Row, name: String): Option[String] = Try(row.string(name)).toOption
}

trait DefaultSQLPrimitives {

  implicit def IntPrimitive: SQLPrimitive[Int]

  implicit def StringPrimitive: SQLPrimitive[String]

  implicit def FloatPrimitive: SQLPrimitive[Float]

  implicit def DatePrimitive: SQLPrimitive[Date]

  implicit def DateTimePrimitive: SQLPrimitive[DateTime]

  implicit def DoublePrimitive: SQLPrimitive[Double]

  implicit def LongPrimitive: SQLPrimitive[Long]
}

trait MaterialisedPrimitives {
  implicit object IntPrimitive extends DefaultIntPrimitive

  implicit object StringPrimitive extends DefaultStringPrimitive

  implicit object FloatPrimitive extends DefaultFloatPrimitive

  implicit object DatePrimitive extends DefaultDatePrimitive

  implicit object DateTimePrimitive extends DefaultDateTimePrimitive

  implicit object DoublePrimitive extends DefaultDoublePrimitive

  implicit object LongPrimitive extends DefaultLongPrimitive
}

object SQLPrimitives extends MaterialisedPrimitives {
  def apply[T : SQLPrimitive]: SQLPrimitive[T] = implicitly[SQLPrimitive[T]]
}
