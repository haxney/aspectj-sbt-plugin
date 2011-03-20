package org.haxney.aspectj.sbt

import java.io._
import _root_.sbt.{Logger, LoggerWriter}
import org.aspectj.bridge.{IMessage, MessageHandler}

/**
 *
 * An imitation of the real "com.sun.tools.javac.Main" which passes control over
 * to the AspectJ compiler.
 */
package com.sun.tools.javac {
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
      val aj = new org.aspectj.tools.ajc.Main
      aj.run(args, handler)
      handler.retval
    }
  }
}

class LoggerUnwrapper(w: PrintWriter) extends PrintWriter(w) {
  def getOut = this.out
}

class LoggerMessageHandler(log: Logger) extends MessageHandler {
  override def handleMessage(message: IMessage) = {
    val m = message.getMessage
    message.getKind match {
      case IMessage.WARNING => log.warn(m)
      case IMessage.DEBUG | IMessage.TASKTAG => log.debug(m)
      case IMessage.ERROR | IMessage.ABORT | IMessage.FAIL => log.error(m)
      case IMessage.INFO | IMessage.WEAVEINFO => log.info(m)
    }
    super.handleMessage(message)
  }

  def retval = (numMessages(IMessage.FAIL, true),
                numMessages(IMessage.ERROR, false)) match {
                  case (num, _) if num > 0 => -num
                  case (_, num) if num > 0 => num
                  case _ => 0
                }
}

