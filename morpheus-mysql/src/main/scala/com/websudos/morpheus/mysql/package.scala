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

package com.websudos.morpheus

import com.websudos.morpheus.query.Unterminated
import com.websudos.morpheus.query.AssignUnchainned
import com.websudos.morpheus.query.Unchainned
import com.websudos.morpheus.query.Unlimited
import com.websudos.morpheus.query.Unordered
import com.websudos.morpheus.query.Ungroupped
import com.websudos.morpheus.query.SelectType
import com.websudos.morpheus.query.Query
import com.websudos.morpheus.column.AbstractColumn
import com.websudos.morpheus.operators.MySQLOperatorSet
import com.websudos.morpheus.dsl.DefaultImportsDefinition


package object mysql extends DefaultImportsDefinition with MySQLPrimitives with MySQLOperatorSet {


  override implicit def columnToQueryColumn[T : SQLPrimitive](col: AbstractColumn[T]): MySQLQueryColumn[T] = new MySQLQueryColumn[T](col)

  implicit def rootSelectQueryToQuery[T <: Table[T, _], R](root: MySQLRootSelectQuery[T, R]): Query[T, R, SelectType, Ungroupped, Unordered, Unlimited,
    Unchainned, AssignUnchainned, Unterminated] = {
    new Query(
      root.table,
      root.st.*,
      root.rowFunc
    )
  }

  type Table[Owner <: BaseTable[Owner, Record], Record] = com.websudos.morpheus.mysql.MySQLTable[Owner, Record]
}
