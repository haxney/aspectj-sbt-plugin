import sbt._

class AspectjPluginProject(info: ProjectInfo) extends PluginProject(info) {
  lazy val aspectjDep = "org.aspectj" % "aspectjtools" % "1.6.11.M2"
}
