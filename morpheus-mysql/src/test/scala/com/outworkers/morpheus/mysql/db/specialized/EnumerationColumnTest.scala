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
package com.outworkers.morpheus.mysql.db.specialized

import com.outworkers.morpheus.SQLPrimitive
import com.outworkers.morpheus.mysql._
import com.outworkers.morpheus.mysql.db.MySQLSuite
import com.outworkers.morpheus.mysql.tables.{EnumerationRecord, EnumerationTable, TestEnumeration}
import com.outworkers.util.samplers.{Generators, Sample}
import com.outworkers.util.testing._
import org.scalatest.FlatSpec

import scala.concurrent.Await
import scala.concurrent.duration._

class EnumerationColumnTest extends FlatSpec with MySQLSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    Await.result(EnumerationTable.create.ifNotExists.engine(InnoDB).future(), 5.seconds)
  }

  implicit object EnumerationRecordSampler extends Sample[EnumerationRecord] {
    override def sample: EnumerationRecord = EnumerationRecord(gen[Int], Generators.oneOf(TestEnumeration))
  }

  implicit val enumPrimitive: SQLPrimitive[TestEnumeration#Value] = SQLPrimitive(TestEnumeration)

  it should "store a record with an enumeration defined inside it" in {
    val record = gen[EnumerationRecord]

    val chain = for {
      store <- EnumerationTable.store(record).future()
      get <- EnumerationTable.select.where(_.id eqs record.id).one
    } yield get

    whenReady(chain) { res =>
      res.value shouldEqual record
    }
  }
}
