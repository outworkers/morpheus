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
package com.outworkers.morpheus.engine.query

import com.outworkers.morpheus.sql._
import com.outworkers.morpheus.dsl._
import org.scalatest.{FlatSpec, Matchers}

class InsertQuerySerialisationTest extends FlatSpec with Matchers {

  it should "serialise an INSERT INTO query to the correct query and convert using an implicit" in {
    BasicTable.insert.queryString shouldEqual "INSERT INTO `BasicTable`;"
  }

  it should "serialise an INSERT INTO query to the correct query" in {
    BasicTable.insert.into.queryString shouldEqual "INSERT INTO `BasicTable`;"
  }

  it should "serialise an INSERT query with a single value defined" in {
    BasicTable.insert
      .value(_.count, 5L)
      .queryString shouldEqual "INSERT INTO `BasicTable` (count) VALUES(5);"
  }

  it should "serialise an INSERT query with multiple values defined" in {
    BasicTable.insert
      .value(_.count, 5L)
      .value(_.name, "test")
      .queryString shouldEqual "INSERT INTO `BasicTable` (count, name) VALUES(5, 'test');"
  }
}
