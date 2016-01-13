package org.gossip.network.actors

import java.nio.ByteBuffer
import akka.actor.{ Props, Actor, ActorLogging, ActorRef }
import akka.io.{ IO, Tcp }
import akka.util.ByteString
import java.net.InetSocketAddress
import scala.collection.mutable.Set
import java.util.concurrent.atomic.AtomicLong
import akka.util.ByteStringBuilder
import java.nio.ByteOrder

/**
 * Support object to build @WorkerActor
 */
final object WorkerSystem {
  def props(handler: WorkerHandler): Props = Props.create(classOf[WorkerActor], handler)
  
  private final val BYTE_ORDER = ByteOrder.BIG_ENDIAN;
  private final val BYTE_0 = '0'.toByte
  private final val BYTE_1 = '1'.toByte

  private val counter: AtomicLong = new AtomicLong(0)
  private val version: AtomicLong = new AtomicLong(0)
  private val lastUpdateTimeStamp : AtomicLong = new AtomicLong(System.currentTimeMillis())
  private val hostList: Set[InetSocketAddress] = Set()
  
  private def add (remote: InetSocketAddress) {
    if(hostList add remote) {
      version.incrementAndGet()
      lastUpdateTimeStamp.set(System.currentTimeMillis())
    }
  }

  def nextHost = {
    hostList.toList.lift((counter.getAndIncrement%hostList.size).toInt)
  }
  
  def remove (remote: InetSocketAddress) {
    if(hostList remove remote) {
      version.incrementAndGet()
      lastUpdateTimeStamp.set(System.currentTimeMillis())
    }
  }
  
  /**
   * Primary class to process all requests. Two scenario in which the actor is invoked.
   * 1. Server receive a connection request and worker actor is used to process the request
   * 2. External worker actor is invoked to connect to server
   *
   * The class functionality is extended with @WorkerHandler concrete class.
   *
   */
  final private class WorkerActor(handler: WorkerHandler) extends Actor with ActorLogging {

    log.info(s"new workerActor $this")
    import akka.io.Tcp._
    import context.system

    /**
     * Implement partial function receive.
     *
     *
     */
    final def receive = {
      case connect: Connect =>
        log.info(s"WorkerActor Requested connection to seed " + connect)
        IO(Tcp) ! connect
      case commandFailed @ CommandFailed(cmd) =>
        log.info(s"WorkerActor Command failed $commandFailed")
        log.info(s"original cmd $cmd")
        cmd match {
          case c: Connect => remove(c.remoteAddress)
        }
        context stop self
      case received: Received =>
        log.info(s"WorkerActor received message $received")
        handleReceive(received)
      case connected @ Connected(remote, local) =>
        log.info(s"WorkerActor successfully connected $connected")
        add(remote)
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
        
        connection ! Write(metaDataRequest, NoAck)
      case Closed =>
        log.info(s"WorkerActor connection closed.")
        context stop self
      case de @ _ =>
        log.info(s"WorkerActor default case $de")
//        val connection = sender()
//        handler.handleStorageMessage(de)
    }

    private def metaDataRequest = {
      val firstMsg = handler.requestMetaData
      val builder = new ByteStringBuilder
      builder.putByte(BYTE_0)
      builder.putLong(lastUpdateTimeStamp.longValue())(BYTE_ORDER)
      builder.putInt(version.intValue)(BYTE_ORDER)
      if(firstMsg != null) builder.append(ByteString(firstMsg))
      builder.result()
    }
    
    /**
     * Common functionality extracted out.
     * All received messages are handled here.
     * If any data is returned back, it will be passed back to the sender
     * else a close message will be send.
     *
     */
    private def handleReceive(received: Received) = {
      val incomingData: ByteString = received.data
      val firstByte = incomingData.iterator.getByte
      firstByte match {
        case BYTE_0 =>
          val (t, v) = (incomingData.iterator.getLong(BYTE_ORDER), incomingData.iterator.getInt(BYTE_ORDER))
          if(lastUpdateTimeStamp.longValue() <= t && version.intValue() != v ) {
            //Handle versions for hosts
          }
        case BYTE_1 =>
          val hostCount = incomingData.iterator.getInt(BYTE_ORDER)
          val h = for (i <- (0 to hostCount)) yield {
            incomingData.iterator.getInt(BYTE_ORDER)
          }
          h.toSet
          //merge changes
        case _ =>
      }
      val returnMsg = handler.requestDelta(received.data.toByteBuffer)
      val connection = sender()
      if (returnMsg != null) {
        connection ! Write(ByteString(returnMsg), NoAck)
      } else {
        connection ! Close
      }
    }
  }
}

