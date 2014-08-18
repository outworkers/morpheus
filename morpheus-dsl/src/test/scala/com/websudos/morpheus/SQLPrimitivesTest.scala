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

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import com.websudos.morpheus.sql._

class SQLPrimitivesTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {

  "The SQL String primitive" should "always use '(apostrophes) around the serialised strings" in {
    forAll(minSuccessful(300)) { (name: String) =>
      whenever (name.length > 0) {
        val query = implicitly[SQLPrimitive[String]].toSQL(name)
        query shouldEqual s"'$name'"
      }
    }
  }

  "The SQL Long primitive" should "serialise a Long value to its string value" in {
    forAll(minSuccessful(300)) { (value: Long) =>
      val query = implicitly[SQLPrimitive[Long]].toSQL(value)
      query shouldEqual s"${value.toString}"
    }
  }

  "The SQL Int primitive" should "serialise a Int value to its string value" in {
    forAll(minSuccessful(300)) { (value: Int) =>
      val query = implicitly[SQLPrimitive[Int]].toSQL(value)
      query shouldEqual s"${value.toString}"
    }
  }


}
