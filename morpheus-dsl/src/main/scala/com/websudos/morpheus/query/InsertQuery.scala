package com.websudos.morpheus.query

import com.websudos.morpheus.dsl.Table
import com.websudos.morpheus.SQLPrimitive
import com.websudos.morpheus.column.AbstractColumn

class InsertQuery[
  T <: Table[T, _],
  R,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind
  ](val query: Query[T, R, Group, Order, Limit, Chain, AssignChain]) {

}
