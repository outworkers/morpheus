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

package com.websudos.morpheus

import java.nio.ByteBuffer
import java.util.Date

import com.twitter.util.Future
import org.joda.time.DateTime

trait Row {

  def bool(name: String): Boolean = ???

  def byte(name: String): Byte = ???

  def string(name: String): String = ???

  def byteBuffer(name: String): ByteBuffer = ???

  def int(name: String): Int = ???

  def double(name: String): Double = ???

  def short(name: String): Short = ???

  def date(name: String): Date = ???

  def datetime(name: String): DateTime = new DateTime(date(name))

  def float(name: String): Float = ???

  def long(name: String): Long = ???

  def bigDecimal(name: String): BigDecimal = ???
}

trait Result {}

trait Client[+DBRow, DBResult] {

  def select[T](query: String)(f: DBRow => T): Future[Seq[T]]

  def query(query: String): Future[DBResult]
}
