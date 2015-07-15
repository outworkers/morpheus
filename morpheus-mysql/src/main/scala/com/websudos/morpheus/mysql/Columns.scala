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

package com.websudos.morpheus.mysql

import com.websudos.morpheus.column._
import shapeless.{<:!<, =:!=}

trait MySQLKeys {

  abstract class ForeignKey[T <: BaseTable[T, R, MySQLRow], R, T1 <: BaseTable[T1, _, MySQLRow]]
  (origin: T, columns: IndexColumn#NonIndexColumn[T1]*)
  (implicit ev: T =:!= T1, ev2: IndexColumn#NonIndexColumn[T1] <:!< IndexColumn) extends AbstractForeignKey[T, R, MySQLRow, T1](origin, columns: _*)


  class Index[T <: BaseTable[T, R, MySQLRow], R](columns: IndexColumn#NonIndexColumn[_]*)(implicit ev:
  IndexColumn#NonIndexColumn[_] <:!< IndexColumn) extends AbstractIndex[T, R, MySQLRow](columns: _*)

}

trait MySQLPrimitiveColumns {

  class IntColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[Int])
    extends AbstractIntColumn[T, R, MySQLRow](t, limit)

  class SmallIntColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[Int])
    extends AbstractSmallIntColumn[T, R, MySQLRow](t, limit)

  class TinyIntColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[Int])
    extends AbstractTinyIntColumn[T, R, MySQLRow](t, limit)

  class MediumIntColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[Int])
    extends AbstractMediumIntColumn[T, R, MySQLRow](t, limit)
}

trait MySQLColumns {

  class BlobColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractBlobColumn[T, R, MySQLRow](t, limit)

  class TextColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractTextColumn[T, R, MySQLRow](t, limit)

  class LongTextColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractLongTextColumn[T, R, MySQLRow](t, limit)

  class LongBlobColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractLongBlobColumn[T, R, MySQLRow](t, limit)

  class MediumBlobColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractMediumBlobColumn[T, R, MySQLRow](t, limit)

  class MediumTextColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractMediumTextColumn[T, R, MySQLRow](t, limit)

  class TinyBlobColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractTinyBlobColumn[T, R, MySQLRow](t, limit)

  class TinyTextColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractTinyTextColumn[T, R, MySQLRow](t, limit)

  class VarcharColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractVarcharColumn[T, R, MySQLRow](t, limit)

  class CharColumn[T <: BaseTable[T, R, MySQLRow], R](t: BaseTable[T, R, MySQLRow], limit: Int = 0)(implicit ev: SQLPrimitive[String])
    extends AbstractCharColumn[T, R, MySQLRow](t, limit)


}
