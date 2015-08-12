package org.gossip.actors


import java.nio.ByteBuffer

import akka.actor.{Props, Actor, ActorLogging, ActorRef}
import akka.io.{IO, Tcp}
import akka.util.ByteString

object WorkerSystem {
	def props (worker: Class[_ <: WorkerActor])  : Props = Props.create(worker)
}


abstract class WorkerActor extends Actor with ActorLogging {

	import akka.io.Tcp._
	import context.system

	final def receive = {
		case connect : Connect =>
			log.info(s"WorkerActor Requested connection to seed "+connect)
			IO(Tcp) ! connect
		case commandFailed @ CommandFailed(_: Connect) =>
			log.info(s"WorkerActor Command failed $commandFailed")
			context stop self
		case received : Received =>
			log.info(s"WorkerActor received message $received")
			val returnMsg = handleMessage(received.data.asByteBuffer)
			val connection = sender()
			if(returnMsg != null){
				connection ! Write(ByteString(returnMsg), NoAck)
			}else{
				connection ! Close
			}
		case connected @ Connected(remote, local) =>
			log.info(s"WorkerActor successfully connected $connected")
			val connection = sender()
			connection ! Register(self)
			context become {
				case write : Write =>
					log.info(s"WorkerActor writing message $write to $connection")
					connection ! write
				case "close" =>
					log.info(s"WorkerActor closing connection as message receive close")
					connection ! Close
				case _: ConnectionClosed =>
					log.info(s"WorkerActor closing connection")
					context stop self
				case received : Received =>
					log.info(s"WorkerActor-CONTEXT received message $received")
					val returnMsg = handleMessage(received.data.asByteBuffer)
					val connection = sender()
					if(returnMsg != null){
						connection ! Write(ByteString(returnMsg), NoAck)
					}else{
						connection ! Close
					}
				case _ =>
					log.info(s"WorkerActor default case")
			}
	}

	def handleMessage(data: ByteBuffer): ByteBuffer


}