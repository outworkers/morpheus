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

package com.websudos.morpheus.schema

import org.scalatest.{Matchers, FlatSpec}

import com.websudos.morpheus.column.KnownTypeLimits
import com.websudos.morpheus.tables.StringsTable

class StringColumnSerialisationTest extends FlatSpec with Matchers {

  it should "serialise a simple Char definition to an SQL query without a limit set" in {
    StringsTable.charColumn.qb.queryString shouldEqual "charColumn CHAR"
  }

  it should "serialise a simple Char definition to an SQL query with a limit set" in {
    StringsTable.charLimited.qb.queryString shouldEqual s"charLimited CHAR(${KnownTypeLimits.charLimit})"
  }

  it should "serialise a simple VarChar definition to an SQL query without a limit set" in {
    StringsTable.varChar.qb.queryString shouldEqual s"varChar VARCHAR(${KnownTypeLimits.varcharLimit}})"
  }

  it should "serialise a simple VarChar definition to an SQL query with a limit set" in {
    StringsTable.varCharLimited.qb.queryString shouldEqual "varCharLimited VARCHAR(100)"
  }

}
