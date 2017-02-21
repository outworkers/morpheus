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
package com.outworkers.morpheus.schema

import org.scalatest.{Matchers, FlatSpec}

import com.outworkers.morpheus.tables.ZeroFillTable

class ZeroFillColumnsSerialisationTest extends FlatSpec with Matchers {

  it should "serialise a ZEROFILL UNSIGNED NOT NULL column" in {
    ZeroFillTable.tinyInt.qb.queryString shouldEqual "tinyInt TINYINT ZEROFILL UNSIGNED NOT NULL"
  }

  it should "serialise a ZEROFILL NOT NULL column" in {
    ZeroFillTable.tinyIntLimited.qb.queryString shouldEqual "tinyIntLimited TINYINT(5) ZEROFILL NOT NULL"
  }

  it should "serialise a ZEROFILL UNSIGNED column" in {
    ZeroFillTable.smallInt.qb.queryString shouldEqual "smallInt SMALLINT ZEROFILL UNSIGNED"
  }

}
