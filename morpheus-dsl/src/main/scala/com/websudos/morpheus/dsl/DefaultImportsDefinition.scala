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

package com.websudos.morpheus.dsl

import com.websudos.morpheus.column.{ AbstractColumn, ModifyImplicits }
import com.websudos.morpheus.query.QueryColumn

/**
 * As the implementation of SQL builders may differ depending on the type of SQL database in use, we will provide a series of specific imports for each
 * individual database.
 *
 * For instance, for MySQL a user will {@code import com.websudos.morpheus.MySQL.Imports._}, for Postgress the user will {@code import com.websudos.morpheus
 * .Postgres.Imports._ } and so on. To make our life easy when we reach the point of writing disjoint import objects for the various SQL databases,
 * this trait will provide the base implementation of an "all you can eat" imports object.
 *
 * This includes the various conversions and implicit mechanisms which will have there own underlying structure. As any underlying variable implementation
 * between databases will still extend the same base implementations internally, we can easily override with the settings we need in any place we want.
 *
 * Thanks to Scala's ability to override the below type aliases, we can easily swap out the base table implementation for a MySQL specific table
 * implementation in a manner that's completely invisible to the API user. The naming of things can stay the same while morpheus invisibly implements all
 * necessary discrepancies.
 */
trait DefaultImportsDefinition extends ModifyImplicits {

  type SQLPrimitive[T] = com.websudos.morpheus.SQLPrimitive[T]
  type Table[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.dsl.Table[Owner, Record]
  type PrimitiveColumn[Owner <: Table[Owner, Record], Record, ValueType] = com.websudos.morpheus.column.PrimitiveColumn[Owner, Record, ValueType]

  type StringColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.PrimitiveColumn[Owner, Record, String]
  type LongColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.PrimitiveColumn[Owner, Record, Long]

  type Result = com.twitter.finagle.exp.mysql.Result
  type Row = com.twitter.finagle.exp.mysql.Row

  implicit def columnToQueryColumn[T: SQLPrimitive](col: AbstractColumn[T]): QueryColumn[T]

}

