/*
 *
 *  * Copyright 2014 websudos ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.websudos.morpheus.query

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future => ScalaFuture}

import com.twitter.finagle.exp.mysql.{Client, Result, Row}
import com.twitter.util.Future
import com.websudos.morpheus.dsl.{ResultSetOperations, Table}


trait SQLQuery extends ResultSetOperations {
  protected[morpheus] val query: String

  def future()(implicit session: Client): ScalaFuture[Result] = {
    queryToScalaFuture(query)
  }

  def execute()(implicit  session: Client): Future[Result] = {
    queryToFuture(query)
  }

}


trait SQLResultsQuery[T <: Table[T, _], R] extends SQLQuery {
  def fromRow(r: Row): R

  /**
   * Returns the first row from the select ignoring everything else.
   * @param client The MySQL client in use.
   * @return
   */
  def one()(implicit client: Client): ScalaFuture[Option[R]]

  /**
   * Get the result of an operation as a Twitter Future.
   * @param client The MySQL client in use.
   * @return A Twitter future wrapping the result.
   */
  def get()(implicit client: Client): Future[Option[R]]
}


class SelectQuery[T <: Table[T, _], R](protected[morpheus] val query: String, rowFunc: Row => R) extends SQLResultsQuery[T, R] {

  def fromRow(r: Row): R = rowFunc(r)

  def fetch()(implicit client: Client): ScalaFuture[Seq[R]] = {
    twitterToScala(client.select(query)(fromRow))
  }

  def collect()(implicit client: Client): Future[Seq[R]] = {
    client.select(query)(fromRow)
  }

  def one()(implicit client: Client): ScalaFuture[Option[R]] = {
    fetch.map(_.headOption)
  }

  def get()(implicit client: Client): Future[Option[R]] = {
    collect().map(_.headOption)
  }

}
