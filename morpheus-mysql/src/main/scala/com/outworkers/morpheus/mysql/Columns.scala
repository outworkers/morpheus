/*
 * Copyright 2013 - 2017 Outworkers, Limited.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.outworkers.morpheus.mysql

import com.outworkers.morpheus.column._
import shapeless.{<:!<, =:!=}

trait Keys {

  abstract class ForeignKey[
    T <: BaseTable[T, R, Row],
    R,
    T1 <: BaseTable[T1, _, Row]
  ](origin: T, columns: IndexColumn#NonIndexColumn[T1]*)(
    implicit ev: T =:!= T1,
    ev2: IndexColumn#NonIndexColumn[T1] <:!< IndexColumn
  ) extends AbstractForeignKey[T, R, Row, T1](origin, columns: _*)

  class Index[T <: BaseTable[T, R, Row], R](
    columns: IndexColumn#NonIndexColumn[_]*
  )(implicit ev: IndexColumn#NonIndexColumn[_] <:!< IndexColumn) extends AbstractIndex[T, R, Row](columns: _*)

}

trait PrimitiveColumns {

  class IntColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[Int])
    extends AbstractIntColumn[T, R, Row](t, limit)

  class SmallIntColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[Int])
    extends AbstractSmallIntColumn[T, R, Row](t, limit)

  class TinyIntColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[Int])
    extends AbstractTinyIntColumn[T, R, Row](t, limit)

  class MediumIntColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[Int])
    extends AbstractMediumIntColumn[T, R, Row](t, limit)
}

trait Columns {

  class BlobColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractBlobColumn[T, R, Row](t, limit)

  class TextColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractTextColumn[T, R, Row](t, limit)

  class LongTextColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractLongTextColumn[T, R, Row](t, limit)

  class LongBlobColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractLongBlobColumn[T, R, Row](t, limit)

  class MediumBlobColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractMediumBlobColumn[T, R, Row](t, limit)

  class MediumTextColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractMediumTextColumn[T, R, Row](t, limit)

  class TinyBlobColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractTinyBlobColumn[T, R, Row](t, limit)

  class TinyTextColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractTinyTextColumn[T, R, Row](t, limit)

  class VarcharColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractVarcharColumn[T, R, Row](t, limit)

  class CharColumn[T <: BaseTable[T, R, Row], R](t: BaseTable[T, R, Row], limit: Int = 0)(implicit ev: DataType[String])
    extends AbstractCharColumn[T, R, Row](t, limit)

  class EnumColumn[T <: BaseTable[T, R, Row], R, EnumType <: Enumeration](t: BaseTable[T, R, Row], enum: EnumType)(implicit ev: DataType[String])
    extends AbstractEnumColumn[T, R, Row, EnumType](t, enum) {
  }

}
