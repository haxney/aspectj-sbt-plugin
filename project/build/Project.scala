import sbt._

class AspectjPluginProject(info: ProjectInfo) extends PluginProject(info) {
  lazy val springRelease = "Spring Framework Release Repository" at "http://maven.springframework.org/release"
  lazy val aspectjDep = "org.aspectj" % "aspectjtools" % "1.6.11"
}
