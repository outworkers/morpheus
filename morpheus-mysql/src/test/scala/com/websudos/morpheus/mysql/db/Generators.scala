package com.websudos.morpheus.mysql.db

import com.websudos.morpheus.mysql.tables.{EnumerationRecord, TestEnumeration}
import com.websudos.util.testing._


trait Generators {
  implicit object EnumerationTableSampler extends Sample[EnumerationRecord] {
    override def sample: EnumerationRecord = {
      EnumerationRecord(
        gen[Int],
        oneOf(TestEnumeration)
      )
    }
  }
}
