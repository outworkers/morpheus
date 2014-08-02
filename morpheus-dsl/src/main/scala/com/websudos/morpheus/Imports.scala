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

package com.websudos.morpheus

object Imports extends SQLPrimitives {

  type Table[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.dsl.Table[Owner, Record]
  type PrimitiveColumn[Owner <: Table[Owner, Record], Record, ValueType] = com.websudos.morpheus.column.PrimitiveColumn[Owner, Record, ValueType]

  type StringColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.PrimitiveColumn[Owner, Record, String]
  type LongColumn[Owner <: Table[Owner, Record], Record] = com.websudos.morpheus.column.PrimitiveColumn[Owner, Record, Long]

  type Result = com.twitter.finagle.exp.mysql.Result
  type Row = com.twitter.finagle.exp.mysql.Row

}
