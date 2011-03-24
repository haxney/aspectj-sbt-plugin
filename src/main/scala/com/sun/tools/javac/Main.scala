package com.sun.tools.javac

import _root_.java.io.PrintWriter
import _root_.org.aspectj.tools.ajc.{Main => AjcMain}
import _root_.org.aspectj.bridge.{IMessage, MessageHandler}

/**
 *
 * An imitation of the real "com.sun.tools.javac.Main" which passes control over
 * to the AspectJ compiler.
 */

object Main {
  def compile(args: Array[String], writer: PrintWriter) = {
    val writerField = writer.getClass.getDeclaredField("out")
    writerField.setAccessible(true)
    val logWriter = writerField.get(writer)
    val logField = logWriter.getClass.getDeclaredField("delegate")
    logField.setAccessible(true)
    val log = logField.get(logWriter)
    val handler = new LoggerMessageHandler(new LoggerProxy(log))
    val aj = new AjcMain
    aj.run(args, handler)
    retval(handler)
  }

  def retval(handler: MessageHandler) =
    (handler.numMessages(IMessage.FAIL, true),
     handler.numMessages(IMessage.ERROR, false)) match {
       case (num, _) if num > 0 => -num
       case (_, num) if num > 0 => num
       case _ => 0
     }

  class LoggerProxy(m: Object) {
    val cl = m.getClass
    val warnMeth = cl.getMethod("warn", classOf[Function0[String]])
    val debugMeth = cl.getMethod("debug", classOf[Function0[String]])
    val errorMeth = cl.getMethod("error", classOf[Function0[String]])
    val infoMeth = cl.getMethod("info", classOf[Function0[String]])

    def warn(s: => String) = warnMeth.invoke(m, () => s)
    def debug(s: => String) = debugMeth.invoke(m, () => s)
    def error(s: => String) = errorMeth.invoke(m, () => s)
    def info(s: => String) = infoMeth.invoke(m, () => s)
  }

  class LoggerMessageHandler(log: LoggerProxy) extends MessageHandler {
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
  }
}
