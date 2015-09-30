package org.gossip.network.actors

import java.nio.ByteBuffer
import akka.actor.{ Props, Actor, ActorLogging, ActorRef }
import akka.io.{ IO, Tcp }
import akka.util.ByteString

final object WorkerSystem {
  def props(worker: Class[_ <: WorkerActor]): Props = Props.create(worker)
}

abstract class WorkerActor extends Actor with ActorLogging {

  import akka.io.Tcp._
  import context.system

  final def receive = {
    case connect: Connect =>
      log.info(s"WorkerActor Requested connection to seed " + connect)
      IO(Tcp) ! connect
    case commandFailed @ CommandFailed(_: Connect) =>
      log.info(s"WorkerActor Command failed $commandFailed")
      context stop self
    case received: Received =>
      log.info(s"WorkerActor received message $received")
      handleReceive(received)
    case connected @ Connected(remote, local) =>
      log.info(s"WorkerActor successfully connected $connected")
      val connection = sender()
      connection ! Register(self)
      context become {
        case write: Write =>
          log.info(s"WorkerActor-CONTEXT writing message $write to $connection")
          connection ! write
        case "close" =>
          log.info(s"WorkerActor-CONTEXT closing connection as message receive close")
          connection ! Close
          context stop self
        case _: ConnectionClosed =>
          log.info(s"WorkerActor-CONTEXT closing connection")
          context stop self
        case received: Received =>
          log.info(s"WorkerActor-CONTEXT received message $received")
          handleReceive(received)
        case _ =>
          log.info(s"WorkerActor-CONTEXT default case")
      }
      log.info(s"WorkerActor wiriting back initial message after connection.")
      connection ! Write(ByteString(firstMessage), NoAck)
    case Closed =>
      log.info(s"WorkerActor connection closed.")
      context stop self
    case de @ _ =>
      log.info(s"WorkerActor default case $de")
      handleStorageMessage(de)
  }

  private def handleReceive(received: Received) = {
    val returnMsg = handleRemoteMessage(received.data.toByteBuffer)
    val connection = sender()
    if (returnMsg != null) {
      connection ! Write(ByteString(returnMsg), NoAck)
    } else {
      connection ! Close
    }
  }

  def handleRemoteMessage(data: ByteBuffer): ByteBuffer
  
  def handleStorageMessage(data: Any) : ByteBuffer
  
  def firstMessage : ByteBuffer 
  
}