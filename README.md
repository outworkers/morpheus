morpheus [NOT PRODUCTION READY][![Build Status](https://travis-ci.org/websudosuk/morpheus.svg)](https://travis-ci.org/websudosuk/morpheus)
========

Named after the Greek God of Dreams, morpheus is an Asynchronous type-safe Scala DSL for MySQL, Postgres, MSSQL, 
MariaDB. We choose this name as it is the dream DSL for any Scala/SQL user, finessed to perfection up to the Websudos quality standard you've gotten 
used to.

We've taken it up ourselves to produce the highest quality database integration tooling for all Scala users, 
currently hitting that bar for Cassandra and MongoDB. But why stop there?


Using morpheus
==============

The current version is: ```val morpheusVersion = 0.1.0```.
Morpheus is published to the Websudos Maven repository at ```http://maven.websudos.co.uk/ext-release-local``` and it's actively and avidly developed. It is 
not yet production ready.

<a id="table-of-contents">Table of contents</a>
===============================================
<ol>
  <li><a href="#design-philosophy">Design philosophy</a></li>
  <li><a href="#copyright">Copyright</a></li>
</ol>


<a id="design-philosophy">Design philosophy</a>
=====================================

You're probably wondering how Morpheus fairs compared to the more established players in the Scala SQL market and why we set out to do something new in the 
first place. To sum it up, we believe Slick is an excellent tool but we do not believe you should learn about our abstractions to get things done. A DSL 
should auto-magically encode the same syntax and the logic as the tool it's designed to "enclose".

Instead of learning about primitives and rules we thought of to abstract away discrepancies between the various SQL implementations, 
morpheus features a unique approach, what we call the auto-magical flip. Although at this point in time only MySQL is supported, 
Morpheus is design to give you an "all-you-can-eat" buffet through a single import.

As follows: ```import com.websudos.morpheus.mysql.Imports._```. And done, you can now define tables, query and so on. Say you have something like this:

```Recipes.select.distinctRow.where(_.name eqs "test")```. ```DISTINCTROW``` doesn't exist in the Postgres ```SELECT``` statement syntax, 
but it's a standard thing as far as MySQL is concerned. Here's how Morpheus operates.

Say you change the top level import to: ```com.websudos.morpheus.postgres.Imports._``` and you try to compile the same ```distinctRow``` query. But there 
will be done. The method will simply not exist. Morpheus has now auto-magically performed a full feature swap, 
changed communication protocol and all underlying settings, and all you get now is Postgres features.

How? Quite a lot of fun magic under the hood, have a look throughout our decently documented codebase for more information. The beauty of it is that you 
don't have to care. Slick makes it easy to move from one SQL database to the other with less code changes, but if you're well set on a database you already 
know and love, it may be counter productive to have to learn about a framework when you could use Morpheus and all you need is IDE auto-completes to get 
lightning fast development productivity. 



<a id="contributors">Contributors</a>
=====================================
<a href="#table-of-contents">back to top</a>

Morpheus was developed at websudos as the foundation of our upcoming book, "Learning Scala by example", which covers all aspects of building an enterprise 
grada Scala framework from scratch.

* Flavian Alexandru @alexflav23(Project lead)
* Benjamin Edwards @benjumanji(Project lead)

<a id="copyright">Copyright</a>
===============================
<a href="#table-of-contents">back to top</a>

Copyright (c) 2014 websudos.


Contributing to morpheus
=======================
<a href="#table-of-contents">back to top</a>

Contributions are most welcome! Don't forget to add your name and GitHub handle to the list of contributors.

<a id="git-flow">Using GitFlow</a>
==================================

To contribute, simply submit a "Pull request" via GitHub.

We use GitFlow as a branching model and SemVer for versioning.

- When you submit a "Pull request" we require all changes to be squashed.
- We never merge more than one commit at a time. All the n commits on your feature branch must be squashed.
- We won't look at the pull request until Travis CI says the tests pass, make sure tests go well.

<a id="style-guidelines">Scala Style Guidelines</a>
===================================================

In spirit, we follow the [Twitter Scala Style Guidelines](http://twitter.github.io/effectivescala/).
We will reject your pull request if it doesn't meet code standards, but we'll happily give you a hand to get it right.

Some of the things that will make us seriously frown:

- Blocking when you don't have to. It just makes our eyes hurt when we see useless blocking.
- Testing should be thread safe and fully async, use ```ParallelTestExecution``` if you want to show off.
- Writing tests should use the pre-existing tools.
- Use the common patterns you already see here, we've done a lot of work to make it easy.
- Don't randomly import stuff. We are very big on alphabetized clean imports.
- Morpheus uses ScalaStyle during Travis CI runs to guarantee you are complying with our guidelines. Since breaking the rules will result in a failed build, 
pleaase take the time to read through the guidelines beforehand.


