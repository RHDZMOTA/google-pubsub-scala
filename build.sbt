import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.rhdzmota",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "pubsub-scala",
    libraryDependencies ++= {
      Seq(
        "com.lightbend.akka" %% "akka-stream-alpakka-google-cloud-pub-sub" % "0.19",
        scalaTest % Test
      )
    }
  )
