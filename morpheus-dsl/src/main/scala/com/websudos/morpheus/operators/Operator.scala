/*
 * Copyright 2014 websudos ltd.
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

package com.websudos.morpheus.operators

import com.websudos.morpheus.SQLPrimitive
import com.websudos.morpheus.dsl.Table
import com.websudos.morpheus.mysql.DefaultQueryBuilder
import com.websudos.morpheus.query._


sealed abstract class Operator  {}


sealed class AsciOperator extends Operator {

  def apply[T : SQLPrimitive](value: T): QueryCondition = {
    QueryCondition(
      DefaultQueryBuilder.asci(implicitly[SQLPrimitive[T]].toSQL(value))
    )
  }
}

sealed class BinOperator extends Operator {
  def apply[T : SQLPrimitive[T] : Numeric](value: T): QueryCondition = {
    QueryCondition(
      DefaultQueryBuilder.bin(implicitly[SQLPrimitive[T]].toSQL(value))
    )
  }
}

sealed class BitLengthOperator extends Operator {
  def apply[T : SQLPrimitive](value: T): QueryCondition = {
    QueryCondition(
      DefaultQueryBuilder.bitLength(implicitly[SQLPrimitive[T]].toSQL(value))
    )
  }
}

sealed class CharLengthOperator extends Operator {
  def apply[T: SQLPrimitive](value: T): QueryCondition = {
    QueryCondition(
      DefaultQueryBuilder.charLength(implicitly[SQLPrimitive[T]].toSQL(value))
    )
  }
}

sealed class CharacterLengthOperator extends Operator {
  def apply[T: SQLPrimitive](value: T): QueryCondition = {
    QueryCondition(
      DefaultQueryBuilder.characterLength(implicitly[SQLPrimitive[T]].toSQL(value))
    )
  }
}

sealed class ConcatWsOperator extends Operator {
  val primitive = implicitly[SQLPrimitive[String]]

  def apply(values: List[String]): QueryCondition = {
    QueryCondition(
      DefaultQueryBuilder.concatWs(values.map(primitive.toSQL))
    )
  }

  def apply(sep: String, values: List[String]): QueryCondition = {
    QueryCondition(
      DefaultQueryBuilder.concatWs(primitive.toSQL(sep) +: values.map(primitive.toSQL))
    )
  }
}

sealed class ConcatOperator extends Operator {
  val primitive = implicitly[SQLPrimitive[String]]

  def apply(values: List[String]): QueryCondition = {
    QueryCondition(
      DefaultQueryBuilder.concat(values.map(primitive.toSQL))
    )
  }
}


sealed class ExistsOperator extends Operator {

  def apply[
    T <: Table[T, R],
    R,
    Group <: GroupBind,
    Order <: OrderBind,
    Limit <: LimitBind,
    Chain <: ChainBind,
    AssignChain <: AssignBind
  ](query: Query[T, R, SelectType, Group, Order, Limit, Chain, AssignChain, Unterminated]): QueryCondition = {
    QueryCondition(
      query.table.queryBuilder.exists(query.query)
    )
  }
}


sealed class NotExistsOperator extends Operator {

  def apply[
    T <: Table[T, R],
    R,
    Group <: GroupBind,
    Order <: OrderBind,
    Limit <: LimitBind,
    Chain <: ChainBind,
    AssignChain <: AssignBind
  ](query: Query[T, R, SelectType, Group, Order, Limit, Chain, AssignChain, Unterminated]): QueryCondition = {
    QueryCondition(
      query.table.queryBuilder.notExists(query.query)
    )
  }
}


sealed trait SQLOperatorSet {


  object asci extends AsciOperator
  object bin extends BinOperator
  object bitLength extends BitLengthOperator
  object charLength extends CharLengthOperator
  object characterLength extends CharacterLengthOperator
  object concatWs extends ConcatWsOperator
  object concat extends ConcatOperator

  object exists extends ExistsOperator
  object notExists extends NotExistsOperator
}


trait MySQLOperatorSet extends SQLOperatorSet {}
