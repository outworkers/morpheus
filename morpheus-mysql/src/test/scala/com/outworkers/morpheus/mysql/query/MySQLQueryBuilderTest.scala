/*
 * Copyright 2013-2015 Websudos, Limited.
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
package com.outworkers.morpheus.mysql.query

import com.websudos.morpheus.builder.SQLBuiltQuery
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}
import com.outworkers.util.testing._

import com.websudos.morpheus.mysql.MySQLQueryBuilder


class MySQLQueryBuilderTest extends FlatSpec with Matchers {

  it should "serialise a simple equals condition" in {
    val name = gen[String]
    val value = gen[String]
    val query = MySQLQueryBuilder.eqs(name, value).queryString
    query shouldEqual s"$name = $value"
  }

  it should "serialise a simple lt condition" in {
    val name = gen[String]
    val value = gen[String]
    val query = MySQLQueryBuilder.lt(name, value).queryString
    query shouldEqual s"$name < $value"
  }

  it should "serialise a simple lte condition" in {
    val name = gen[String]
    val value = gen[String]
    val query = MySQLQueryBuilder.lte(name, value).queryString
    query shouldEqual s"$name <= $value"
  }

  it should "serialise a simple gt condition" in {
    val name = gen[String]
    val value = gen[String]
    val query = MySQLQueryBuilder.gt(name, value).queryString
    query shouldEqual s"$name > $value"
  }

  it should "serialise a simple gte condition" in {
    val name = gen[String]
    val value = gen[String]
    val query = MySQLQueryBuilder.gte(name, value).queryString
    query shouldEqual s"$name >= $value"
  }

  it should "serialise a simple != condition" in {
    val name = gen[String]
    val value = gen[String]
    val query = MySQLQueryBuilder.!=(name, value).queryString
    query shouldEqual s"$name != $value"
  }

  it should "serialise a simple <> condition" in {
    val name = gen[String]
    val value = gen[String]
    val query = MySQLQueryBuilder.<>(name, value).queryString
    query shouldEqual s"$name <> $value"
  }

  it should "serialise a simple <=> condition" in {
    val name = gen[String]
    val value = gen[String]
    val query = MySQLQueryBuilder.<=>(name, value).queryString
    query shouldEqual s"$name <=> $value"
  }

  it should "serialise a simple select * query" in {
    val name = gen[String]
    val query = MySQLQueryBuilder.select(name).queryString
    query shouldEqual s"SELECT * FROM `$name`"
  }

  it should "serialise a partial select query where 1 column name is specified" in {
    val name = gen[String]
    val column = gen[ShortString].value
    val query = MySQLQueryBuilder.select(name, column).queryString
    query shouldEqual s"SELECT $column FROM `$name`"
  }

  it should "serialise a partial select query where 2 column names are specified" in {
    val name = gen[String]
    val column1 = gen[ShortString].value
    val column2 = gen[ShortString].value
    val query = MySQLQueryBuilder.select(name, column1, column2).queryString
    query shouldEqual s"SELECT $column1 $column2 FROM `$name`"
  }

  it should "serialise a partial select query where 3 column names are specified" in {
    val name = gen[String]
    val column1 = gen[ShortString].value
    val column2 = gen[ShortString].value
    val column3 = gen[ShortString].value

    val query = MySQLQueryBuilder.select(name, column1, column2, column3).queryString
    query shouldEqual s"SELECT $column1 $column2 $column3 FROM `$name`"
  }

  it should "serialise an IN operator query" in {
    val name = gen[String]
    val column1 = gen[ShortString].value
    val column2 = gen[ShortString].value
    val column3 = gen[ShortString].value

    val query = MySQLQueryBuilder.in(name, List(column1, column2, column3)).queryString
    query shouldEqual s"$name IN ($column1, $column2, $column3)"
  }

  it should "serialise an NOT IN operator query" in {
    val name = gen[String]
    val column1 = gen[ShortString].value
    val column2 = gen[ShortString].value
    val column3 = gen[ShortString].value
    val query = MySQLQueryBuilder.notIn(name, List(column1, column2, column3)).queryString
    query shouldEqual s"$name NOT IN ($column1, $column2, $column3)"
  }

  it should "serialise an setTo operator query" in {
    val name = gen[String]
    val value = gen[String]
    val query = MySQLQueryBuilder.setTo(name, value).queryString
    query shouldEqual s"$name = $value"
  }


  it should "serialise an ASC operator query" in {
    val name = gen[String]
    val query = MySQLQueryBuilder.asc(name).queryString
    query shouldEqual s"$name ASC"
  }

  it should "serialise a DESC operator query" in {
    val name = gen[String]
    val value = gen[String]
    val query = MySQLQueryBuilder.desc(name).queryString
    query shouldEqual s"$name DESC"
  }

  it should "serialise a SET query" in {
    val name = gen[ShortString].value
    val part = gen[ShortString].value
    val query = MySQLQueryBuilder.set(SQLBuiltQuery(part), SQLBuiltQuery(name)).queryString
    query shouldEqual s"$part SET $name"
  }

  it should "serialise a LIKE operator query" in {
    val name = gen[ShortString].value
    val part = gen[ShortString].value
    val tested = part.trim
    val query = MySQLQueryBuilder.like(tested, name).queryString
    query shouldEqual s"$tested LIKE $name"
  }

  it should "serialise a NOT LIKE operator query" in {
    val name = gen[ShortString].value
    val part = gen[ShortString].value
    val query = MySQLQueryBuilder.notLike(part, name).queryString
    query shouldEqual s"$part NOT LIKE $name"
  }

  it should "correctly set multiple conditions" in {
    val name = gen[ShortString].value
    val part = gen[ShortString].value
    val query = MySQLQueryBuilder.andSet(SQLBuiltQuery(part), SQLBuiltQuery(name)).queryString
    query shouldEqual s"$part, $name"
  }

  it should "append an ENGINE clause" in {
    val name = gen[ShortString].value
    val part = gen[ShortString].value
    val query = MySQLQueryBuilder.engine(SQLBuiltQuery(part), name).queryString
    query shouldEqual s"$part ENGINE $name"
  }

  it should "serialise a BIN clause" in {
    val part = gen[ShortString].value
    val query = MySQLQueryBuilder.bin(part).queryString
    query shouldEqual s"BIN ($part)"
  }

  it should "serialise a CHAR_LENGTH clause" in {
    val part = gen[ShortString].value
    val query = MySQLQueryBuilder.charLength(part).queryString
    query shouldEqual s"CHAR_LENGTH ($part)"
  }

  it should "serialise a CHARACTER_LENGTH clause" in {
    val part = gen[ShortString].value
    val query = MySQLQueryBuilder.characterLength(part).queryString
    query shouldEqual s"CHARACTER_LENGTH ($part)"
  }

  it should "serialise a BIT_LENGTH clause" in {
    val part = gen[ShortString].value
    val query = MySQLQueryBuilder.bitLength(part).queryString
    query shouldEqual s"BIT_LENGTH ($part)"
  }

  it should "serialise a ASCII clause" in {
    val part = gen[ShortString].value
    val query = MySQLQueryBuilder.ascii(part).queryString
    query shouldEqual s"ASCII ($part)"
  }

  it should "serialise an EXISTS clause" in {
    val part = gen[ShortString].value
    val query = MySQLQueryBuilder.exists(SQLBuiltQuery(part)).queryString
    query shouldEqual s"EXISTS ($part)"
  }

  it should "serialise a NOT EXISTS clause" in {
    val part = gen[ShortString].value
    val query = MySQLQueryBuilder.notExists(SQLBuiltQuery(part)).queryString
    query shouldEqual s"NOT EXISTS ($part)"
  }

  it should "serialise a ON clause" in {
    val name = gen[ShortString].value
    val part = gen[ShortString].value
    val value = gen[ShortString].value
    val query = MySQLQueryBuilder.on(SQLBuiltQuery(part), SQLBuiltQuery(value)).queryString
    query shouldEqual s"$part ON $value"
  }

  it should "serialise a BETWEEN clause" in {
    val part = gen[ShortString].value
    val value = gen[ShortString].value
    val query = MySQLQueryBuilder.between(part, value).queryString
    query shouldEqual s"$part BETWEEN $value"
  }

  it should "serialise a NOT BETWEEN clause" in {
    val part = gen[ShortString].value
    val value = gen[ShortString].value
    val query = MySQLQueryBuilder.notBetween(part, value).queryString
    query shouldEqual s"$part NOT BETWEEN $value"
  }
}
