import sbt._

class AspectjPluginProject(info: ProjectInfo) extends PluginProject(info) {
  lazy val springMilestones = "Spring Framework Milestone Repository" at "http://maven.springframework.org/milestone"
  lazy val aspectjDep = "org.aspectj" % "aspectjtools" % "1.6.11.M2"
}
