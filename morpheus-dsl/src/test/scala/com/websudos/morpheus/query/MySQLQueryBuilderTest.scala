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

package com.websudos.morpheus.query

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}


class MySQLQueryBuilderTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {

  it should "correctly serialise a simple equals condition" in {
    forAll(minSuccessful(300)) { (name: String, value: String) =>
      whenever (name.length > 0 && value.length > 0) {
        val query = MySQLQueryBuilder.eqs(name, value).queryString
        query shouldEqual s"$name = $value"
      }
    }
  }

  it should "correctly serialise a simple lt condition" in {
    forAll(minSuccessful(300)) { (name: String, value: String) =>
      whenever (name.length > 0 && value.length > 0) {
        val query = MySQLQueryBuilder.lt(name, value).queryString
        query shouldEqual s"$name < $value"
      }
    }
  }

  it should "correctly serialise a simple lte condition" in {
    forAll(minSuccessful(300)) { (name: String, value: String) =>
      whenever (name.length > 0 && value.length > 0) {
        val query = MySQLQueryBuilder.lte(name, value).queryString
        query shouldEqual s"$name <= $value"
      }
    }
  }

  it should "correctly serialise a simple gt condition" in {
    forAll(minSuccessful(300)) { (name: String, value: String) =>
      whenever (name.length > 0 && value.length > 0) {
        val query = MySQLQueryBuilder.gt(name, value).queryString
        query shouldEqual s"$name > $value"
      }
    }
  }

  it should "correctly serialise a simple gte condition" in {
    forAll(minSuccessful(300)) { (name: String, value: String) =>
      whenever (name.length > 0 && value.length > 0) {
        val query = MySQLQueryBuilder.gte(name, value).queryString
        query shouldEqual s"$name >= $value"
      }
    }
  }

  it should "correctly serialise a simple != condition" in {
    forAll(minSuccessful(300)) { (name: String, value: String) =>
      whenever (name.length > 0 && value.length > 0) {
        val query = MySQLQueryBuilder.!=(name, value).queryString
        query shouldEqual s"$name != $value"
      }
    }
  }

  it should "correctly serialise a simple <> condition" in {
    forAll(minSuccessful(300)) { (name: String, value: String) =>
      whenever (name.length > 0 && value.length > 0) {
        val query = MySQLQueryBuilder.<>(name, value).queryString
        query shouldEqual s"$name <> $value"
      }
    }
  }
}
