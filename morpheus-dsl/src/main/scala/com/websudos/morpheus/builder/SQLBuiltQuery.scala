package com.websudos.morpheus.builder

case class SQLBuiltQuery(queryString: String) {
  def append(st: String): SQLBuiltQuery = SQLBuiltQuery(queryString + st)
  def append(st: SQLBuiltQuery): SQLBuiltQuery = append(st.queryString)
  def append[T](list: T)(implicit ev1: T => TraversableOnce[String]): SQLBuiltQuery = SQLBuiltQuery(queryString + list.mkString(", "))

  def appendEscape(st: String): SQLBuiltQuery = append(escape(st))
  def appendEscape(st: SQLBuiltQuery): SQLBuiltQuery = appendEscape(st.queryString)

  def prepend(st: String): SQLBuiltQuery = SQLBuiltQuery(st + queryString)
  def prepend(st: SQLBuiltQuery): SQLBuiltQuery = prepend(st.queryString)

  def escape(st: String): String = "`" + st + "`"

  def spaced: Boolean = queryString.endsWith(" ")
  def pad: SQLBuiltQuery = if (spaced) this else SQLBuiltQuery(queryString + " ")
  def forcePad: SQLBuiltQuery = SQLBuiltQuery(queryString + " ")
  def trim: SQLBuiltQuery = SQLBuiltQuery(queryString.trim)

  def wrap(str: String): SQLBuiltQuery = pad.append(DefaultSQLSyntax.`(`).append(str).append(DefaultSQLSyntax.`)`)
  def wrap(query: SQLBuiltQuery): SQLBuiltQuery = wrap(query.queryString)
  def wrap[T](list: T)(implicit ev1: T => TraversableOnce[String]): SQLBuiltQuery = wrap(list.mkString(", "))
  def wrapEscape(list: List[String]): SQLBuiltQuery = wrap(list.map(escape).mkString(", "))
}
