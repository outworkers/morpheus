
import sbt.Keys._
import sbt._
import scoverage.ScoverageSbtPlugin.instrumentSettings
import org.scoverage.coveralls.CoverallsPlugin.coverallsSettings
import org.scalastyle.sbt.ScalastylePlugin

object MorpheusBuild extends Build {

  val UtilVersion = "0.5.0"
  val scalatestVersion = "2.2.0-M1"
  val FinagleVersion = "6.24.0"
  val SparkVersion = "1.2.1"
  val ShapelessVersion = "2.1.0"

  val publishUrl = "http://maven.websudos.co.uk"

  val mavenPublishSettings : Seq[Def.Setting[_]] = Seq(
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishMavenStyle := true,
    publishTo <<= version.apply {
      v =>
        val nexus = "https://oss.sonatype.org/"
        if (v.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => true },
    pomExtra :=
      <url>https://github.com/websudosuk/morpheus</url>
        <licenses>
          <license>
            <name>Apache License, Version 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:websudosuk/morpheus.git</url>
          <connection>scm:git:git@github.com:websudosuk/morpheus.git</connection>
        </scm>
        <developers>
          <developer>
            <id>benjumanji</id>
            <name>Benjamin Edwards</name>
            <url>http://github.com/benjumanji</url>
          </developer>
          <developer>
            <id>alexflav</id>
            <name>Flavian Alexandru</name>
            <url>http://github.com/alexflav23</url>
          </developer>
        </developers>
  )

  val publishSettings : Seq[Def.Setting[_]] = Seq(
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishTo <<= version { (v: String) => {
        if (v.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at publishUrl + "/ext-snapshot-local")
        else
          Some("releases"  at publishUrl + "/ext-release-local")
      }
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => true }
  )

  def liftVersion(scalaVersion: String) = {
    scalaVersion match {
      case "2.10.4" => "3.0-M1"
      case _ => "3.0-M2"
    }
  }

  def shapelessVersion(scalaVersion: String) = {
    scalaVersion match {
      case "2.10.4" =>
    }
  }

  val sharedSettings: Seq[Def.Setting[_]] = Seq(
    organization := "com.websudos",
    version := "0.1.2",
    scalaVersion := "2.10.4",
    crossScalaVersions := Seq("2.10.4", "2.11.4"),
    resolvers ++= Seq(
      "Typesafe repository snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
      "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
      "Sonatype repo"                    at "https://oss.sonatype.org/content/groups/scala-tools/",
      "Sonatype releases"                at "https://oss.sonatype.org/content/repositories/releases",
      "Sonatype snapshots"               at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype staging"                 at "http://oss.sonatype.org/content/repositories/staging",
      "Java.net Maven2 Repository"       at "http://download.java.net/maven/2/",
      "Twitter Repository"               at "http://maven.twttr.com",
      "Websudos releases"                at "http://maven.websudos.co.uk/ext-release-local",
      "Websudos snapshots"               at "http://maven.websudos.co.uk/ext-snapshot-local"
    ),
    scalacOptions ++= Seq(
      "-language:postfixOps",
      "-language:implicitConversions",
      "-language:reflectiveCalls",
      "-language:higherKinds",
      "-language:existentials",
      "-Yinline-warnings",
      "-Xlint",
      "-deprecation",
      "-feature",
      "-unchecked"
     ),
    fork in Test := true,
    javaOptions in Test ++= Seq("-Xmx2G")
  ) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings ++ instrumentSettings ++ publishSettings ++ ScalastylePlugin.Settings

  lazy val morpheus = Project(
    id = "morpheus",
    base = file("."),
    settings = Defaults.coreDefaultSettings ++ sharedSettings ++ coverallsSettings
  ).settings(
    name := "morpheus"
  ).aggregate(
    morpheusDsl,
    morpheusMySQL,
    morpheusPostgres,
    morpheusSpark,
    morpheusTestkit,
    morpheusZookeeper
  )

  lazy val morpheusDsl = Project(
    id = "morpheus-dsl",
    base = file("morpheus-dsl"),
    settings = Defaults.coreDefaultSettings ++
      sharedSettings ++
      publishSettings
  ).settings(
    name := "morpheus-dsl",
    libraryDependencies ++= Seq(
      "com.chuusai"                  % "shapeless"                         % ShapelessVersion,
      "org.scala-lang"               % "scala-reflect"                     % scalaVersion.value,
      "joda-time"                    % "joda-time"                         % "2.3",
      "org.joda"                     % "joda-convert"                      % "1.6",
      "net.liftweb"                  %% "lift-json"                        % liftVersion(scalaVersion.value)                 % "test, provided"
    )
  ).dependsOn(
    morpheusTestkit % "test, provided"
  )

  lazy val morpheusMySQL = Project(
    id = "morpheus-mysql",
    base = file("morpheus-mysql"),
    settings = Defaults.coreDefaultSettings ++
      sharedSettings ++ publishSettings
  ).settings(
    name := "morpheus-mysql",
    libraryDependencies ++= Seq(
      "com.twitter"                  %% "finagle-mysql"                     % FinagleVersion
    )
  ).dependsOn(
    morpheusDsl,
      morpheusTestkit % "test, provided"
  )

  lazy val morpheusPostgres = Project(
    id = "morpheus-postgres",
    base = file("morpheus-postgres"),
    settings = Defaults.coreDefaultSettings ++
      sharedSettings ++ publishSettings
  ).settings(
    name := "morpheus-postgres",
    libraryDependencies ++= Seq(
      "com.github.mauricio" %% "postgresql-async" % "0.2.15"
    )
  ).dependsOn(
    morpheusDsl,
    morpheusTestkit % "test, provided"
  )


  lazy val morpheusZookeeper = Project(
    id = "morpheus-zookeeper",
    base = file("morpheus-zookeeper"),
    settings = Defaults.coreDefaultSettings ++ sharedSettings
  ).settings(
    name := "morpheus-zookeeper",
    libraryDependencies ++= Seq(
      "com.twitter"                  %% "finagle-zookeeper"                 % FinagleVersion,
      "com.websudos"                 %% "util-zookeeper"                    % UtilVersion
    )
  )

  lazy val morpheusSpark = Project(
    id = "morpheus-spark",
    base = file("morpheus-spark"),
    settings = Defaults.coreDefaultSettings ++ sharedSettings
  ).settings(
    name := "morpheus-spark",
    libraryDependencies ++= Seq(
      "org.apache.spark"             %% "spark-sql"                    % SparkVersion
    )
  ).dependsOn(
    morpheusTestkit
  )

  lazy val morpheusTestkit = Project(
    id = "morpheus-testkit",
    base = file("morpheus-testkit"),
    settings = Defaults.coreDefaultSettings ++ sharedSettings
  ).settings(
    name := "morpheus-testkit",
    libraryDependencies ++= Seq(
      "com.h2database"                   % "h2"                        % "1.4.181",
      "com.websudos"                     %% "util-testing"             % UtilVersion,
      "org.scalacheck"                   %% "scalacheck"               % "1.11.3"
    )
  ).dependsOn(
    morpheusZookeeper
  )
}
