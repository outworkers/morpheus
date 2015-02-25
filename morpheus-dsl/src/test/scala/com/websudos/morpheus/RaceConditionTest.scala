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

import java.util.concurrent.{Callable, Executors}

import scala.collection.JavaConversions._

import org.scalatest.FunSuite

import com.websudos.morpheus.sql._

class RaceConditionTest extends FunSuite {

  case class Group(id: Long, name: String)

  class Groups extends Table[Groups, Group] {
    object id extends LongColumn(this)
    object name extends TextColumn(this)

    def fromRow(row: DefaultRow) = Group(id(row), name(row))
  }

  case class User(id: Long, name: String)

  class Users extends Table[Users, User] {
    object id extends LongColumn(this)
    object name extends TextColumn(this)

    def fromRow(row: DefaultRow) = User(id(row), name(row))
  }

  test("parallel tables instantiation") {
    val executor = Executors.newFixedThreadPool(2)
    val futureResults = executor.invokeAll(List(
      new Callable[AnyRef] {
        override def call() = new Users
      },
      new Callable[AnyRef] {
        override def call() = new Groups
      }
    ))
    futureResults.map(_.get())
  }

  test("sequential tables instantiation") {
    new Groups
    new Users
  }
}
