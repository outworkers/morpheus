package com.websudos.morpheus.query

import org.scalatest.{ FlatSpec, Matchers }
import com.websudos.morpheus.dsl.BasicTable
import com.websudos.morpheus.mysql.Imports._

class CompileTimeRestrictionsTest extends FlatSpec with Matchers {

  it should " compile a SELECT query with a limit set before an orderBy clause" in {
    """BasicTable.select
      .limit(10)
      .orderBy(_.count asc, _.name desc)
    """ should compile
  }

  it should " compile a SELECT query with a limit set after an orderBy clause" in {
    """BasicTable.select
      .orderBy(_.count asc, _.name desc)
      .limit(10)
    """ should compile
  }

  it should "not compile a SELECT query with a limit set before and after an orderBy clause" in {
    """BasicTable.select
      .limit(10)
      .orderBy(_.count asc, _.name desc)
      .limit(10)
    """ shouldNot compile
  }

  it should " compile a SELECT query with a an orderBy clause set after a limit clause" in {
    """BasicTable.select
      .limit(10)
      .orderBy(_.count asc, _.name desc)
    """ should compile
  }

  it should " compile a SELECT query with a an orderBy clause set before a limit clause" in {
    """BasicTable.select
      .orderBy(_.count asc, _.name desc)
      .limit(10)
    """ should compile
  }

  it should "not compile a SELECT query with a an orderBy clause set before and after a limit clause" in {
    """BasicTable.select
      .orderBy(_.count asc, _.name desc)
      .limit(10)
    """ should compile
  }
}
