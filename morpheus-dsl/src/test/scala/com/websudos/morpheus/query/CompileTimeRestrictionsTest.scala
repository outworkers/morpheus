package com.websudos.morpheus.query

import org.scalatest.{FlatSpec, Matchers}

import com.websudos.morpheus.column.ForeignKey
import com.websudos.morpheus.dsl.{BasicRecord, BasicTable}
import com.websudos.morpheus.mysql.Imports._
import com.websudos.morpheus.tables.IndexTable

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

  it should "not compile a SELECT query with two limit clauses" in {
    BasicTable.select
      .limit(10)

    """BasicTable.select
      .limit(10)
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

  it should "not compile a SELECT query with two orderBy clauses" in {
    """BasicTable.select
      .orderBy(_.count asc, _.name desc)
      .orderBy(_.count asc, _.name desc)
    """ shouldNot compile
  }

  it should "compile a SELECT query with a single groupBy clause" in {
    """BasicTable.select
      .groupBy(_.count, _.name)
    """ should compile
  }

  it should "not compile a SELECT query with two groupBy clauses" in {
    """BasicTable.select
      .groupBy(_.count, _.name)
      .groupBy(_.count, _.name)
    """ shouldNot compile
  }

  it should "compile a select query groupped before ordering" in {
    """BasicTable.select
      .groupBy(_.count, _.name)
      .orderBy(_.name asc)
      .queryString""" should compile
  }

  it should "not compile a select query ordered before grouping" in {
    """BasicTable.select
      .orderBy(_.name asc)
      .groupBy(_.count, _.name)
      .queryString""" shouldNot compile
  }

  it should "allow defining a foreignKey from one table to another" in {
    """object foreign extends ForeignKey[BasicTable, BasicRecord, IndexTable](BasicTable, IndexTable.id, IndexTable.value)""" should compile
  }

  it should "not allow defining a foreignKey from a table to columns in the same table" in {
    // this line is because InteliJ will remove the imports automatically as it's not detecting objects inside string literals.
    // We are also too lazy to remove the automatic import management.
    object foreign extends ForeignKey[BasicTable, BasicRecord, IndexTable](BasicTable, IndexTable.id, IndexTable.value)

    """object foreign2 extends ForeignKey[BasicTable, BasicRecord, BasicTable](BasicTable, BasicTable.name, BasicTable.count)""" shouldNot compile
  }

  it should "not allow defining a foreignKey from a table to columns belonging to multiple tables" in {
    """ object foreign extends ForeignKey[BasicTable, BasicRecord, IndexTable](BasicTable, IndexTable.id, KeysTable.id)""" shouldNot compile
  }
}
