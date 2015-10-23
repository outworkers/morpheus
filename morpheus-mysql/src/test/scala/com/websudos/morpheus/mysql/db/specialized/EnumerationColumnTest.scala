package com.websudos.morpheus.mysql.db.specialized

import com.websudos.morpheus.mysql.db.MySQLSuite
import com.websudos.morpheus.mysql.tables.{EnumerationTable, EnumerationRecord}
import org.scalatest.FlatSpec
import com.websudos.util.testing._
import com.websudos.morpheus.mysql._
import scala.concurrent.ExecutionContext.Implicits.global

class EnumerationColumnTest extends FlatSpec with MySQLSuite {


  it should "store a record with an enumeration defined inside it" in {
    val record = gen[EnumerationRecord]

    val chain = for {
      store <- EnumerationTable.store(record).future()
      get <- EnumerationTable.select.where(_.id eqs record.id).one
    } yield get

    whenReady(chain) {
      res => {
        res shouldBe defined
        res.value shouldEqual record
      }
    }

  }
}
