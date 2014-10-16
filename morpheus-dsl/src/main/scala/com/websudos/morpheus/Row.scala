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

import com.twitter.util.Future

trait Row {

  def get[A](name: String): A

  def bool(name: String): Boolean = get[Boolean](name)

  def string(name: String): String = get[String](name)

  def byteBuffer(name: String): ByteBuffer = get[ByteBuffer](name)

  def int(name: String): Int = get[Int](name)

  def double(name: String): Double = get[Double](name)

  def float(name: String): Float = get[Float](name)

  def long(name: String): Long = get[Long](name)

  def bigInt(name: String): BigInt = get[BigInt](name)

  def bigDecimal(name: String): BigDecimal = get[BigDecimal](name)
}

trait Result {}

trait Client[+Row, Result] {

  def select[T](query: String)(f: Row => T): Future[Seq[T]]

  def query(query: String): Future[Result]
}
