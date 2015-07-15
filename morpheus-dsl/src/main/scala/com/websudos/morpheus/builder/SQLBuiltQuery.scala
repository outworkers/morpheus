package com.websudos.morpheus.builder

import com.websudos.diesel.engine.query.AbstractQuery

case class SQLBuiltQuery(override val queryString: String) extends AbstractQuery[SQLBuiltQuery](queryString) {
  def create(st: String): SQLBuiltQuery = SQLBuiltQuery(st)
}

object SQLBuiltQuery {
  def empty: SQLBuiltQuery = SQLBuiltQuery("")
}
