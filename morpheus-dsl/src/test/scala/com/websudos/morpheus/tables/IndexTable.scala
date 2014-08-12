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

case class IndexedRecord(name: String, value: Long)

class IndexTable extends MySQLTable[IndexTable, IndexedRecord] {

  object name extends StringColumn(this)

  object value extends LongColumn(this)

  object index extends Index(name, value)

  def fromRow(row: Row): IndexedRecord = {
    IndexedRecord(
      name(row),
      value(row)
    )
  }

}
