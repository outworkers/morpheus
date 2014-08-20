package com.websudos.morpheus.column

import com.websudos.morpheus.dsl.Table
import com.websudos.morpheus.query.SQLBuiltQuery
import org.scalatest.{Matchers, FlatSpec}

class TestColumn extends AbstractColumn[Int] {
  override def qb: SQLBuiltQuery = ???

  override def toQueryString(v: Int): String = ???

  override def sqlType: String = ???

  override def table: Table[_, _] = ???
}

class AbstractColumnTest extends FlatSpec with Matchers {

  it should "resolve column name from the name of implementing class" in {
    val column = new TestColumn

    column.name shouldEqual "TestColumn"
  }

}
