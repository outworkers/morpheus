/*
 * Copyright 2013 - 2017 Outworkers, Limited.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
