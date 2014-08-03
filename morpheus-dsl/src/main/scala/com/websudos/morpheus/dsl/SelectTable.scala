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

import com.twitter.finagle.exp.mysql.Row
import com.websudos.morpheus.column.SelectColumn
import com.websudos.morpheus.query.{DefaultSQLOperators, SelectSyntaxBlock, RootSelectQuery}


/**
 * This wonderfully bloated trait is the one where we provide all select methods and partial select methods.
 * Since partial select methods will return type-safe tuples but Scala cannot correctly infer the type for varargs and pass it along to create an adjacent
 * Tuple, we provide a long series of methods which allow partial selects of up to 22 fields, as that's the maximum number of fields a Scala tuple can have.
 * @tparam Owner The table owning the record.
 * @tparam Record The record type.
 */
private[morpheus] trait SelectTable[Owner <: Table[Owner, Record], Record] {
  self: Table[Owner, Record] =>

  /**
   * This is the SELECT * query, where the user won't specify any partial select.
   * @return An instance of a RootSelectQuery.
   */
  def select: RootSelectQuery[Owner, Record] = {
    new RootSelectQuery[Owner, Record](
      this.asInstanceOf[Owner],
      new SelectSyntaxBlock[Owner, Record](DefaultSQLOperators.select, tableName, fromRow),
      fromRow
    )
  }

  /**
   * This is the SELECT column1 query, where a single column is specified to be partially selected.
   * @return An instance of a RootSelectQuery.
   */
  def select[T1](f1: Owner => SelectColumn[T1]): RootSelectQuery[Owner, T1] = {

    val t = this.asInstanceOf[Owner]
    val c1: SelectColumn[T1] = f1(t)
    def rowFunc(row: Row): T1 = c1(row)

    new RootSelectQuery[Owner, T1](
      this.asInstanceOf[Owner],
      new SelectSyntaxBlock[Owner, T1](DefaultSQLOperators.select, tableName, rowFunc, List(c1.col.name)),
      rowFunc
    )
  }

  /**
   * This is the SELECT column1 column2 query, where 2 columns are specified to be partially selected.
   * @return An instance of a RootSelectQuery.
   */
  def select[T1, T2](f1: Owner => SelectColumn[T1], f2: Owner => SelectColumn[T2]): RootSelectQuery[Owner, (T1, T2)] = {

    val t = this.asInstanceOf[Owner]
    val c1: SelectColumn[T1] = f1(t)
    val c2: SelectColumn[T2] = f2(t)
    def rowFunc(row: Row): (T1, T2) = Tuple2(c1(row), c2(row))

    new RootSelectQuery[Owner, (T1, T2)](
      this.asInstanceOf[Owner],
      new SelectSyntaxBlock[Owner, (T1, T2)](DefaultSQLOperators.select, tableName, rowFunc, List(c1.col.name, c2.col.name)),
      rowFunc
    )
  }

  /**
   * This is the SELECT column1 column2 column3 query, where 3 columns are specified to be partially selected.
   * @return An instance of a RootSelectQuery.
   */
  def select[T1, T2, T3](f1: Owner => SelectColumn[T1], f2: Owner => SelectColumn[T2], f3: Owner => SelectColumn[T3]): RootSelectQuery[Owner, (T1, T2, T3)] = {

    val t = this.asInstanceOf[Owner]
    val c1: SelectColumn[T1] = f1(t)
    val c2: SelectColumn[T2] = f2(t)
    val c3: SelectColumn[T3] = f3(t)

    def rowFunc(row: Row): (T1, T2, T3) = Tuple3(c1(row), c2(row), c3(row) )

    new RootSelectQuery[Owner, (T1, T2, T3)](
      this.asInstanceOf[Owner],
      new SelectSyntaxBlock[Owner, (T1, T2, T3)](DefaultSQLOperators.select, tableName, rowFunc, List(c1.col.name, c2.col.name, c3.col.name)),
      rowFunc
    )
  }

  /**
   * This is the SELECT column1 column2 column3 column4 query, where 4 columns are specified to be partially selected.
   * @return An instance of a RootSelectQuery.
   */
  def select[T1, T2, T3, T4](f1: Owner => SelectColumn[T1], f2: Owner => SelectColumn[T2], f3: Owner => SelectColumn[T3],
                             f4: Owner => SelectColumn[T4]): RootSelectQuery[Owner, (T1, T2,
    T3, T4)] = {

    val t = this.asInstanceOf[Owner]
    val c1: SelectColumn[T1] = f1(t)
    val c2: SelectColumn[T2] = f2(t)
    val c3: SelectColumn[T3] = f3(t)
    val c4: SelectColumn[T4] = f4(t)

    def rowFunc(row: Row): (T1, T2, T3, T4) = Tuple4(c1(row), c2(row), c3(row), c4(row))

    new RootSelectQuery[Owner, (T1, T2, T3, T4)](
      this.asInstanceOf[Owner],
      new SelectSyntaxBlock[Owner, (T1, T2, T3, T4)](DefaultSQLOperators.select, tableName, rowFunc, List(c1.col.name, c2.col.name, c3.col.name, c4.col.name)),
      rowFunc
    )
  }

  /**
   * This is the SELECT column1 column2 column3 column4 query, where 4 columns are specified to be partially selected.
   * @return An instance of a RootSelectQuery.
   */
  def select[T1, T2, T3, T4, T5](
    f1: Owner => SelectColumn[T1],
    f2: Owner => SelectColumn[T2],
    f3: Owner => SelectColumn[T3],
    f4: Owner => SelectColumn[T4],
    f5: Owner => SelectColumn[T5]): RootSelectQuery[Owner, (T1, T2, T3, T4, T5)] = {

    val t = this.asInstanceOf[Owner]
    val c1: SelectColumn[T1] = f1(t)
    val c2: SelectColumn[T2] = f2(t)
    val c3: SelectColumn[T3] = f3(t)
    val c4: SelectColumn[T4] = f4(t)
    val c5: SelectColumn[T5] = f5(t)

    def rowFunc(row: Row): (T1, T2, T3, T4, T5) = Tuple5(c1(row), c2(row), c3(row), c4(row), c5(row))

    new RootSelectQuery[Owner, (T1, T2, T3, T4, T5)](
      this.asInstanceOf[Owner],
      new SelectSyntaxBlock[Owner, (T1, T2, T3, T4, T5)](
        DefaultSQLOperators.select,
        tableName, rowFunc,
        List(c1.col.name, c2.col.name, c3.col.name, c4.col.name, c5.col.name)
      ),
      rowFunc
    )
  }
}
