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

import scala.annotation.implicitNotFound

import com.twitter.finagle.exp.mysql.Row
import com.websudos.morpheus.query._
import com.websudos.morpheus.{ SQLPrimitive, SQLPrimitives }
import com.websudos.morpheus.dsl.Table
import com.websudos.morpheus.query.QueryAssignment
import com.websudos.morpheus.query.Query

private[morpheus] trait AbstractColumn[@specialized(Int, Double, Float, Long, Boolean, Short) T] {

  type Value = T

  lazy val name: String = getClass.getSimpleName.replaceAll("\\$+", "").replaceAll("(anonfun\\d+.+\\d+)|", "")

  def sqlType: String

  def table: Table[_, _]

  def toQueryString(v: T): String
}

sealed abstract class SelectColumn[T](val col: AbstractColumn[_]) {
  def apply(r: Row): T
}

abstract class Column[Owner <: Table[Owner, Record], Record, T](val table: Table[Owner, Record]) extends AbstractColumn[T] {

  def optional(r: Row): Option[T]

  def apply(r: Row): T = optional(r).getOrElse(throw new Exception(s"can't extract required value for column '$name'"))
}


private[morpheus] abstract class AbstractModifyColumn[RR](col: AbstractColumn[RR]) {

  def toQueryString(v: RR): String = col.toQueryString(v)

  def setTo(value: RR): QueryAssignment = QueryAssignment(col.table.queryBuilder.setTo(col.name, toQueryString(value)))
}


@implicitNotFound(msg = "Type ${RR} must be a MySQL primitive")
class PrimitiveColumn[T <: Table[T, R], R, @specialized(Int, Double, Float, Long) RR: SQLPrimitive](t: Table[T, R])
  extends Column[T, R, RR](t) {

  def sqlType: String = SQLPrimitives[RR].sqlType
  def toQueryString(v: RR): String = SQLPrimitives[RR].toSQL(v)

  def optional(r: Row): Option[RR] =
    implicitly[SQLPrimitive[RR]].fromRow(r, name)
}


sealed trait ModifyImplicits {

  implicit class SelectColumnRequired[Owner <: Table[Owner, Record], Record, T](col: Column[Owner, Record, T]) extends SelectColumn[T](col) {
    def apply(r: Row): T = col.apply(r)
  }


  implicit class ModifyColumn[RR](col: AbstractColumn[RR]) extends AbstractModifyColumn[RR](col)

  /**
   * This defines an implicit conversion from a RootUpdateQuery to an UpdateQuery, making the UPDATE syntax block invisible to the end user.
   * Much like a decision block, a UpdateSyntaxBlock needs a decision branch to follow, may that be nothing, LOW_PRIORITY or IGNORE.
   *
   * The one catch is that this form of "exit" from an un-executable RootUpdateQuery will directly translate the query to an "UPDATE tableName"
   * query, meaning no UPDATE operators will be used in the default serialisation.
   *
   * The simple assumption made here is that since the user didn't use any other provided method, such as "lowPriority" or "ignore" the desired behaviour is
   * a full select.
   *
   * @param root The RootSelectQuery to convert.
   * @tparam T The table owning the record.
   * @tparam R The record type.
   * @return An executable SelectQuery.
   */
  implicit def rootUpdateQueryToSelectQuery[T <: Table[T, _], R](root: RootUpdateQuery[T, R]): Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(
      root.table,
      root.st.all,
      root.rowFunc
    )
  }

  implicit def rootUpdateQueryToAssignQuery[T <: Table[T, _], R](root: RootUpdateQuery[T, R]): AssignmentsQuery[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new AssignmentsQuery(
      new Query(
        root.table,
        root.st.all,
        root.rowFunc
      )
    )
  }


  /**
   * This defines an implicit conversion from a RootUpdateQuery to an UpdateQuery, making the UPDATE syntax block invisible to the end user.
   * Much like a decision block, a UpdateSyntaxBlock needs a decision branch to follow, may that be nothing, LOW_PRIORITY or IGNORE.
   *
   * The one catch is that this form of "exit" from an un-executable RootUpdateQuery will directly translate the query to an "UPDATE tableName"
   * query, meaning no UPDATE operators will be used in the default serialisation.
   *
   * The simple assumption made here is that since the user didn't use any other provided method, such as "lowPriority" or "ignore" the desired behaviour is
   * a full select.
   *
   * @param root The RootSelectQuery to convert.
   * @tparam T The table owning the record.
   * @tparam R The record type.
   * @return An executable SelectQuery.
   */
  implicit def rootDeleteQueryToDeleteQuery[T <: Table[T, _], R](root: RootDeleteQuery[T, R]): Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(
      root.table,
      root.st.all,
      root.rowFunc
    )
  }

  /**
   * This defines an implicit conversion from a RootSelectQuery to a SelectQuery, making the SELECT syntax block invisible to the end user.
   * Much like a decision block, a SelectSyntaxBlock needs a decision branch to follow, may that be DISTINCT, ALL or DISTINCTROW as per the SQL spec.
   *
   * The one catch is that this form of "exit" from an un-executable RootSelectQuery will directly translate the query to a "SELECT fields* FROM tableName"
   * query, meaning no SELECT operators will be used in the serialisation.
   *
   * The simple assumption made here is that since the user didn't use any other provided method, such as "all", "distinct" or "distinctrow",
   * the desired behaviour is a full select.
   *
   * @param root The RootSelectQuery to convert.
   * @tparam T The table owning the record.
   * @tparam R The record type.
   * @return An executable SelectQuery.
   */
  implicit def rootSelectQueryToSelectQuery[T <: Table[T, _], R](root: RootSelectQuery[T, R]): Query[T, R, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned] = {
    new Query(
      root.table,
      root.st.*,
      root.rowFunc
    )
  }


  implicit def queryToAssignmentsQuery[
    T <: Table[T, _],
    R,
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    C <: ChainBind,
    AC <: AssignBind
  ](query: Query[T, R, G, O, L, C, AC]): AssignmentsQuery[T, R, G, O, L, C, AC] = {
    new AssignmentsQuery(query)
  }

  implicit def assignmentToQuery[
    T <: Table[T, _],
    R,
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    C <: ChainBind,
    AC <: AssignBind
  ](assignment: AssignmentsQuery[T, R, G, O, L, C, AC]): Query[T, R, G, O, L, C, AC] = assignment.query
}


private[morpheus] trait FullDslDefinition extends ModifyImplicits {

}
