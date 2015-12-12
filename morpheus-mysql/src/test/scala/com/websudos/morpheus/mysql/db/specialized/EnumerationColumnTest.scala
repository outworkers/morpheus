package com.websudos.morpheus.mysql.db.specialized

import com.websudos.morpheus.mysql.Samplers

import scala.concurrent.ExecutionContext.Implicits.global
import com.websudos.morpheus.mysql._
import com.websudos.morpheus.mysql.db.MySQLSuite
import com.websudos.morpheus.mysql.tables.{EnumerationRecord, EnumerationTable}
import com.websudos.util.testing._
import org.scalatest.FlatSpec

class EnumerationColumnTest extends FlatSpec with MySQLSuite with Samplers {

  it should "store a record with an enumeration defined inside it" in {
    val record = gen[EnumerationRecord]

    val chain = for {
      store <- EnumerationTable.store(record)
      get <- EnumerationTable.select.where(_.id eqs record.id).one
    } yield get

    whenReady(chain) {
      res => {
        res.value shouldEqual record
      }
    }

  }
}
