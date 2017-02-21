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

import java.nio.ByteBuffer
import java.util.Date

import com.twitter.finagle.exp.mysql.{Client => FinagleClient, Result => FinagleResult, ResultSet => FinagleResultSet, Row => FinagleRow, _}
import com.twitter.finagle.exp.mysql
import com.twitter.util.Future
import com.outworkers.morpheus.builder.{AbstractQueryBuilder, AbstractSQLSyntax, SQLOperatorSet}
import com.outworkers.morpheus.column.AbstractColumn
import com.outworkers.morpheus.query.AbstractQueryColumn
import com.outworkers.morpheus.{Client, Result => BaseResult, Row => BaseRow}

import scala.util.{Failure, Success, Try}

case class MySQLResult(result: FinagleResult) extends BaseResult

case class MySQLRow(res: FinagleRow) extends BaseRow {

  protected[this] def extract[T](column: String)(fn: Option[Value] => Try[T]): Try[T] = {
    fn(res(column))
  }

  override def string(name: String): Try[String] = {
    extract(name) {
      case Some(StringValue(value)) => Success(value)
      case x @ _ => Failure(new Exception(s"Invalid value $name for column $name, expected string, got $x"))
    }
  }

  override def byte(name: String): Try[Byte] = {
    extract(name) {
      case Some(ByteValue(value)) => Success(value)
      case x @ _ => Failure(new Exception(s"Invalid value $name for column $name, expected byte, got $x"))
    }
  }

  override def int(name: String): Try[Int] = {
    extract(name) {
      case Some(IntValue(value)) => Success(value)
      case x @ _ => Failure(new Exception(s"Invalid value $name for column $name, expected int, got $x"))
    }
  }

  override def date(name: String): Try[Date] = {
    extract(name) {
      case Some(DateValue(value)) => Success(value)
      case x @ _ => Failure(new Exception(s"Invalid value $name for column $name, expected int, got $x"))
    }
  }

  override def bigDecimal(name: String): BigDecimal = {
    val extracted = res.apply(name)
    extracted match {
      case Some(BigDecimalValue(value)) => value
      case _ => throw new Exception(s"Invalid value $extracted for column $name")
    }
  }

  override def double(name: String): Double = {
    val extracted = res.apply(name)
    extracted match {
      case Some(DoubleValue(value)) => value
      case _ => throw new Exception(s"Invalid value $extracted for column $name")
    }
  }

  override def float(name: String): Float = {
    val extracted = res.apply(name)
    extracted match {
      case Some(FloatValue(value)) => value
      case _ => throw new Exception(s"Invalid value $extracted for column $name")
    }
  }

  override def long(name: String): Long = {
    val extracted = res.apply(name)
    extracted match {
      case Some(LongValue(value)) => value
      case Some(StringValue(value)) => value.toLong
      case _ => throw new Exception(s"Invalid value $extracted for column $name")
    }
  }

  override def short(name: String): Short = {
    val extracted = res.apply(name)
    extracted match {
      case Some(ShortValue(value)) => value
      case _ => throw new Exception(s"Invalid value $extracted for column $name")
    }
  }

  override def bool(name: String): Boolean = {
    val extracted = res.apply(name)
    extracted match {
      case Some(mysql.StringValue("true")) => true
      case Some(mysql.StringValue("false")) => false
      case _ => throw new Exception(s"Invalid value $extracted for column $name")
    }
  }

  override def byteBuffer(name: String): ByteBuffer = {
    val extracted = res.apply(name)
    extracted match {
      case Some(mysql.RawValue(typ, charset, isBinary, bytes)) => ByteBuffer.wrap(bytes)
      case _ => throw new Exception(s"Invalid value $extracted for column $name")
    }
  }
}

class MySQLClient(val client: FinagleClient) extends Client[MySQLRow, MySQLResult] {

  def select[T](qb: String)(f: MySQLRow => T): Future[Seq[T]] = {
    // logger.info(s"Executing query $qb")
    client.query(qb).map {
      case set: FinagleResultSet => set.rows.map {
        row => f(MySQLRow(row))
      }
      case _ => Seq.empty[T]
    }
  }

  def query(query: String): Future[MySQLResult] = {
    client.query(query).map { res => MySQLResult(res) }
  }

}

object MySQLSyntax extends AbstractSQLSyntax {


  object SelectOptions {
    val distinctRow = "DISTINCTROW"
    val straightJoin = "STRAIGHT_JOIN"
    val sqlSmallResult = "SQL_SMALL_RESULT"
    val sqlBigResult = "SQL_BIG_RESULT"
    val sqlBufferResult = "SQL_BUFFER_RESULT"
    val sqlCache = "SQL_CACHE"
    val sqlNoCache = "SQL_NO_CACHE"
    val sqlCalcFoundRows = "SQL_CALC_FOUND_ROWS"
  }

  object Priorities {
    val lowPriority = "LOW_PRIORITY"
    val highPriority = "HIGH_PRIORITY"
  }

  object DeleteOptions {
    val ignore = "IGNORE"
    val quick = "QUICK"
  }

  object InsertOptions {
    val ignore = "IGNORE"
    val delayed = "DELAYED"
  }
}


object MySQLOperatorSet extends SQLOperatorSet

object MySQLQueryBuilder extends AbstractQueryBuilder {
  val operators = MySQLOperatorSet
  val syntax = MySQLSyntax
}

private[morpheus] class MySQLQueryColumn[T : DataType](col: AbstractColumn[T]) extends AbstractQueryColumn[T](col)
