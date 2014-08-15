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

  it should  "serialise a simple select all where query with a limit set" in {
    BasicTable.select
      .where(_.name eqs "test")
      .limit(10).queryString shouldEqual "SELECT * FROM BasicTable WHERE name = 'test' LIMIT 10"
  }

  it should "serialise a select query with an < operator" in {
    BasicTable.select.where(_.count < 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count < 5"
  }

  it should "serialise a select query with an lt operator" in {
    BasicTable.select.where(_.count lt 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count < 5"
  }

  it should "serialise a select query with an <= operator" in {
    BasicTable.select.where(_.count <= 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count <= 5"
  }

  it should "serialise a select query with an lte operator" in {
    BasicTable.select.where(_.count lte 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count <= 5"
  }

  it should "serialise a select query with a gt operator" in {
    BasicTable.select.where(_.count gt 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count > 5"
  }

  it should "serialise a select query with a > operator" in {
    BasicTable.select.where(_.count > 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count > 5"
  }

  it should "serialise a select query with a gte operator" in {
    BasicTable.select.where(_.count gte 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count >= 5"
  }

  it should "serialise a select query with a >= operator" in {
    BasicTable.select.where(_.count >= 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count >= 5"
  }

  it should  "serialise a simple select all where-and query" in {
    BasicTable.select.where(_.name eqs "test")
      .and(_.count eqs 5)
      .queryString shouldEqual "SELECT * FROM BasicTable WHERE name = 'test' AND count = 5"
  }

  it should "serialise a 1 column partial select query" in {
    BasicTable.select(_.name)
      .queryString shouldEqual "SELECT name FROM BasicTable"
  }

  it should "serialise a 1 column partial select query with an where clause" in {
    BasicTable.select(_.name)
      .where(_.count eqs 5)
      .queryString shouldEqual "SELECT name FROM BasicTable WHERE count = 5"
  }

  it should "serialise a 1 column partial select query with an or-where clause" in {
    BasicTable.select(_.name)
      .where(t => { (t.count eqs 5) or (t.count eqs 10) })
      .queryString shouldEqual "SELECT name FROM BasicTable WHERE (count = 5 OR " +
      "count = 10)"
  }

  it should "serialise a 1 column partial select query with an or-where clause and a limit" in {
    BasicTable.select(_.name)
      .where(t => { (t.count eqs 5) or (t.count eqs 10) })
      .limit(20)
      .queryString shouldEqual "SELECT name FROM BasicTable WHERE (count = 5 OR " +
      "count = 10) LIMIT 20"
  }

  it should "serialise a 1 column partial select query with a multiple or-where clause" in {
    BasicTable.select(_.name)
      .where(t => { (t.count eqs 5) or (t.count eqs 10) or (t.count >= 15)})
      .queryString shouldEqual "SELECT name FROM BasicTable " +
      "WHERE (count = 5 OR count = 10 OR count >= 15)"
  }

  it should "serialise a 1 column partial select query with a multiple or-where clause and a limit" in {
    BasicTable.select(_.name)
      .where(t => { (t.count eqs 5) or (t.count eqs 10) or (t.count >= 15)})
      .limit(15)
      .queryString shouldEqual "SELECT name FROM BasicTable " +
      "WHERE (count = 5 OR count = 10 OR count >= 15) LIMIT 15"
  }

  it should "serialise a 2 column partial select query" in {
    BasicTable.select(_.name, _.count)
      .queryString shouldEqual "SELECT name count FROM BasicTable"
  }

  it should "serialise a 2 column partial select query with an WHERE clause" in {
    BasicTable.select(_.name, _.count)
      .queryString shouldEqual "SELECT name count FROM BasicTable"
  }

  it should "serialise a conditional clause with an OR operator" in {
    BasicTable.select.where(_.name eqs "test")
      .and(t => { (t.count eqs 5) or (t.name eqs "test") })
      .limit(25)
      .queryString shouldEqual "SELECT * FROM BasicTable WHERE name = " +
      "'test' AND (count = 5 OR name = 'test') LIMIT 25"
  }

  it should  "not compile a select query if the value compared against doesn't match the value type of the underlying column" in {
    """BasicTable.select.where(_.name eqs 5).queryString""" shouldNot compile
  }

  it should "serialise a simple SELECT ALL query using the all method" in {
    BasicTable.select.all.queryString shouldEqual "SELECT * FROM BasicTable"
  }

  it should "serialise a simple SELECT ALL LIMIT query using the all method" in {
    BasicTable.select.all.limit(50).queryString shouldEqual "SELECT * FROM BasicTable LIMIT 50"
  }


  it should  "serialise a simple select all where query using the all method" in {
    BasicTable.select.all.where(_.name eqs "test").queryString shouldEqual "SELECT * FROM BasicTable WHERE name = 'test'"
  }

  it should "serialise a select query with an < operato using the all methodr" in {
    BasicTable.select.all.where(_.count < 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count < 5"
  }

  it should "serialise a select query with an lt operator using the all method" in {
    BasicTable.select.all.where(_.count lt 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count < 5"
  }

  it should "serialise a select query with an <= operator using the all method" in {
    BasicTable.select.all.where(_.count <= 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count <= 5"
  }

  it should "serialise a select query with an lte operator using the all method" in {
    BasicTable.select.all.where(_.count lte 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count <= 5"
  }

  it should "serialise a select query with a gt operator using the all method" in {
    BasicTable.select.all.where(_.count gt 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count > 5"
  }

  it should "serialise a select query with a > operator using the all method" in {
    BasicTable.select.all.where(_.count > 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count > 5"
  }

  it should "serialise a select query with a gte operator using the all method" in {
    BasicTable.select.all.where(_.count gte 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count >= 5"
  }

  it should "serialise a select query with a >= operator using the all method" in {
    BasicTable.select.all.where(_.count >= 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count >= 5"
  }

  it should  "serialise a simple select all where-and query using the all method" in {
    BasicTable.select.all.where(_.name eqs "test").and(_.count eqs 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE name = 'test' AND count = 5"
  }

  it should "serialise a 1 column partial select query using the all method" in {
    BasicTable.select(_.name).all.queryString shouldEqual "SELECT name FROM BasicTable"
  }

  it should "serialise a 1 column partial select query with an where clause using the all method" in {
    BasicTable.select(_.name).all.where(_.count eqs 5).queryString shouldEqual "SELECT name FROM BasicTable WHERE count = 5"
  }

  it should "serialise a 1 column partial select query with an or-where clause using the all method and a LIMIT" in {
    BasicTable.select(_.name)
      .all
      .where(t => { (t.count eqs 5) or (t.count eqs 10) })
      .limit(100)
      .queryString shouldEqual "SELECT name FROM BasicTable WHERE (count = 5 OR count = 10) LIMIT 100"
  }

  it should "serialise a 1 column partial select query with a multiple or-where clause using the all method" in {
    BasicTable.select(_.name).all.where(t => { (t.count eqs 5) or (t.count eqs 10) or (t.count >= 15)}).queryString shouldEqual "SELECT name FROM BasicTable " +
      "WHERE (count = 5 OR count = 10 OR count >= 15)"
  }

  it should "serialise a 2 column partial select query using the all method" in {
    BasicTable.select(_.name, _.count).all.queryString shouldEqual "SELECT name count FROM BasicTable"
  }

  it should "serialise a 2 column partial select query with an WHERE clause using the all method" in {
    BasicTable.select(_.name, _.count).all.queryString shouldEqual "SELECT name count FROM BasicTable"
  }

  it should "serialise a conditional clause with an OR operator using the all method" in {
    BasicTable.select.all
      .where(_.name eqs "test")
      .and(t => { (t.count eqs 5) or (t.name eqs "test") })
      .queryString shouldEqual "SELECT * FROM BasicTable " +
      "WHERE name = 'test' AND (count = 5 OR name = 'test')"
  }

  it should  "not compile a select query if the value compared against doesn't match the value type of the underlying column using the all method" in {
    """BasicTable.select.all.where(_.name eqs 5).queryString""" shouldNot compile
  }

  it should "not allow chaining multiple where clauses" in {
    """BasicTable.select.all.where(_.count eqs 5).where(_.count eqs 10).queryString""" shouldNot compile
  }

  it should "serialise a simple SELECT DISTINCT query" in {
    BasicTable.select.distinct.queryString shouldEqual "SELECT DISTINCT * FROM BasicTable"
  }

  it should "serialise a simple SELECT DISTINCT query with an WHERE clause" in {
    BasicTable.select.distinct.where(_.name eqs "test").queryString shouldEqual "SELECT DISTINCT * FROM BasicTable WHERE name = 'test'"
  }

  it should "serialise a partial SELECT DISTINCT query with a single column in the partial select" in {
    BasicTable.select(_.name).distinct.queryString shouldEqual "SELECT DISTINCT name FROM BasicTable"
  }

  it should "serialise a partial SELECT DISTINCT query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name).distinct.where(_.count >= 5).queryString shouldEqual "SELECT DISTINCT name FROM BasicTable WHERE count >= 5"
  }

  it should "serialise a partial SELECT DISTINCT query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count).distinct.queryString shouldEqual "SELECT DISTINCT name, count FROM BasicTable"
  }

  it should "serialise a partial SELECT DISTINCT query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count).distinct.where(_.count <= 10).queryString shouldEqual "SELECT DISTINCT name, count FROM BasicTable WHERE count <= 10"
  }

  it should "serialise a partial SELECT DISTINCT query with multiple columns in a partial select and an where-and clause" in {
    BasicTable
      .select(_.name, _.count)
      .distinct
      .where(_.count >= 10)
      .and(_.count <= 100).queryString shouldEqual "SELECT DISTINCT name, " +
      "count FROM BasicTable WHERE count >= 10 AND count <= 100"
  }

  it should "serialise a partial SELECT DISTINCT query with multiple columns in a partial select and an where-and-or clause" in {
    BasicTable
      .select(_.name, _.count)
      .distinct
      .where(_.count >= 10)
      .and(t => { (t.count <= 100) or (t.name eqs "test")})
      .queryString shouldEqual "SELECT DISTINCT name, " +
      "count FROM BasicTable WHERE count >= 10 AND (count <= 100 OR name = 'test')"
  }

  it should "serialise a simple SELECT DISTINCTROW query" in {
    BasicTable.select.distinctRow.queryString shouldEqual "SELECT DISTINCTROW * FROM BasicTable"
  }

  it should "serialise a simple SELECT DISTINCTROW query with an WHERE clause" in {
    BasicTable.select.distinctRow.where(_.name eqs "test").queryString shouldEqual "SELECT DISTINCTROW * FROM BasicTable WHERE name = 'test'"
  }

  it should "serialise a partial SELECT DISTINCTROW query with a single column in the partial select" in {
    BasicTable.select(_.name).distinctRow.queryString shouldEqual "SELECT DISTINCTROW name FROM BasicTable"
  }

  it should "serialise a partial SELECT DISTINCTROW query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name).distinctRow.where(_.count >= 5).queryString shouldEqual "SELECT DISTINCTROW name FROM BasicTable WHERE count >= 5"
  }

  it should "serialise a partial SELECT DISTINCTROW query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count).distinctRow.queryString shouldEqual "SELECT DISTINCTROW name, count FROM BasicTable"
  }

  it should "serialise a partial SELECT DISTINCTROW query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count).distinctRow.where(_.count <= 10).queryString shouldEqual "SELECT DISTINCTROW name, count FROM BasicTable WHERE count <= 10"
  }

  it should "serialise a partial SELECT DISTINCTROW query with multiple columns in a partial select and an where-and clause" in {
    BasicTable
      .select(_.name, _.count)
      .distinctRow
      .where(_.count >= 10)
      .and(_.count <= 100).queryString shouldEqual "SELECT DISTINCTROW name, count FROM BasicTable WHERE count >= 10 AND count <= 100"
  }

  it should "serialise a partial SELECT DISTINCTROW query with multiple columns in a partial select and an where-and-or clause" in {
    BasicTable
      .select(_.name, _.count)
      .distinctRow
      .where(_.count >= 10)
      .and(t => { (t.count <= 100) or (t.name eqs "test")}).queryString shouldEqual "SELECT DISTINCTROW name, " +
      "count FROM BasicTable WHERE count >= 10 AND (count <= 100 OR name = 'test')"
  }

  it should "serialise a simple SELECT HIGH_PRIORITY query" in {
    BasicTable.select.highPriority.queryString shouldEqual "SELECT HIGH_PRIORITY * FROM BasicTable"
  }

  it should "serialise a simple SELECT HIGH_PRIORITY query with an WHERE clause" in {
    BasicTable.select.highPriority.where(_.name eqs "test").queryString shouldEqual "SELECT HIGH_PRIORITY * FROM BasicTable WHERE name = 'test'"
  }

  it should "serialise a partial SELECT HIGH_PRIORITY query with a single column in the partial select" in {
    BasicTable.select(_.name).highPriority.queryString shouldEqual "SELECT HIGH_PRIORITY name FROM BasicTable"
  }

  it should "serialise a partial SELECT HIGH_PRIORITY query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name).highPriority.where(_.count >= 5).queryString shouldEqual "SELECT HIGH_PRIORITY name FROM BasicTable WHERE count >= 5"
  }

  it should "serialise a partial SELECT HIGH_PRIORITY query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count).highPriority.queryString shouldEqual "SELECT HIGH_PRIORITY name, count FROM BasicTable"
  }

  it should "serialise a partial SELECT HIGH_PRIORITY query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count).highPriority.where(_.count <= 10).queryString shouldEqual "SELECT HIGH_PRIORITY name, count FROM BasicTable WHERE count <= 10"
  }

  it should "serialise a partial SELECT HIGH_PRIORITY query with multiple columns in a partial select and an where-and clause" in {
    BasicTable
      .select(_.name, _.count)
      .highPriority
      .where(_.count >= 10)
      .and(_.count <= 100).queryString shouldEqual "SELECT HIGH_PRIORITY name, count FROM BasicTable WHERE count >= 10 AND count <= 100"
  }

  it should "serialise a partial SELECT HIGH_PRIORITY query with multiple columns in a partial select and an where-and-or clause" in {
    BasicTable
      .select(_.name, _.count)
      .highPriority
      .where(_.count >= 10)
      .and(t => { (t.count <= 100) or (t.name eqs "test")}).queryString shouldEqual "SELECT HIGH_PRIORITY name, " +
      "count FROM BasicTable WHERE count >= 10 AND (count <= 100 OR name = 'test')"
  }

  it should "serialise a simple SELECT STRAIGHT_JOIN query" in {
    BasicTable.select.straightJoin.queryString shouldEqual "SELECT STRAIGHT_JOIN * FROM BasicTable"
  }

  it should "serialise a simple SELECT STRAIGHT_JOIN query with an WHERE clause" in {
    BasicTable.select.straightJoin.where(_.name eqs "test").queryString shouldEqual "SELECT STRAIGHT_JOIN * FROM BasicTable WHERE name = 'test'"
  }

  it should "serialise a partial SELECT STRAIGHT_JOIN query with a single column in the partial select" in {
    BasicTable.select(_.name).straightJoin.queryString shouldEqual "SELECT STRAIGHT_JOIN name FROM BasicTable"
  }

  it should "serialise a partial SELECT STRAIGHT_JOIN query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name).straightJoin.where(_.count >= 5).queryString shouldEqual "SELECT STRAIGHT_JOIN name FROM BasicTable WHERE count >= 5"
  }

  it should "serialise a partial SELECT STRAIGHT_JOIN query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count).straightJoin.queryString shouldEqual "SELECT STRAIGHT_JOIN name, count FROM BasicTable"
  }

  it should "serialise a partial SELECT STRAIGHT_JOIN query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count).straightJoin.where(_.count <= 10).queryString shouldEqual "SELECT STRAIGHT_JOIN name, count FROM BasicTable WHERE count <= 10"
  }

  it should "serialise a partial SELECT STRAIGHT_JOIN query with multiple columns in a partial select and an where-and clause" in {
    BasicTable
      .select(_.name, _.count)
      .straightJoin
      .where(_.count >= 10)
      .and(_.count <= 100).queryString shouldEqual "SELECT STRAIGHT_JOIN name, count FROM BasicTable WHERE count >= 10 AND count <= 100"
  }

  it should "serialise a partial SELECT STRAIGHT_JOIN query with multiple columns in a partial select and an where-and-or clause" in {
    BasicTable
      .select(_.name, _.count)
      .straightJoin
      .where(_.count >= 10)
      .and(t => { (t.count <= 100) or (t.name eqs "test")}).queryString shouldEqual "SELECT STRAIGHT_JOIN name, " +
      "count FROM BasicTable WHERE count >= 10 AND (count <= 100 OR name = 'test')"
  }

  it should "serialise a simple SELECT SQL_SMALL_RESULT query" in {
    BasicTable.select.sqlSmallResult
      .queryString shouldEqual "SELECT SQL_SMALL_RESULT * FROM BasicTable"
  }

  it should "serialise a simple SELECT SQL_SMALL_RESULT query with an WHERE clause" in {
    BasicTable.select.sqlSmallResult.where(_.name eqs "test")
      .queryString shouldEqual "SELECT SQL_SMALL_RESULT * FROM BasicTable WHERE name = 'test'"
  }

  it should "serialise a partial SELECT SQL_SMALL_RESULT query with a single column in the partial select" in {
    BasicTable.select(_.name)
      .sqlSmallResult.queryString shouldEqual "SELECT SQL_SMALL_RESULT name FROM BasicTable"
  }

  it should "serialise a partial SELECT SQL_SMALL_RESULT query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name)
      .sqlSmallResult.where(_.count >= 5).queryString shouldEqual "SELECT SQL_SMALL_RESULT name FROM BasicTable WHERE count >= 5"
  }

  it should "serialise a partial SELECT SQL_SMALL_RESULT query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count)
      .sqlSmallResult.queryString shouldEqual "SELECT SQL_SMALL_RESULT name, count FROM BasicTable"
  }

  it should "serialise a partial SELECT SQL_SMALL_RESULT query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count)
      .sqlSmallResult.where(_.count <= 10).queryString shouldEqual "SELECT SQL_SMALL_RESULT name, count FROM BasicTable WHERE count <= 10"
  }

  it should "serialise a partial SELECT SQL_SMALL_RESULT query with multiple columns in a partial select and an where-and clause" in {
    BasicTable
      .select(_.name, _.count)
      .sqlSmallResult
      .where(_.count >= 10)
      .and(_.count <= 100).queryString shouldEqual "SELECT SQL_SMALL_RESULT name, count FROM BasicTable WHERE count >= 10 AND count <= 100"
  }

  it should "serialise a partial SELECT SQL_SMALL_RESULT query with multiple columns in a partial select and an where-and-or clause" in {
    BasicTable
      .select(_.name, _.count)
      .sqlSmallResult
      .where(_.count >= 10)
      .and(t => { (t.count <= 100) or (t.name eqs "test")}).queryString shouldEqual "SELECT SQL_SMALL_RESULT name, " +
      "count FROM BasicTable WHERE count >= 10 AND (count <= 100 OR name = 'test')"
  }
  //

  it should "serialise a simple SELECT SQL_BIG_RESULT query" in {
    BasicTable.select.sqlBigResult.queryString shouldEqual "SELECT SQL_BIG_RESULT * FROM BasicTable"
  }

  it should "serialise a simple SELECT SQL_BIG_RESULT query with an WHERE clause" in {
    BasicTable.select.sqlBigResult.where(_.name eqs "test").queryString shouldEqual "SELECT SQL_BIG_RESULT * FROM BasicTable WHERE name = 'test'"
  }

  it should "serialise a partial SELECT SQL_BIG_RESULT query with a single column in the partial select" in {
    BasicTable.select(_.name).sqlBigResult.queryString shouldEqual "SELECT SQL_BIG_RESULT name FROM BasicTable"
  }

  it should "serialise a partial SELECT SQL_BIG_RESULT query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name).sqlBigResult.where(_.count >= 5).queryString shouldEqual "SELECT SQL_BIG_RESULT name FROM BasicTable WHERE count >= 5"
  }

  it should "serialise a partial SELECT SQL_BIG_RESULT query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count).sqlBigResult.queryString shouldEqual "SELECT SQL_BIG_RESULT name, count FROM BasicTable"
  }

  it should "serialise a partial SELECT SQL_BIG_RESULT query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count).sqlBigResult.where(_.count <= 10).queryString shouldEqual "SELECT SQL_BIG_RESULT name, count FROM BasicTable WHERE count <= 10"
  }

  it should "serialise a partial SELECT SQL_BIG_RESULT query with multiple columns in a partial select and an where-and clause" in {
    BasicTable
      .select(_.name, _.count)
      .sqlBigResult
      .where(_.count >= 10)
      .and(_.count <= 100).queryString shouldEqual "SELECT SQL_BIG_RESULT name, count FROM BasicTable WHERE count >= 10 AND count <= 100"
  }

  it should "serialise a partial SELECT SQL_BIG_RESULT query with multiple columns in a partial select and an where-and-or clause" in {
    BasicTable
      .select(_.name, _.count)
      .sqlBigResult
      .where(_.count >= 10)
      .and(t => { (t.count <= 100) or (t.name eqs "test")}).queryString shouldEqual "SELECT SQL_BIG_RESULT name, " +
      "count FROM BasicTable WHERE count >= 10 AND (count <= 100 OR name = 'test')"
  }

  it should "serialise a simple SELECT SQL_BUFFER_RESULT query" in {
    BasicTable.select.sqlBufferResult.queryString shouldEqual "SELECT SQL_BUFFER_RESULT * FROM BasicTable"
  }

  it should "serialise a simple SELECT SQL_BUFFER_RESULT query with an WHERE clause" in {
    BasicTable.select.sqlBufferResult.where(_.name eqs "test").queryString shouldEqual "SELECT SQL_BUFFER_RESULT * FROM BasicTable WHERE name = 'test'"
  }

  it should "serialise a partial SELECT SQL_BUFFER_RESULT query with a single column in the partial select" in {
    BasicTable.select(_.name).sqlBufferResult.queryString shouldEqual "SELECT SQL_BUFFER_RESULT name FROM BasicTable"
  }

  it should "serialise a partial SELECT SQL_BUFFER_RESULT query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name).sqlBufferResult.where(_.count >= 5).queryString shouldEqual "SELECT SQL_BUFFER_RESULT name FROM BasicTable WHERE count >= 5"
  }

  it should "serialise a partial SELECT SQL_BUFFER_RESULT query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count).sqlBufferResult.queryString shouldEqual "SELECT SQL_BUFFER_RESULT name, count FROM BasicTable"
  }

  it should "serialise a partial SELECT SQL_BUFFER_RESULT query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count).sqlBufferResult.where(_.count <= 10).queryString shouldEqual "SELECT SQL_BUFFER_RESULT name, count FROM BasicTable WHERE count <= 10"
  }

  it should "serialise a partial SELECT SQL_BUFFER_RESULT query with multiple columns in a partial select and an where-and clause" in {
    BasicTable
      .select(_.name, _.count)
      .sqlBufferResult
      .where(_.count >= 10)
      .and(_.count <= 100).queryString shouldEqual "SELECT SQL_BUFFER_RESULT name, count FROM BasicTable WHERE count >= 10 AND count <= 100"
  }

  it should "serialise a partial SELECT SQL_BUFFER_RESULT query with multiple columns in a partial select and an where-and-or clause" in {
    BasicTable
      .select(_.name, _.count)
      .sqlBufferResult
      .where(_.count >= 10)
      .and(t => { (t.count <= 100) or (t.name eqs "test")}).queryString shouldEqual "SELECT SQL_BUFFER_RESULT name, " +
      "count FROM BasicTable WHERE count >= 10 AND (count <= 100 OR name = 'test')"
  }

  it should "serialise a simple in operator query for string columns" in {
    BasicTable.select
      .where(_.name in List("name1", "name2", "name3"))
      .queryString shouldEqual "SELECT * FROM BasicTable WHERE name IN ('name1', 'name2', 'name3')"
  }

  it should "serialise a simple in operator query for string columns followed by an AND-IN clause" in {
    BasicTable.select
      .where(_.name in List("name1", "name2", "name3"))
      .and(_.count in List(5, 10, 15))
      .queryString shouldEqual "SELECT * FROM BasicTable WHERE name IN ('name1', 'name2', 'name3') AND count IN (5, 10, 15)"
  }

  it should "serialise a in-or operator query for string columns followed by an AND-IN clause" in {
    BasicTable.select
      .where(t => { (t.name in List("name1", "name2", "name3")) or (t.name in List("name4", "name5")) })
      .and(_.count in List(5, 10, 15))
      .queryString shouldEqual "SELECT * FROM BasicTable WHERE (name IN ('name1', 'name2', 'name3') OR name IN ('name4', 'name5')) AND count IN (5, 10, 15)"
  }

  it should "serialise a SELECT with a single ORDER BY clause" in {
    BasicTable.select
      .orderBy(_.count asc)
      .queryString shouldEqual "SELECT * FROM BasicTable ORDER BY count ASC"
  }

  it should "serialise a SELECT with multiple ORDER BY clauses" in {
    BasicTable.select
      .orderBy(_.count asc, _.name desc)
      .queryString shouldEqual "SELECT * FROM BasicTable ORDER BY count ASC, name DESC"
  }

  it should "serialise a SELECT with multiple ORDER BY clauses and a limit" in {
    BasicTable.select
      .orderBy(_.count asc, _.name desc)
      .limit(10)
      .queryString shouldEqual "SELECT * FROM BasicTable ORDER BY count ASC, name DESC LIMIT 10"
  }

  it should "serialise a SELECT with a single GROUP BY clause" in {
    BasicTable.select
      .groupBy(_.count)
      .queryString shouldEqual "SELECT * FROM BasicTable GROUP BY count"
  }

  it should "serialise a SELECT with multiple GROUP BY clauses" in {
    BasicTable.select
      .groupBy(_.count, _.name)
      .queryString shouldEqual "SELECT * FROM BasicTable GROUP BY count, name"
  }

  it should "serialise a SELECT with multiple GROUP BY clauses and an orderBy clause" in {
    BasicTable.select
      .groupBy(_.count, _.name)
      .orderBy(_.name asc)
      .queryString shouldEqual "SELECT * FROM BasicTable GROUP BY count, name ORDER BY name ASC"
  }
}
