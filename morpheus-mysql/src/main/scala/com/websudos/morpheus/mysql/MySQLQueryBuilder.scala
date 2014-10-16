/*
 * Copyright 2014 websudos ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.websudos.morpheus.mysql

import java.util.Date
import org.joda.time.DateTime

import com.twitter.finagle.exp.mysql.{ Row => FinagleRow, Client => FinagleClient, Result => FinagleResult, ResultSet => FinagleResultSet, _ }
import com.twitter.util.Future
import com.websudos.morpheus._
import com.websudos.morpheus.column.AbstractColumn
import com.websudos.morpheus.query._


case class MySQLResult(result: FinagleResult) extends Result {}

case class MySQLRow(res: FinagleRow) extends Row {
  def get[A](name: String): A = res.apply(name).asInstanceOf[A]
}

class MySQLClient(val client: FinagleClient) extends Client[MySQLRow, MySQLResult] {

  def select[T](qb: String)(f: MySQLRow => T): Future[Seq[T]] = {
    client.query(qb).map {
      case set: FinagleResultSet => set.rows.map {
        row => f(new MySQLRow(row))
      }
      case _ => Seq.empty[T]
    }
  }

  def query(query: String): Future[MySQLResult] = client.query(query).map { res => MySQLResult(res)}
}




object MySQLSyntax extends AbstractSQLSyntax {
  val distinctRow = "DISTINCTROW"
  val lowPriority = "LOW_PRIORITY"
  val highPriority = "HIGH_PRIORITY"
  val delayed = "DELAYED"
  val straightJoin = "STRAIGHT_JOIN"
  val sqlSmallResult = "SQL_SMALL_RESULT"
  val sqlBigResult = "SQL_BIG_RESULT"
  val sqlBufferResult = "SQL_BUFFER_RESULT"
  val sqlCache = "SQL_CACHE"
  val sqlNoCache = "SQL_NO_CACHE"
  val sqlCalcFoundRows = "SQL_CALC_FOUND_ROWS"
}


object MySQLOperatorSet extends SQLOperatorSet

object MySQLQueryBuilder extends AbstractQueryBuilder {
  val operators = MySQLOperatorSet
  val syntax = MySQLSyntax
}

private[morpheus] class MySQLQueryColumn[T : SQLPrimitive](col: AbstractColumn[T]) extends AbstractQueryColumn[T](col)
