/*
 * Copyright 2015 websudos ltd.
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

package com.websudos.morpheus.sql

import com.websudos.morpheus.column._
import shapeless.{<:!<, =:!=}

trait SqlKeys {

  abstract class ForeignKey[T <: BaseTable[T, R, DefaultRow], R, T1 <: BaseTable[T1, _, DefaultRow]]
  (origin: T, columns: IndexColumn#NonIndexColumn[T1]*)
  (implicit ev: T =:!= T1, ev2: IndexColumn#NonIndexColumn[T1] <:!< IndexColumn) extends AbstractForeignKey[T, R, DefaultRow, T1](origin, columns: _*)

  class Index[T <: BaseTable[T, R, DefaultRow], R](columns: IndexColumn#NonIndexColumn[_]*)(implicit ev:
    IndexColumn#NonIndexColumn[_] <:!< IndexColumn) extends AbstractIndex[T, R, DefaultRow](columns: _*)

}


trait SqlPrimitiveColumns {
  class LongColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow])(implicit ev: SQLPrimitive[Long])
    extends AbstractLongColumn[T, R, DefaultRow](t)
}


trait SqlColumns {

  class IntColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[Int])
    extends AbstractIntColumn[T, R, DefaultRow](t, limit)

  class MediumIntColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[Int])
    extends AbstractMediumIntColumn[T, R, DefaultRow](t, limit)

  class SmallIntColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[Int])
    extends AbstractSmallIntColumn[T, R, DefaultRow](t, limit)

  class TinyIntColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[Int])
    extends AbstractTinyIntColumn[T, R, DefaultRow](t, limit)

  class YearColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow])(implicit ev: SQLPrimitive[Int])
    extends AbstractYearColumn[T, R, DefaultRow](t)


  class BlobColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractBlobColumn[T, R, DefaultRow](t)

  class LongTextColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractLongTextColumn[T, R, DefaultRow](t)

  class MediumBlobColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractMediumBlobColumn[T, R, DefaultRow](t)

  class MediumTextColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractMediumTextColumn[T, R, DefaultRow](t)

  class TextColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractTextColumn[T, R, DefaultRow](t)

  class TinyBlobColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractTinyBlobColumn[T, R, DefaultRow](t)

  class TinyTextColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractTinyTextColumn[T, R, DefaultRow](t)

  class VarcharColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractVarcharColumn[T, R, DefaultRow](t)

  class CharColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractCharColumn[T, R, DefaultRow](t)

  class LongBlobColumn[T <: BaseTable[T, R, DefaultRow], R](t: BaseTable[T, R, DefaultRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractLongBlobColumn[T, R, DefaultRow](t)
}
