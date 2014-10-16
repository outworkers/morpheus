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

import com.websudos.morpheus.{Row, SQLPrimitive}
import com.websudos.morpheus.SQLPrimitives._
import com.websudos.morpheus.dsl.BaseTable
import com.websudos.morpheus.query._

sealed abstract class Operator {}

sealed class AsciiOperator extends Operator {

  final def apply[T : SQLPrimitive](value: T): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.ascii(implicitly[SQLPrimitive[T]].toSQL(value))
    ) {
      def fromRow(row: Row): String = ""
    }
  }
}

sealed class BinOperator extends Operator {
  final def apply[T : SQLPrimitive : Numeric](value: T): SelectOperatorClause[Int] = {
    new SelectOperatorClause[Int](
      DefaultQueryBuilder.bin(implicitly[SQLPrimitive[T]].toSQL(value))
    ) {
      def fromRow(row: Row): Int = 5
    }
  }
}

sealed class BitLengthOperator extends Operator {
  final def apply[T : SQLPrimitive](value: T): SelectOperatorClause[Int] = {
    new SelectOperatorClause[Int](
      DefaultQueryBuilder.bitLength(implicitly[SQLPrimitive[T]].toSQL(value))
    ) {
      def fromRow(row: Row): Int = 5
    }
  }
}

sealed class CharLengthOperator extends Operator {
  final def apply[T: SQLPrimitive](value: T): SelectOperatorClause[Int] = {
    new SelectOperatorClause[Int](
      DefaultQueryBuilder.charLength(implicitly[SQLPrimitive[T]].toSQL(value))
    ) {
      def fromRow(row: Row): Int = 5
    }
  }
}

sealed class CharacterLengthOperator extends Operator {
  final def apply[T: SQLPrimitive](value: T): SelectOperatorClause[Int] = {
    new SelectOperatorClause[Int](
      DefaultQueryBuilder.characterLength(implicitly[SQLPrimitive[T]].toSQL(value))
    ) {
      def fromRow(row: Row): Int = 5
    }
  }
}

sealed class ConcatWsOperator extends Operator {
  val primitive = implicitly[SQLPrimitive[String]]

  final def apply(values: List[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.concatWs(values.map(primitive.toSQL))
    ) {
      def fromRow(row: Row): String = ""
    }
  }

  final def apply(sep: String, values: List[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.concatWs(primitive.toSQL(sep) +: values.map(primitive.toSQL))
    ) {
      def fromRow(row: Row): String = ""
    }
  }
}

sealed class ConcatOperator extends Operator {
  val primitive = implicitly[SQLPrimitive[String]]

  final def apply(values: List[String]): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.concat(values.map(primitive.toSQL))
    ) {
      def fromRow(row: Row): String = ""
    }
  }

  final def apply(values: String*): SelectOperatorClause[String] = {
    new SelectOperatorClause[String](
      DefaultQueryBuilder.concat(values.map(primitive.toSQL).toList)
    ) {
      override def fromRow(row: Row): String = ""
    }
  }
}


sealed class ExistsOperator extends Operator {

  final def apply[
    T <: BaseTable[T, R],
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

  final def apply[
    T <: BaseTable[T, R],
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


private[morpheus] trait SQLOperatorSet {


  object ascii extends AsciiOperator
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
