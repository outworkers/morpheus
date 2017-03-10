/*
 * Copyright 2013 - 2017 Outworkers Ltd.
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
package com.outworkers.morpheus

package mysql
package db

import java.util.concurrent.TimeUnit

import com.outworkers.morpheus.Client
import com.outworkers.morpheus.mysql.{Client, Result, Row}
import com.twitter.finagle.exp.Mysql
import org.scalatest.concurrent.{PatienceConfiguration, ScalaFutures, Waiters}
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, Suite}

object Connector {

  def isRunningUnderTravis: Boolean = sys.env.contains("TRAVIS")

  val user = if (isRunningUnderTravis) "travis" else "morpheus"
  val pwd = "morpheus23!"

  /**
   * This client is meant to connect to the Travis CI default MySQL service.
   */
  lazy val client = Mysql.client
    .withCredentials(user, pwd)
    .withDatabase("morpheus_test")
    .newRichClient("127.0.0.1:3306")
}

trait BaseSuite extends Waiters
  with ScalaFutures
  with OptionValues
  with Matchers
  with BeforeAndAfterAll {

  this: Suite =>

  implicit lazy val client = new mysql.Client(Connector.client)

  protected[this] val defaultScalaTimeoutSeconds = 10

  private[this] val defaultScalaInterval = 50L

  implicit val defaultScalaTimeout = scala.concurrent.duration.Duration(defaultScalaTimeoutSeconds, TimeUnit.SECONDS)

  private[this] val defaultTimeoutSpan = Span(defaultScalaTimeoutSeconds, Seconds)

  implicit val defaultTimeout: PatienceConfiguration.Timeout = timeout(defaultTimeoutSpan)

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  override implicit val patienceConfig = PatienceConfig(
    timeout = defaultTimeoutSpan,
    interval = Span(defaultScalaInterval, Millis)
  )
}

// CREATE USER 'morpheus'@'localhost' IDENTIFIED BY 'morpheus23!';
// GRANT ALL PRIVILEGES ON * . * TO 'morpheus'@'localhost';
