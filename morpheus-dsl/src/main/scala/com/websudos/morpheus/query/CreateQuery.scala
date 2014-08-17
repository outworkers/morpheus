/*
 * Copyright 2014 websudos ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.websudos.morpheus.query

import com.twitter.finagle.exp.mysql.Row
import com.websudos.morpheus.dsl.Table

private[morpheus]abstract class AbstractCreateSyntaxBlock(query: String, tableName: String) extends AbstractSyntaxBlock {

  protected[this] val qb: SQLBuiltQuery = SQLBuiltQuery(query)

  def syntax: AbstractSQLSyntax

  def default: SQLBuiltQuery = {
    qb.pad.append(DefaultSQLSyntax.table)
      .forcePad.append(tableName)
  }

  def ifNotExists: SQLBuiltQuery = {
    qb.pad.append(DefaultSQLSyntax.table)
      .forcePad.append(syntax.ifNotExists)
      .forcePad.append(tableName)
  }

  def temporary: SQLBuiltQuery = {
    qb.pad
      .append(syntax.temporary)
      .forcePad.append(DefaultSQLSyntax.table)
      .forcePad.append(tableName)
  }
}

/**
 * This is the implementation of a root CREATE query, a wrapper around an abstract CREATE syntax block.
 *
 * This is used as the entry point to an SQL CREATE query, and it requires the user to provide "one more method" to fully specify a CREATE query.
 * The implicit conversion from a RootCreateQuery to a CreateQuery will automatically pick the "default" strategy below.
 *
 * @param table The table owning the record.
 * @param st The Abstract syntax block describing the possible decisions.
 * @param rowFunc The function used to map a result to a type-safe record.
 * @tparam T The type of the owning table.
 * @tparam R The type of the record.
 */
private[morpheus] abstract class AbstractRootCreateQuery[T <: Table[T, _], R](val table: T, val st: AbstractCreateSyntaxBlock, val rowFunc: Row => R) {

  def fromRow(r: Row): R = rowFunc(r)

  protected[this] type BaseCreateQuery = Query[T, R, CreateType, Ungroupped, Unordered, Unlimited, Unchainned, AssignUnchainned, Unterminated]

  private[morpheus] def default: BaseCreateQuery = {
    new Query(table, st.default, rowFunc)
  }

  def ifNotExists: BaseCreateQuery = {
    new Query(table, st.ifNotExists, rowFunc)
  }

  def temporary: BaseCreateQuery = {
    new Query(table, st.temporary, rowFunc)
  }

}

object DefaultMySQLEngines {
  val InnoDB = "InnoDB"
  val Memory = "MEMORY"
  val Heap = "HEAP"
  val Merge = "MERGE"
  val MrgMyLSAM = "MRG_MYISAM"
  val isam = "ISAM"
  val MrgISAM = "MRG_ISAM"
  val innoBase = "INNOBASE"
  val BDB = "BDB"
  val BerkleyDB = "BERKELEYDB"
  val NDBCluster = "NDBCLUSTER"
  val NDB = "NDB"
  val example = "EXAMPLE"
  val archive = "ARCHIVE"
  val csv = "CSV"
  val federated = "FEDERATED"
  val blackhole = "BLACKHOLE"
}

sealed abstract class SQLEngine(val value: String)

/**
 * This is the sequence of default available storage engines in the MySQL 5.0 specification.
 * For the official documentation, @see <a href="http://dev.mysql.com/doc/refman/5.0/en/show-engines.html">the MySQL 5.0 docs</a>.
 *
 * More recent versions of MySQL features far less available options. The official list is available on @see <a href="http://dev.mysql.com/doc/refman/5
 * .7/en/show-engines.html">the MySQL 5.7 docs</a> page.
 */
