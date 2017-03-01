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
 * - Explicit consent must be obtained from the copyright owner, Websudos Limited before any redistribution is made.
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
package com.outworkers.morpheus.engine.query.parts

import com.outworkers.morpheus.builder.SQLBuiltQuery

abstract class QueryPart[T <: QueryPart[T]](val list: List[SQLBuiltQuery] = Nil) {

  def instance(l: List[SQLBuiltQuery]): T

  def nonEmpty: Boolean = list.nonEmpty

  def qb: SQLBuiltQuery

  def build(init: SQLBuiltQuery): SQLBuiltQuery = if (init.nonEmpty) {
    qb.bpad.prepend(init)
  } else {
    qb.prepend(init)
  }

  def append(q: SQLBuiltQuery): T = instance(list ::: (q :: Nil))

  def append(q: SQLBuiltQuery*): T = instance(q.toList ::: list)

  def append(q: List[SQLBuiltQuery]): T = instance(q ::: list)

  def mergeList(list: List[SQLBuiltQuery]): MergedQueryList

  def merge[X <: QueryPart[X]](part: X): MergedQueryList = {
    val list = if (part.qb.nonEmpty) List(qb, part.qb) else List(qb)

    mergeList(list)
  }
}


abstract class MergedQueryList(val list: List[SQLBuiltQuery]) {

  def this(query: SQLBuiltQuery) = this(List(query))

  def apply(list: List[SQLBuiltQuery]): MergedQueryList

  def apply(str: String): SQLBuiltQuery

  def build: SQLBuiltQuery = apply(list.map(_.queryString).mkString(" "))

  /**
    * This will build a merge list into a final executable query.
    * It will also prepend the CQL query passed as a parameter to the final string.
    *
    * If the current list has only empty queries to merge, the init string is return instead.
    * Alternatively, the init string is prepended after a single space.
    *
    * @param init The initialisation query of the part merge.
    * @return A final, executable CQL query with all the parts merged.
    */
  def build(init: SQLBuiltQuery): SQLBuiltQuery = if (list.exists(_.nonEmpty)) {
    build.bpad.prepend(init.queryString)
  } else {
    init
  }

  def merge[X <: QueryPart[X]](part: X, init: SQLBuiltQuery = apply("")): MergedQueryList = {
    val appendable = part build init

    if (appendable.nonEmpty) {
      apply(list ::: List(appendable))
    } else {
      this
    }
  }
}