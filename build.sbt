name := "play-elasticsearch-plugin"

version := "1.0.0-SNAPSHOT"

organization := "de.micromata"

resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Spring releases" at "http://repo.springsource.org/release",
  "Sprin milestones" at "http://repo.spring.io/milestone",
  "Local Maven" at "file://" + Path.userHome.absolutePath + "/.m2/repository"
)

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
   "com.typesafe.play" %% "play" % "2.2.3",
   "com.typesafe.play" %% "play-java" % "2.2.3",
    "org.springframework.data" % "spring-data-elasticsearch" % "1.0.0.RELEASE",
    "javax.enterprise" % "cdi-api" % "1.0"
)

publishTo <<= version {
  case v if v.trim.endsWith("SNAPSHOT") => Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
  case _ => Some(Resolver.file("Github Pages",  new File("../micromata.github.io/repo")))
}
