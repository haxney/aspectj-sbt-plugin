package org.haxney.aspectj.sbt

import _root_.sbt._
import org.aspectj.tools.ajc.Main
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler

trait AspectJ extends BasicScalaProject with FileTasks with MavenStyleScalaPaths {
  lazy val aspectjTools = "org.aspectj" % "aspectjtools" % "1.6.11.M2" % "aspectj"
  lazy val aspectjRt = "org.aspectj"    % "aspectjrt"    % "1.6.11.M2" % "aspectj"
  lazy val aspectjConf = config("aspectj")
  def aspectjClasspath = configurationClasspath(aspectjConf)

  def aspectPaths: PathFinder = configurationClasspath(aspectjConf)

  def aspectjTask(args: => List[String], output: => Path, srcRoot: => Path, aspects: => PathFinder) = {
    runTask(Some("org.aspectj.tools.ajc.Main"), aspectjClasspath,
            "-sourceroots" :: srcRoot.absolutePath ::
            "-aspectpath" :: aspects.absString ::
            "-d" :: output.absolutePath ::
            args)
  }
  lazy val aspectj = aspectjTask(Nil, outputPath, mainJavaSourcePath, aspectPaths)
}
