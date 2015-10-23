package com.websudos.morpheus.mysql.db.specialized

import com.websudos.morpheus.mysql.db.MySQLSuite
import com.websudos.morpheus.mysql.tables.{EnumerationTable, EnumerationRecord}
import org.scalatest.FlatSpec

class EnumerationColumnTest extends FlatSpec with MySQLSuite {


  it should "store a record with an enumeration defined inside it" in {
    val record = gen[EnumerationRecord]

    val chain = for {
      store <- EnumerationTable.sto
    }

  }
}
