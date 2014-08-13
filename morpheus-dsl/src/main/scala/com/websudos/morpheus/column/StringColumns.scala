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

package com.websudos.morpheus.column

import com.websudos.morpheus.SQLPrimitives.StringIsSQLPrimitive
import com.websudos.morpheus.dsl.Table
import com.websudos.morpheus.query.DefaultSQLDataTypes

sealed abstract class LimitedTextColumn[T <: Table[T, R], R](t: Table[T, R], protected[this] val limit: Int) extends PrimitiveColumn[T, R, String](t)(StringIsSQLPrimitive)

class VarcharColumn[T <: Table[T, R], R](t: Table[T, R], limit: Int = KnownTypeLimits.varcharLimit) extends LimitedTextColumn(t, limit) {
  override def sqlType = s"${DefaultSQLDataTypes.varchar}($limit)"
}

class TinyTextColumn[T <: Table[T, R], R](t: Table[T, R]) extends LimitedTextColumn(t, 0) {
  override def sqlType = DefaultSQLDataTypes.tinyText
}

class TextColumn[T <: Table[T, R], R](t: Table[T, R]) extends LimitedTextColumn(t, 0) {
  override def sqlType = DefaultSQLDataTypes.text
}

class MediumTextColumn[T <: Table[T, R], R](t: Table[T, R]) extends LimitedTextColumn(t, 0) {
  override def sqlType = DefaultSQLDataTypes.mediumText
}

class LongTextColumn[T <: Table[T, R], R](t: Table[T, R]) extends LimitedTextColumn(t, 0) {
  override def sqlType = DefaultSQLDataTypes.longText
}

class TinyBlobColumn[T <: Table[T, R], R](t: Table[T, R]) extends LimitedTextColumn(t, 0) {
  override def sqlType = DefaultSQLDataTypes.tinyBlob
}

class BlobColumn[T <: Table[T, R], R](t: Table[T, R]) extends LimitedTextColumn(t, 0) {
  override def sqlType = DefaultSQLDataTypes.blob
}

class MediumBlobColumn[T <: Table[T, R], R](t: Table[T, R]) extends LimitedTextColumn(t, 0) {
  override def sqlType = DefaultSQLDataTypes.mediumBlob
}

class LongBlobColumn[T <: Table[T, R], R](t: Table[T, R]) extends LimitedTextColumn(t, 0) {
  override def sqlType = DefaultSQLDataTypes.longBlob
}
