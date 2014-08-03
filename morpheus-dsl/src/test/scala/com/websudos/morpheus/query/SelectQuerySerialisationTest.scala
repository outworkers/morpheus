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

import org.scalatest.{Matchers, FlatSpec}

import com.websudos.morpheus.dsl.BasicTable
import com.websudos.morpheus.mysql.Imports._

class SelectQuerySerialisationTest extends FlatSpec with Matchers {

  it should "serialise a simple SELECT ALL query" in {
    BasicTable.select.queryString shouldEqual "SELECT * FROM BasicTable"
  }

  it should  "serialise a simple select all where query" in {
    BasicTable.select.where(_.name eqs "test").queryString shouldEqual "SELECT * FROM BasicTable WHERE name = 'test'"
  }

  it should  "serialise a simple select all where-and query" in {
    BasicTable.select.where(_.name eqs "test").and(_.count eqs 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE name = 'test' AND count = 5"
  }

  it should "serialise a 1 column partial select query" in {
    BasicTable.select(_.name).queryString shouldEqual "SELECT name FROM BasicTable"
  }

  it should "serialise a 1 column partial select query with an where clause" in {
    BasicTable.select(_.name).where(_.count eqs 5).queryString shouldEqual "SELECT name FROM BasicTable WHERE count = 5"
  }

  it should "serialise a 1 column partial select query with an or-where clause" in {
    BasicTable.select(_.name).where(t => { (t.count eqs 5) or (t.count eqs 10) }).queryString shouldEqual "SELECT name FROM BasicTable WHERE (count = 5 OR " +
      "count = 10)"
  }

  it should "serialise a 1 column partial select query with a multiple or-where clause" in {
    BasicTable.select(_.name).where(t => { (t.count eqs 5) or (t.count eqs 10) or (t.count >= 15)}).queryString shouldEqual "SELECT name FROM BasicTable " +
      "WHERE (count = 5 OR count = 10 OR count >= 15)"
  }

  it should "serialise a 2 column partial select query" in {
    BasicTable.select(_.name, _.count).queryString shouldEqual "SELECT name count FROM BasicTable"
  }

  it should "serialise a 2 column partial select query with an WHERE clause" in {
    BasicTable.select(_.name, _.count).queryString shouldEqual "SELECT name count FROM BasicTable"
  }

  it should "serialise a conditional clause with an OR operator" in {
    BasicTable.select.where(_.name eqs "test").and(t => { (t.count eqs 5) or (t.name eqs "test") }).queryString shouldEqual "SELECT * FROM BasicTable WHERE name = " +
      "'test' AND (count = 5 OR name = 'test')"
  }

  it should  "not compile a select query if the value compared against doesn't match the value type of the underlying column" in {
    """BasicTable.select.where(_.name eqs 5).queryString""" shouldNot compile
  }
}