trait DefaultSQLEngines {
  case object InnoDB extends SQLEngine(DefaultMySQLEngines.InnoDB)
  case object InnoBase extends SQLEngine(DefaultMySQLEngines.innoBase)
  case object Memory extends SQLEngine(DefaultMySQLEngines.Memory)
  case object Heap extends SQLEngine(DefaultMySQLEngines.Heap)
  case object Merge extends SQLEngine(DefaultMySQLEngines.Merge)
  case object BDB extends SQLEngine(DefaultMySQLEngines.BDB)
  case object BerkleyDB extends SQLEngine(DefaultMySQLEngines.BerkleyDB)
  case object NDBCluster extends SQLEngine(DefaultMySQLEngines.NDBCluster)
  case object NDB extends SQLEngine(DefaultMySQLEngines.NDB)
  case object Example extends SQLEngine(DefaultMySQLEngines.example)
  case object Archive extends SQLEngine(DefaultMySQLEngines.archive)
  case object CSV extends SQLEngine(DefaultMySQLEngines.csv)
  case object Federated extends SQLEngine(DefaultMySQLEngines.federated)
  case object Blackhole extends SQLEngine(DefaultMySQLEngines.blackhole)

}

trait MySQLEngines extends DefaultSQLEngines {
}

/**
 * This bit of magic allows all extending sub-classes to implement the "set" and "and" SQL clauses with all the necessary operators,
 * in a type safe way. By providing the third type argument and a custom way to subclass with the predetermined set of arguments,
 * all DSL representations of an UPDATE query can use the implementation without violating DRY.
 *
 * @tparam T The type of the table owning the record.
 * @tparam R The type of the record held in the table.
 */
class CreateQuery[T <: Table[T, _],
  R,
  Type <: QueryType,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: StatusBind
](val query: Query[T, R, CreateType, Group, Order, Limit, Chain, AssignChain, Status]) {


  final protected def columnDefinitions: List[String] = {
    query.table.columns.foldRight(List.empty[String])((col, acc) => {
      col.qb.queryString :: acc
    })
  }

  final protected def columnSchema[St <: StatusBind]: CreateQuery[T, R, CreateType, Group, Order, Limit, Chain, AssignChain, St] = {

    new CreateQuery[T, R, CreateType, Group, Order, Limit, Chain, AssignChain, St](
      new Query(query.table, query.query.append(columnDefinitions.mkString(", ")), query.rowFunc)
    )
  }

  def ifNotExists: CreateQuery[T, R, CreateType, Group, Order, Limit, Chain, AssignChain, Unterminated] = {
    new CreateQuery[T, R, CreateType, Group, Order, Limit, Chain, AssignChain, Unterminated](
      new Query(query.table, query.table.queryBuilder.ifNotExists(query.query), query.rowFunc)
    )
  }

  def engine(engine: SQLEngine): Query[T, R, CreateType, Group, Order, Limit, Chain, AssignChain, Unterminated] = {
    new Query(query.table,
      query.table.queryBuilder.engine(
        query.query.wrap(columnDefinitions.mkString(", ")),
        engine.value
      ),
      query.rowFunc
    )
  }



  private[morpheus] final def terminate: Query[T, R, CreateType, Group, Order, Limit, Chain, AssignChain, Terminated] = {
    new Query(
      query.table,
      query.query,
      query.rowFunc
    )
  }

}


private[morpheus] trait CreateImplicits extends DefaultSQLEngines {
  /**
   * This defines an implicit conversion from a RootInsertQuery to an InsertQuery, making the INSERT syntax block invisible to the end user.
   * This allows chaining a "value" method call directly after "Table.insert".
   *
   * @param root The RootSelectQuery to convert.
   * @tparam T The table owning the record.
   * @tparam R The record type.
   * @return An executable SelectQuery.
   */
  implicit def rootCreateQueryToCreateQuery[T <: Table[T, _], R](root: AbstractRootCreateQuery[T, R]): CreateQuery[T, R, CreateType, Ungroupped, Unordered,
    Unlimited, Unchainned, AssignUnchainned, Unterminated] = {
    new CreateQuery(
      new Query(
        root.table,
        root.st.default,
        root.rowFunc
      )
    )
  }
}

