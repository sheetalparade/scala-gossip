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
  private final val BYTE_2 = '2'.toByte
  private final val BYTE_3 = '3'.toByte
  private final val BYTE_4 = '4'.toByte
  private final val BYTE_5 = '5'.toByte
  private final val BYTE_6 = '6'.toByte

  def nextHost = WorkerData.nextHost
  
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
          case c: Connect => WorkerData.remove(c.remoteAddress)
        }
        context stop self
      case received: Received =>
        log.info(s"WorkerActor received message $received")
        handleReceive(received)
      case connected @ Connected(remote, local) =>
        log.info(s"WorkerActor successfully connected $connected")
        WorkerData.add(remote)
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
      builder ++= WorkerData.getMetaData
      builder.putByte(BYTE_2)
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
      val incomingData = received.data.iterator
      val firstByte = incomingData.getByte
      val (t, v) = (incomingData.getLong(BYTE_ORDER), incomingData.getInt(BYTE_ORDER))
      val returnMsg = firstByte match {
        case BYTE_0 =>
          val hostData = WorkerData.compareVersion(t, v)
          incomingData.getByte
          val builder = new ByteStringBuilder
//          if(hostData.size == 0) {
//            builder.putByte(BYTE_3)
//            builder.putInt(hostData.size)(BYTE_ORDER) ++= hostData
//          } else {
//            builder.putByte(BYTE_4)
//          }
          builder ++= hostData
          builder ++= ByteString(handler.requestDelta(incomingData.toByteString.asByteBuffer))
          builder.result()
        case BYTE_1 =>
          val builder = new ByteStringBuilder
          val dataType = incomingData.getByte
          val hostData = dataType match {
            case BYTE_2 =>
              builder.putByte(BYTE_3)
              val d = WorkerData.toByteString
              builder.putInt(d.size)(BYTE_ORDER)
              builder ++= d
            case BYTE_3 =>
              val hostDataCount = incomingData.getInt(BYTE_ORDER)
              builder ++= WorkerData.merge(incomingData.take(hostDataCount).toByteString)
            case BYTE_4 =>
              
          }
          val appData = handler.merge(incomingData.toByteString.asByteBuffer)
          val partialData = if(builder.length > 0){
            builder.result()
          } else if(appData != null){
            builder.putByte(BYTE_4)
            builder.result()
          }else {
            builder.result()
          }
          if(appData != null) {
            partialData ++ ByteString(appData)
          } else {
            partialData
          }
        case _ =>
          ByteString()
      }
      val connection = sender()
      if (returnMsg.size != 0) {
        connection ! Write(ByteString(BYTE_1) ++ WorkerData.getMetaData ++ returnMsg, NoAck)
      } else {
        connection ! Close
      }
    }
  }
  
  private object WorkerData {
    private val counter: AtomicLong = new AtomicLong(0)
    private val version: AtomicLong = new AtomicLong(0)
    private val lastUpdateTimeStamp : AtomicLong = new AtomicLong(System.currentTimeMillis())
    private val hostList: Set[InetSocketAddress] = Set()
    private val removedList: Set[InetSocketAddress] = Set()
    
    def add (remote: InetSocketAddress) {
      synchronized {
        val hostListChange = hostList add remote
        val removeListChange = removedList remove remote
        if(hostListChange || removeListChange) {
          version.incrementAndGet()
          lastUpdateTimeStamp.set(System.currentTimeMillis())
        }
      }
    }
  
    def nextHost = {
      hostList.toList.lift((counter.getAndIncrement%hostList.size).toInt)
    }
    
    def remove (remote: InetSocketAddress) {
      synchronized {
        val hostListChange = hostList remove remote
        val removeListChange = removedList add remote
  
        if(hostListChange || removeListChange) {
          version.incrementAndGet()
          lastUpdateTimeStamp.set(System.currentTimeMillis())
        }
      }
    }
    
    def getMetaData = {
      val builder = new ByteStringBuilder
      builder.putLong(lastUpdateTimeStamp.longValue())(BYTE_ORDER)
      builder.putInt(version.intValue)(BYTE_ORDER)
      builder.result()
    }
    
    def compareVersion(remoteTS: Long, remoteVersion: Long) = {
      val builder = new ByteStringBuilder
      if(remoteTS < lastUpdateTimeStamp.get && remoteVersion < version.get) {
        val d = toByteString
        builder.putByte(BYTE_3)
        builder.putInt(d.size)(BYTE_ORDER)
        builder ++= toByteString
      } else if (remoteTS > lastUpdateTimeStamp.get && remoteVersion > version.get){
        builder.putByte(BYTE_2)
      } else {
        builder.putByte(BYTE_4)
      }
      builder.result()
    }
    
    def toByteString = {
      val builder = new ByteStringBuilder
      builder.putLong(lastUpdateTimeStamp.get)(BYTE_ORDER)
      builder.putLong(version.get)(BYTE_ORDER)
      builder.putInt(hostList.size)(BYTE_ORDER)
      hostList.map(x => {
        val host = x.getHostString
       (host.size, host, x.getPort)
      }).foreach ( x =>  {
        builder.putInt(x._1)(BYTE_ORDER)
        builder.putBytes(x._2.getBytes)
        builder.putInt(x._3)(BYTE_ORDER)
      })
      builder.putInt(removedList.size)(BYTE_ORDER)
      removedList.map(x => {
       val host = x.getHostString
       (host.size, host, x.getPort)
      }).foreach ( x =>  {
        builder.putInt(x._1)(BYTE_ORDER)
        builder.putBytes(x._2.getBytes)
        builder.putInt(x._3)(BYTE_ORDER)
      })
      builder.result()
    }
    
    def merge(incomingData: ByteString) : ByteString= {
      
      val builder = new ByteStringBuilder
      if(incomingData.size > 0) {
        val iter = incomingData.iterator
        val remoteTS = iter.getLong(BYTE_ORDER)
        val remoteVersion = iter.getLong(BYTE_ORDER)
        if(remoteTS < lastUpdateTimeStamp.get && remoteVersion < version.get) {
          builder ++= toByteString
        } else {
          val hostListSize = iter.getInt(BYTE_ORDER)
          val remoteHostList = (0 to hostListSize).map(x => {
            val hostNameSize = iter.getInt(BYTE_ORDER)
            val hostNameArray : Array[Byte] = Array.ofDim[Byte](hostNameSize)
            iter.copyToArray(hostNameArray)
            val hostName = hostNameArray.toString()
            val port = iter.getInt(BYTE_ORDER)
            (hostName, port)
          }).toList.map(x => new InetSocketAddress(x._1, x._2))
          val removeListSize = iter.getInt(BYTE_ORDER)
          val remoteRemoveList = (0 to removeListSize).map(x => {
            val hostNameSize = iter.getInt(BYTE_ORDER)
            val hostNameArray : Array[Byte] = Array.ofDim[Byte](hostNameSize)
            iter.copyToArray(hostNameArray)
            val hostName = hostNameArray.toString()
            val port = iter.getInt(BYTE_ORDER)
            (hostName, port)
          }).toList.map(x => new InetSocketAddress(x._1, x._2))
          synchronized {
            hostList ++= remoteHostList
            removedList ++= remoteRemoveList
            version.incrementAndGet()
            lastUpdateTimeStamp.set(System.currentTimeMillis())
          }
        }
      } 
      builder.result()
      
    }

  }
}

