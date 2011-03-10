package org.haxney.aspectj.sbt

import _root_.sbt._
import org.aspectj.tools.ajc.Main
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import java.io.File

trait AspectJ extends BasicScalaProject with FileTasks with MavenStyleScalaPaths {
  lazy val aspectjTools = "org.aspectj" % "aspectjtools" % "1.6.11.M2" % "aspectj"
  lazy val aspectjRt = "org.aspectj"    % "aspectjrt"    % "1.6.11.M2" % "aspectj"
  lazy val aspectjConf = config("aspectj")

  def aspectjCompileDescription = "Run AspectJ"
  def aspectjClasspath: PathFinder = configurationClasspath(aspectjConf)
  def aspectjArgs = List("-1.5")
  def aspectjSourceRoot = mainJavaSourcePath
  def aspectjSourcesRel = (aspectjSourceRoot ##) ** "*.java"
  def aspectjSources = aspectjSourceRoot ** "*.java"
  def aspectjAspects = aspectjClasspath
  def aspectjOutputs = {
    val newPaths = aspectjSourcesRel.getRelativePaths.map { p =>
      new File(mainCompilePath.asFile, "\\.java$".r.replaceFirstIn(p, ".class"))
    }
    Path.fromFiles(newPaths)
  }
  override def mainSourceRoots = super.mainSourceRoots --- mainJavaSourcePath

  protected def aspectjAction = fileTask(aspectjOutputs from aspectjSources) {
    runTask(Some("org.aspectj.tools.ajc.Main"), aspectjClasspath,
            "-sourceroots" :: mainJavaSourcePath.absString ::
            "-aspectpath" :: aspectjAspects.absString ::
            "-d" :: mainCompilePath.absolutePath ::
            aspectjArgs).run
  } describedAs aspectjCompileDescription

  lazy val aspectj = aspectjAction
}
