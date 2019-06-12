/*
 * Copyright 2013 - 2019 Outworkers Ltd.
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
import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport.{ReleaseStep, _}
import sbtrelease.Vcs
import scala.util.Properties

object Publishing {

  lazy val noPublishSettings = Seq(
    publish := (),
    publishLocal := (),
    publishArtifact := false
  )

  val ciSkipSequence = "[ci skip]"

  private def toProcessLogger(st: State): ProcessLogger = new ProcessLogger {
    override def error(s: => String): Unit = st.log.error(s)
    override def info(s: => String): Unit = st.log.info(s)
    override def buffer[T](f: => T): T = st.log.buffer(f)
  }

  def vcs(state: State): Vcs = {
    Project.extract(state).get(releaseVcs)
      .getOrElse(sys.error("Aborting release. Working directory is not a repository of a recognized VCS."))
  }

  val releaseTutFolder = settingKey[File]("The file to write the version to")
  val releaseTutCommit = taskKey[String]("Commit message for the tut commit")

  def commitTutFilesAndVersion: ReleaseStep = ReleaseStep { st: State =>
    val settings = Project.extract(st)
    val logger = ConsoleLogger()
    logger.info(s"Found modified files: ${vcs(st).hasModifiedFiles}")

    val log = toProcessLogger(st)
    val versionsFile = settings.get(releaseVersionFile).getCanonicalFile
    val docsFolder = settings.get(releaseTutFolder).getCanonicalFile

    logger.info(s"Docs folder path: Path: ${docsFolder.getPath}; Absolute path: ${docsFolder.getAbsolutePath}")
    val base = vcs(st).baseDir.getCanonicalFile
    val sign = settings.get(releaseVcsSign)

    val versionPath = IO.relativize(
      base,
      versionsFile
    ).getOrElse("Version file [%s] is outside of this VCS repository with base directory [%s]!" format(versionsFile, base))

    val commitablePaths = Seq(versionPath) ++ {
      if (docsFolder.exists) {
        logger.info(s"Docs folder exists under $docsFolder")
        val relativeDocsPath = IO.relativize(
          base,
          docsFolder
        ).getOrElse("Docs folder [%s] is outside of this VCS repository with base directory [%s]!" format(docsFolder, base))
        Seq(relativeDocsPath)
      } else {
        logger.info(s"Docs folder doesn't exist under, $base and $docsFolder")
        Seq.empty
      }
    }

    vcs(st).add(commitablePaths: _*) !! log
    val status = (vcs(st).status !!) trim

    val newState = if (status.nonEmpty) {
      val (state, msg) = settings.runTask(releaseCommitMessage, st)
      val x = vcs(state).commit(msg, sign)

      state
    } else {
      // nothing to commit. this happens if the version.sbt file hasn't changed or no docs have been added.
      st
    }
    vcs(newState).status !! log

    newState
  }

  lazy val defaultCredentials: Seq[Credentials] = {
    if (!Publishing.runningUnderCi) {
      Seq(
        Credentials(Path.userHome / ".bintray" / ".credentials"),
        Credentials(Path.userHome / ".ivy2" / ".credentials")
      )
    } else {
      Seq(
        Credentials(
          realm = "Bintray",
          host = "dl.bintray.com",
          userName = System.getenv("bintray_user"),
          passwd = System.getenv("bintray_password")
        ),
        Credentials(
          realm = "Sonatype OSS Repository Manager",
          host = "oss.sonatype.org",
          userName = System.getenv("maven_user"),
          passwd = System.getenv("maven_password")
        ),
        Credentials(
          realm = "Bintray API Realm",
          host = "api.bintray.com",
          userName = System.getenv("bintray_user"),
          passwd = System.getenv("bintray_password")
        )
      )
    }
  }

  def publishToMaven: Boolean = sys.env.get("MAVEN_PUBLISH").exists("true" ==)

  lazy val pgpPass: Option[Array[Char]] = Properties.envOrNone("pgp_passphrase")
    .orElse(Properties.envOrNone("PGP_PASSPHRASE")).map(_.toCharArray)

  lazy val mavenSettings: Seq[Def.Setting[_]] = Seq(
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishMavenStyle := true,
    licenses += ("Apache-2.0", url("https://github.com/outworkers/phantom/blob/develop/LICENSE.txt")),
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.trim.endsWith("SNAPSHOT")) {
        Some("snapshots" at nexus + "content/repositories/snapshots")
      } else {
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
      }
    },
    externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = true),
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => true },
    pomExtra :=
      <url>https://github.com/outworkers/morpheus</url>
        <scm>
          <url>git@github.com:outworkers/morpheus.git</url>
          <connection>scm:git:git@github.com:outworkers/morpheus.git</connection>
        </scm>
        <developers>
          <developer>
            <id>alexflav</id>
            <name>Flavian Alexandru</name>
            <url>http://github.com/alexflav23</url>
          </developer>
        </developers>
  )

  def effectiveSettings: Seq[Def.Setting[_]] = mavenSettings

  def runningUnderCi: Boolean = sys.env.get("CI").isDefined || sys.env.get("TRAVIS").isDefined
  def travisScala211: Boolean = sys.env.get("TRAVIS_SCALA_VERSION").exists(_.contains("2.11"))

  def isTravisScala210: Boolean = !travisScala211

  def isJdk8: Boolean = sys.props("java.specification.version") == "1.8"

  lazy val addOnCondition: (Boolean, ProjectReference) => Seq[ProjectReference] = (bool, ref) =>
    if (bool) ref :: Nil else Nil

  lazy val addRef: (Boolean, ClasspathDep[ProjectReference]) => Seq[ClasspathDep[ProjectReference]] = (bool, ref) =>
    if (bool) Seq(ref) else Seq.empty

}
