morpheus [NOT PRODUCTION READY][![Build Status](https://travis-ci.org/websudosuk/morpheus.svg)](https://travis-ci.org/websudosuk/morpheus)
========

Named after the Greek God of Dreams, morpheus is an Asynchronous type-safe Scala DSL for MySQL, Postgres, MSSQL, 
MariaDB. We choose this name as it is the dream DSL for any Scala/SQL user, finessed to perfection up to the Websudos quality standard you've gotten 
used to.

We've taken it up ourselves to produce the highest quality database integration tooling for all Scala users, 
currently hitting that bar for Cassandra and MongoDB. But why stop there?


Using morpheus
==============

The current version is: ```val moprheusVersion = 0.1.0```.
Morpheus is published to the Websudos Maven repository at ```http://maven.websudos.co.uk/ext-release-local``` and it's actively and avidly developed. It is 
not yet production ready.

<a id="table-of-contents">Table of contents</a>
===============================================
<ol>
  <li><a href="#copyright">Copyright</a></li>
</ol>


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


