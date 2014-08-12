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

package com.websudos.morpheus.tables

import com.websudos.morpheus.mysql.Imports._

case class IndexedRecord(id: Int, value: Long)

sealed class IndexTable extends MySQLTable[IndexTable, IndexedRecord] {

  object id extends SmallIntColumn(this) with PrimaryKey[Int] with NotNull with Autoincrement

  object value extends LongColumn(this)

  object index extends Index(id, value)

  def fromRow(row: Row): IndexedRecord = {
    IndexedRecord(
      id(row),
      value(row)
    )
  }
}

object IndexTable extends IndexTable

case class SimplePrimaryRecord(id: Int)

sealed class SimplePrimaryKeyTable extends MySQLTable[SimplePrimaryKeyTable, SimplePrimaryRecord] {

  object id extends IntColumn(this) with PrimaryKey[Int]

  object notNullId extends IntColumn(this) with PrimaryKey[Int] with NotNull

  object autoincrementedId extends IntColumn(this) with PrimaryKey[Int] with Autoincrement

  object indexId extends IntColumn(this) with PrimaryKey[Int] with NotNull with Autoincrement

  object foreignKey extends ForeignKey[SimplePrimaryKeyTable, SimplePrimaryRecord, IndexTable](this)(IndexTable.id, IndexTable.value)


  /**
   * The most notable and honorable of functions in this file, this is what allows our DSL to provide type-safety.
   * It works by requiring a user to define a type-safe mapping between a buffered Result and the above refined Record.
   *
   * Objects delimiting pre-defined columns also have a pre-defined "apply" method, allowing the user to simply autofill the type-safe mapping by using
   * pre-existing definitions.
   *
   * @param row The row incoming as a result from a MySQL query.
   * @return A Record instance.
   */
  override def fromRow(row: Row): SimplePrimaryRecord = SimplePrimaryRecord(id(row))
}

object SimplePrimaryKeyTable extends SimplePrimaryKeyTable
