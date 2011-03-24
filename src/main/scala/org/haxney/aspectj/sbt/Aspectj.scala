package org.haxney.aspectj.sbt

import _root_.sbt._
import _root_.xsbt._
import org.aspectj.tools.ajc.Main
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler

trait AspectJ extends BasicScalaProject with FileTasks with MavenStyleScalaPaths {
  lazy val aspectjTools = "org.aspectj" % "aspectjtools" % "1.6.11"
  lazy val aspectjConf = config("aspectj")

  implicit def sting2CompileOption(opts: Iterable[String]) = opts.map(CompileOption.apply)
  implicit def sting2JavaCompileOption(opts: Iterable[String]) = opts.map(JavaCompileOption.apply)

  def aspectjPaths: PathFinder = configurationClasspath(aspectjConf)
  def aspectjCompilerJar = aspectjPaths.getFiles.find(_.getName.contains("aspectj-compiler-facade")).get

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
