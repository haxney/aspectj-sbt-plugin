package org.haxney.aspectj.sbt

import _root_.sbt._
import org.aspectj.tools.ajc.Main
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler

trait AspectJ extends BasicScalaProject {
  lazy val aspectjDep = "org.aspectj" % "aspectjtools" % "1.6.10"
  def javaSrc = info.projectPath / "src" / "main" / "java"
  def aspectPaths = info.projectPath / "src" / "main" / "java"

  lazy val aspectj = aspectjAction describedAs "Run AspectJ"
  def aspectjAction = task {
    val main = new Main()
    val m = new MessageHandler()
    val aspectPath = aspectPaths.absString
    val args: Array[String] = Array("-inpath", javaSrc.absString, "-aspectpath", aspectPath, "-d", outputPath.absolutePath)
    log.info("Running AspectJ")
    main.run(args, m)
    log.info("AspectJ complete")
    None
  }
}
