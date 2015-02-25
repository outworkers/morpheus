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

import com.websudos.morpheus.column.{DefaultForeignKeyConstraints, AbstractColumn}
import com.websudos.morpheus.dsl.DefaultImportsDefinition
import com.websudos.morpheus.mysql.query.{MySQLRootSelectQuery, MySQLSelectQuery}
import com.websudos.morpheus.operators.MySQLOperatorSet
import com.websudos.morpheus.query.{AssignUnchainned, Unchainned, Ungroupped, Unlimited, Unordered, Unterminated}


package object mysql extends DefaultImportsDefinition
  with MySQLImplicits
  with MySQLPrimitives
  with MySQLOperatorSet
  with MySQLColumns
  with MySQLKeys
  with MySQLPrimitiveColumns
  with DefaultForeignKeyConstraints {

  override implicit def columnToQueryColumn[T : SQLPrimitive](col: AbstractColumn[T]): MySQLQueryColumn[T] = new MySQLQueryColumn[T](col)

  implicit def rootSelectQueryToQuery[T <: Table[T, _], R](root: MySQLRootSelectQuery[T, R]): MySQLSelectQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new MySQLSelectQuery(
      root.table,
      root.st.*,
      root.rowFunc
    )
  }

  type Table[Owner <: BaseTable[Owner, Record, MySQLRow], Record] = com.websudos.morpheus.mysql.MySQLTable[Owner, Record]
}
