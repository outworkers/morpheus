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

import com.websudos.morpheus.column.{DefaultForeignKeyConstraints, AbstractColumn, ModifyImplicits}
import com.websudos.morpheus.query.AbstractQueryColumn

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
trait DefaultImportsDefinition extends ModifyImplicits with DefaultForeignKeyConstraints {

  type SQLPrimitive[T] = com.websudos.morpheus.SQLPrimitive[T]
  type Table[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.dsl.Table[Owner, Record]

  type Index[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.Index[Owner, Record]
  type PrimitiveColumn[Owner <: Table[Owner, Record], Record, ValueType] = com.websudos.morpheus.column.PrimitiveColumn[Owner, Record, ValueType]

  type LongColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.PrimitiveColumn[Owner, Record, Long]

  type TinyIntColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.TinyIntColumn[Owner, Record]
  type SmallIntColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.SmallIntColumn[Owner, Record]
  type IntColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.IntColumn[Owner, Record]
  type MediumIntColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.MediumIntColumn[Owner, Record]
  type YearColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.YearColumn[Owner, Record]

  type TinyTextColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.TinyTextColumn[Owner, Record]
  type MediumTextColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.MediumTextColumn[Owner, Record]
  type LongTextColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.LongTextColumn[Owner, Record]
  type TextColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.TextColumn[Owner, Record]

  type CharColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.CharColumn[Owner, Record]
  type VarcharColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.VarcharColumn[Owner, Record]

  type TinyBlobColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.TinyBlobColumn[Owner, Record]
  type BlobColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.BlobColumn[Owner, Record]
  type MediumBlobColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.MediumBlobColumn[Owner, Record]
  type LongBlobColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.LongBlobColumn[Owner, Record]

  type Result = com.twitter.finagle.exp.mysql.Result
  type Row = com.twitter.finagle.exp.mysql.Row

  type ForeignKey[Owner <: Table[Owner, Record], Record, T1 <: Table[T1, _]] = com.websudos.morpheus.column.ForeignKey[Owner, Record, T1]
  type PrimaryKey[ValueType] = com.websudos.morpheus.keys.PrimaryKey[ValueType]
  type UniqueKey[ValueType] = com.websudos.morpheus.keys.UniqueKey[ValueType]
  type NotNull = com.websudos.morpheus.keys.NotNull
  type Autoincrement = com.websudos.morpheus.keys.Autoincrement

  implicit def columnToQueryColumn[T: SQLPrimitive](col: AbstractColumn[T]): AbstractQueryColumn[T]

}

