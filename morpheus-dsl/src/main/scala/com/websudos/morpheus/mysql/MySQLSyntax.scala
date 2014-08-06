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

package com.websudos.morpheus.mysql

import com.websudos.morpheus.query.AbstractSQLSyntax

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
