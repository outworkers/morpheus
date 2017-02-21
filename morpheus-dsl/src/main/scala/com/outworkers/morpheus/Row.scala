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
package com.outworkers.morpheus

import java.nio.ByteBuffer
import java.util.Date

import com.twitter.util.Future
import org.joda.time.{DateTime, DateTimeZone}

import scala.util.Try

trait Row {

  def bool(name: String): Try[Boolean]

  def byte(name: String): Try[Byte]

  def string(name: String): Try[String]

  def byteBuffer(name: String): Try[ByteBuffer]

  def int(name: String): Try[Int]

  def double(name: String): Try[Double]

  def short(name: String): Try[Short]

  def date(name: String): Try[Date]

  def datetime(name: String): Try[DateTime] = date(name) map(d => new DateTime(d, DateTimeZone.UTC))

  def float(name: String): Try[Float]

  def long(name: String): Try[Long]

  def bigDecimal(name: String): Try[BigDecimal]
}

trait Result

trait Client[+DBRow, DBResult] {

  def select[T](query: String)(f: DBRow => T): Future[Seq[T]]

  def query(query: String): Future[DBResult]
}
