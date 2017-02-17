package com.outworkers.morpheus

import com.outworkers.morpheus.builder.DefaultQueryBuilder
import com.outworkers.morpheus.operators.Operator
import com.outworkers.morpheus.query.SelectOperatorClause

sealed class BinOperator extends Operator {
  final def apply[T](value: T)(implicit ev: SQLPrimitive[T], ev2: SQLPrimitive[Int], ev3: Numeric[T]): SelectOperatorClause[Int] = {
    new SelectOperatorClause[Int](
      DefaultQueryBuilder.bin(implicitly[SQLPrimitive[T]].toSQL(value))
    ) {
      def fromRow(row: Row): Int = 5
    }
  }
}
