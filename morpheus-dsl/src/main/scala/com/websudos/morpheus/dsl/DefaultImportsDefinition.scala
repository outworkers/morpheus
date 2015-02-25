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

package com.websudos.morpheus.dsl

import com.websudos.morpheus.Row
import com.websudos.morpheus.column.{AbstractColumn, DefaultForeignKeyConstraints}
import com.websudos.morpheus.query.AbstractQueryColumn

/**
 * As the implementation of SQL builders may differ depending on the type of SQL database in use, we will provide a series of specific imports for each
 * individual database.
 *
 * For instance, for MySQL a user will {{ import com.websudos.morpheus.mysql._ }}, for Postgress the user will {{import com.websudos.morpheus
 * .postgres._ }} and so on. To make our life easy when we reach the point of writing disjoint import objects for the various SQL databases,
 * this trait will provide the base implementation of an "all you can eat" imports object.
 *
 * This includes the various conversions and implicit mechanisms which will have there own underlying structure. As any underlying variable implementation
 * between databases will still extend the same base implementations internally, we can easily override with the settings we need in any place we want.
 *
 * Thanks to Scala's ability to override the below type aliases, we can easily swap out the base BaseTable implementation for a MySQL specific BaseTable
 * implementation in a manner that's completely invisible to the API user. The naming of things can stay the same while morpheus invisibly implements all
 * necessary discrepancies.
 */
trait DefaultImportsDefinition extends DefaultForeignKeyConstraints {

  type SQLPrimitive[T] = com.websudos.morpheus.SQLPrimitive[T]

  type BaseTable[Owner <: BaseTable[Owner, Record, TableRow], Record, TableRow <: Row] = com.websudos.morpheus.dsl.BaseTable[Owner, Record, TableRow]


  type PrimaryKey[ValueType] = com.websudos.morpheus.keys.PrimaryKey[ValueType]
  type UniqueKey[ValueType] = com.websudos.morpheus.keys.UniqueKey[ValueType]
  type NotNull = com.websudos.morpheus.keys.NotNull
  type Autoincrement = com.websudos.morpheus.keys.Autoincrement
  type Zerofill[ValueType] = com.websudos.morpheus.keys.Zerofill[ValueType]

  implicit def columnToQueryColumn[T: SQLPrimitive](col: AbstractColumn[T]): AbstractQueryColumn[T]

}

