package org.haxney.aspectj.sbt

import java.io._
import sbt.Logger
import org.aspectj.bridge.{IMessage, MessageHandler}

/**
 *
 * An imitation of the real "com.sun.tools.javac.Main" which passes control over
 * to the AspectJ compiler.
 */
package com.sun.tools.javac {
  object Main {
    def compile(args: Array[String], writer: PrintWriter) = {
      val log = new LoggerUnwrapper(writer).getOut.asInstanceOf[LoggerWriter].delegate
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
  def handleMessage(message: IMessage) = {
    val m = message.getMessage
    message.getKind match {
      case IMessage.WARNING => log.warn(m)
      case IMessage.DEBUG | IMessage.TASKTAG => log.debug(m)
      case IMessage.ERROR | IMessage.ABORT | IMessage.FAIL => log.error(m)
      case IMessage.INFO | IMessage.WEAVEINFO => log.info(m)
    }
    super.handleMessage(m)
  }

  def retval = (numMessages(IMessage.FAIL, true),
                numMessages(IMessage.ERROR, false)) match {
    case (num, _) if num > 0 => -num
    case (_, num) if num > 0 => num
    case _ => 0
  }
}

