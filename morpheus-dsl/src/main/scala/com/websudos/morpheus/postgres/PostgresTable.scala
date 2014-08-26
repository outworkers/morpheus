package com.websudos.morpheus.postgres

import com.twitter.finagle.exp.mysql.Row
import com.websudos.morpheus.dsl.{SelectTable, Table}
import com.websudos.morpheus.mysql.MySQLRootSelectQuery
import com.websudos.morpheus.mysql.MySQLSelectSyntaxBlock
import com.websudos.morpheus.mysql.MySQLQueryBuilder
import com.websudos.morpheus.mysql.MySQLSyntax
import com.websudos.morpheus.mysql.MySQLRootUpdateQuery
import com.websudos.morpheus.mysql.MySQLUpdateSyntaxBlock
import com.websudos.morpheus.mysql.MySQLRootDeleteQuery
import com.websudos.morpheus.mysql.MySQLDeleteSyntaxBlock

abstract class PostgresTable[Owner <: PostgresTable[Owner, Record], Record] extends Table[Owner, Record] with SelectTable[Owner, Record,
  MySQLRootSelectQuery, MySQLSelectSyntaxBlock] {

  val queryBuilder = MySQLQueryBuilder

  val syntax = MySQLSyntax

  protected[this] def createRootSelect[A <: Table[A, _], B](table: A, block: MySQLSelectSyntaxBlock, rowFunc: Row => B): MySQLRootSelectQuery[A,
    B] = {
    new MySQLRootSelectQuery[A, B](table, block, rowFunc)
  }

  protected[this] def createSelectSyntaxBlock(query: String, tableName: String, cols: List[String] = List("*")): MySQLSelectSyntaxBlock = {
    new MySQLSelectSyntaxBlock(query, tableName, cols)
  }

  def update: MySQLRootUpdateQuery[Owner, Record] = new MySQLRootUpdateQuery(
    this.asInstanceOf[Owner],
    MySQLUpdateSyntaxBlock(syntax.update, tableName),
    fromRow
  )

  def delete: MySQLRootDeleteQuery[Owner, Record] = new MySQLRootDeleteQuery(
    this.asInstanceOf[Owner],
    MySQLDeleteSyntaxBlock(syntax.delete, tableName),
    fromRow
  )

}