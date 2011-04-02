package org.haxney.aspectj.sbt

import _root_.sbt._
import _root_.xsbt._

trait AspectJ extends BasicScalaProject with FileTasks with MavenStyleScalaPaths {
  // For AspectJ 1.6.11
  lazy val springRelease = "Spring Framework Release Repository" at "http://maven.springframework.org/release"
  lazy val aspectjRt = "org.aspectj" % "aspectjrt" % "1.6.11"

  lazy val facade = "org.haxney.aspectj" %% "aspectj-compiler-facade" % "0.1" % "aspectj"
  lazy val aspectjConf = config("aspectj")

  implicit def sting2CompileOption(opts: Iterable[String]) = opts.map(CompileOption.apply)
  implicit def sting2JavaCompileOption(opts: Iterable[String]) = opts.map(JavaCompileOption.apply)

  def aspectjPaths: PathFinder = configurationClasspath(aspectjConf)

  override def javaCompileOptions = List("-1.5", "-aspectpath", compileClasspath.absString).map(JavaCompileOption.apply) ++ super.javaCompileOptions

  protected def aspectjSetup = {
    import java.net.{URLClassLoader, URL}
    val loader = classOf[Compile].getClassLoader.asInstanceOf[URLClassLoader]
    val addURLMethod = loader.getClass.getDeclaredMethod("addURL", classOf[URL])
    addURLMethod.setAccessible(true)

    def addURL(u: URL) = addURLMethod.invoke(loader, u)
    def toURL(f: java.io.File) = f.toURL

    (aspectjPaths.getFiles + buildScalaInstance.libraryJar) map toURL foreach addURL
  }

  override def compileAction = {
    aspectjSetup
    super.compileAction
  }

  override def compileOrder = CompileOrder.JavaThenScala
}
