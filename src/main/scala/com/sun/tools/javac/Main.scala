package com.sun.tools.javac

import _root_.java.io.PrintWriter
import _root_.sbt.{Logger, LoggerWriter}
import _root_.org.haxney.aspectj.sbt.LoggerMessageHandler
import _root_.org.aspectj.tools.ajc.{Main => AjcMain}

/**
 *
 * An imitation of the real "com.sun.tools.javac.Main" which passes control over
 * to the AspectJ compiler.
 */

object Main {
  def compile(args: Array[String], writer: PrintWriter) = {
    // Hack around the access restrictions to get the underlying Logger
    val writerField = writer.getClass.getDeclaredField("out")
    writerField.setAccessible(true)
    val logWriter = writerField.get(writer)
    val logField = logWriter.getClass.getDeclaredField("delegate")
    logField.setAccessible(true)
    val log = logField.get(logWriter).asInstanceOf[Logger]
    val handler = new LoggerMessageHandler(log)
    val aj = new AjcMain
    aj.run(args, handler)
    handler.retval
  }
}
