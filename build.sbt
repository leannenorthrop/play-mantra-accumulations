name := """mantra-accumulations"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

//skip in update := true
offline := true

libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "3.0.0",
  "org.webjars" %% "webjars-play" % "2.4.0",
  "net.codingwell" %% "scala-guice" % "4.0.0",
  "net.ceedubs" %% "ficus" % "1.1.2",
  "com.adrianhurt" %% "play-bootstrap3" % "0.4.4-P24",
  "com.mohiva" %% "play-silhouette-testkit" % "3.0.0" % "test",
  "com.typesafe.play" %% "play-slick" % "1.0.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.0.1",
  "org.postgresql"     %  "postgresql" % "9.4-1201-jdbc41",
  filters,
  cache,
  ws,
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalatestplus" %% "play" % "1.4.0-M3" % "test",
  "org.hsqldb" % "hsqldb" % "2.3.3" % "test",
  "org.dbunit" % "dbunit" % "2.5.1" % "test",
  "com.h2database" % "h2" % "1.4.186" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test"
)

routesGenerator := InjectedRoutesGenerator

resolvers := ("Atlassian Releases" at "https://maven.atlassian.com/public/") +: resolvers.value

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

resolvers += Resolver.url("fix-sbt-plugin-releases", url("https://dl.bintray.com/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

resolvers += Resolver.url("fix-sbt-plugin-releases2", url("http://dl.bintray.com/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"

resolvers += "Typesafe Ivy Releases" at "http://repo.typesafe.com/typesafe/ivy-releases"

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen" // Warn when numerics are widened.
)

net.virtualvoid.sbt.graph.Plugin.graphSettings

ScoverageSbtPlugin.ScoverageKeys.coverageMinimum := 95

ScoverageSbtPlugin.ScoverageKeys.coverageFailOnMinimum := false

ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := {
    if(scalaBinaryVersion.value == "2.11") true
    else false
}

//Exclude template classes from coverage as template didn't include tests and not concentrating on auth at present
ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := ".*DBTableDefinitions*.*;.*Routes*.*;.*Reverse*.*;.*Users.*;.*Infos.*;.*OAuth*.*;.*Open*.*;.*Filters.*;.*ErrorHandler.*;.*SilhouetteModule.*;.*UserService*.*;.*OAuth*DAO.*;.*OpenIDInfoDAO.*;.*PasswordInfoDAO.*;.*UserDAO.*;.*SignIn.*;.*SignUp.*;.*ApplicationController.*;.*CredentialsAuthController.*;.*SocialAuthController.*;"

javaOptions in Test += "-Dconfig.resource=test.conf"

scalariformSettings
