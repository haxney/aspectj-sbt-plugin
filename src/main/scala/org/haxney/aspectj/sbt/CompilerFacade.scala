package org.haxney.aspectj.sbt

import java.io._
import _root_.sbt.{Logger, LoggerWriter}
import org.aspectj.bridge.{IMessage, MessageHandler}

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

