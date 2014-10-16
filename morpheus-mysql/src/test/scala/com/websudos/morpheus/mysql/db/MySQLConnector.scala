/*
 * Copyright 2014 websudos ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.websudos.morpheus.mysql.db

import com.twitter.conversions.time._
import com.twitter.finagle.exp.Mysql
import com.twitter.util.Await
import com.websudos.morpheus.Client
import com.websudos.morpheus.mysql.{MySQLResult, MySQLRow, MySQLClient}

object MySQLConnector {
  lazy val client = {
    val c = Mysql.withCredentials("root", "").newRichClient("localhost:3306")
    Await.result(c.ping(), 2.seconds)
    c
  }

}


trait MySQLSuite {
  implicit lazy val client: Client[MySQLRow, MySQLResult] = new MySQLClient(MySQLConnector.client)
}
