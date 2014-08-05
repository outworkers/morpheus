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

import com.twitter.finagle.exp.mysql.Row
import com.websudos.morpheus.dsl.Table
import com.websudos.morpheus.query._

private[morpheus] class MySQLRootSelectQuery[T <: Table[T, _], R](table: T, st: MySQLSelectSyntaxBlock, rowFunc: Row => R)
  extends AbstractRootSelectQuery[T, R](table, st, rowFunc) {

  def distinctRow: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.distinctRow, rowFunc)
  }

  def highPriority: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.highPriority, rowFunc)
  }

  def straightJoin: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.straightJoin, rowFunc)
  }

  def sqlSmallResult: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.sqlSmallResult, rowFunc)
  }

  def sqlBigResult: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.sqlBigResult, rowFunc)
  }

  def sqlBufferResult: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.sqlBufferResult, rowFunc)
  }

  def sqlCache: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.sqlCache, rowFunc)
  }

  def sqlNoCache: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.sqlNoCache, rowFunc)
  }

  def sqlCalcFoundRows: Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(table, st.sqlCalcFoundRows, rowFunc)
  }
}
