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

package com.websudos.morpheus.dsl

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, FlatSpec}

import com.websudos.morpheus.query.SQLBuiltQuery

class SQLBuiltQueryTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {

  it should "serialise an append on an SQLBuiltQuery" in {
    forAll(minSuccessful(300)) { (part1: String, part2: String) =>
      whenever (part1.length > 0 && part2.length > 0) {
        val query = SQLBuiltQuery(part1).append(SQLBuiltQuery(part2)).queryString
        query shouldEqual s"$part1$part2"
      }
    }
  }

  it should "serialise a prepend on an SQLBuiltQuery" in {
    forAll(minSuccessful(300)) { (part1: String, part2: String) =>
      whenever (part1.length > 0 && part2.length > 0) {
        val query = SQLBuiltQuery(part1).prepend(SQLBuiltQuery(part2)).queryString
        query shouldEqual s"$part2$part1"
      }
    }
  }

  it should "serialise and pad an SQLBuiltQuery with a trailing space if the space is missing" in {
    forAll(minSuccessful(300)) { (part1: String) =>
      whenever (part1.length > 0) {
        val query = SQLBuiltQuery(part1).pad.queryString
        query shouldEqual s"$part1 "
      }
    }
  }

  it should "not add a trailing space if the last character of an SQLBuiltQuery is a space" in {
    forAll(minSuccessful(300)) { (part1: String) =>
      whenever (part1.length > 0) {
        val s = part1 + " "
        val query = SQLBuiltQuery(s).pad.queryString
        query shouldEqual s"$s"
      }
    }
  }

}
