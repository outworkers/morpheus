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

package com.websudos.morpheus.mysql

import java.util.Date
import org.joda.time.DateTime

import com.twitter.finagle.exp.mysql._
import com.websudos.morpheus.InvalidTypeDefinitionException
import com.websudos.morpheus.column.AbstractColumn
import com.websudos.morpheus.query._

trait SQLPrimitives {

  def apply[RR: SQLPrimitive]: SQLPrimitive[RR] = implicitly[SQLPrimitive[RR]]

  implicit object IntIsSQLPrimitive extends SQLPrimitive[Int] {
    val sqlType = DefaultSQLDataTypes.int

    def fromRow(row: Row, name: String): Option[Int] = row(name) map {
      case IntValue(num) => num
      case EmptyValue => 0
      case _ => throw InvalidTypeDefinitionException()
    }

    def toSQL(value: Int): String = value.toString
  }

  implicit object FloatIsSQLPrimitive extends SQLPrimitive[Float] {
    override def sqlType: String = DefaultSQLDataTypes.float

    override def fromRow(row: Row, name: String): Option[Float] = row(name) map {
      case FloatValue(num) => num
      case EmptyValue => 0
      case _ => throw InvalidTypeDefinitionException()
    }

    override def toSQL(value: Float): String = value.toString
  }

  implicit object DoubleIsSQLPrimitive extends SQLPrimitive[Double] {
    override def sqlType: String = DefaultSQLDataTypes.double

    override def fromRow(row: Row, name: String): Option[Double] = row(name) map {
      case DoubleValue(num) => num
      case EmptyValue => 0
      case _ => throw InvalidTypeDefinitionException()
    }

    override def toSQL(value: Double): String = value.toString
  }

  implicit object LongIsSQLPrimitive extends SQLPrimitive[Long] {

    val sqlType = DefaultSQLDataTypes.long

    def fromRow(row: Row, name: String): Option[Long] = row(name) map {
      case LongValue(num) => num
      case EmptyValue => 0L
      case _ => throw InvalidTypeDefinitionException()
    }

    def toSQL(value: Long): String = value.toString

  }


  implicit object DateIsSQLPrimitive extends SQLPrimitive[Date] {
    override val sqlType = DefaultSQLDataTypes.date

    def fromRow(row: Row, name: String): Option[Date] = row(name) map {
      case DateValue(date) => date
      case _ => throw InvalidTypeDefinitionException(s"Couldn't not parse a Date from column $name.")
    }

    def toSQL(value: Date): String = value.toString
  }

  implicit object DateTimeIsSQLPrimitive extends SQLPrimitive[DateTime] {
    override val sqlType: String = DefaultSQLDataTypes.date

    override def fromRow(row: Row, name: String): Option[DateTime] = row(name) map {
      case DateValue(date) => new DateTime(date)
      case _ => throw InvalidTypeDefinitionException(s"Couldn't not parse a DateTime from column $name.")
    }

    override def toSQL(value: DateTime): String = value.toString
  }

  implicit object StringIsSQLPrimitive extends SQLPrimitive[String] {

    override val sqlType = DefaultSQLDataTypes.text

    def fromRow(row: Row, name: String): Option[String] = row(name) match {
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


trait MySQLPrimitives extends SQLPrimitives {}

object MySQLSyntax extends AbstractSQLSyntax {
  val distinctRow = "DISTINCTROW"
  val lowPriority = "LOW_PRIORITY"
  val highPriority = "HIGH_PRIORITY"
  val delayed = "DELAYED"
  val straightJoin = "STRAIGHT_JOIN"
  val sqlSmallResult = "SQL_SMALL_RESULT"
  val sqlBigResult = "SQL_BIG_RESULT"
  val sqlBufferResult = "SQL_BUFFER_RESULT"
  val sqlCache = "SQL_CACHE"
  val sqlNoCache = "SQL_NO_CACHE"
  val sqlCalcFoundRows = "SQL_CALC_FOUND_ROWS"
}


object MySQLOperatorSet extends SQLOperatorSet

object MySQLQueryBuilder extends AbstractQueryBuilder {
  val operators = MySQLOperatorSet
  val syntax = MySQLSyntax
}

private[morpheus] class MySQLQueryColumn[T : SQLPrimitive](col: AbstractColumn[T]) extends AbstractQueryColumn[T](col)
