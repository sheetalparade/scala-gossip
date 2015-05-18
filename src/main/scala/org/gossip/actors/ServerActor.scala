package org.gossip.actors

import java.net.InetSocketAddress
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.io.IO
import akka.io.Tcp
import akka.actor.Props

/**
 * Server actor starts the server to listen for gossip on the netowork
 */
class ServerActor(inetSocketAddress: InetSocketAddress) extends Actor with ActorLogging {

  import akka.io.Tcp._
  import context.system
  
  IO(Tcp) ! Bind(self, inetSocketAddress)

  override def receive : Receive  = {
    case Connected(r, l) =>
      log.info(s"remote ${r} connected to ${l}")
      val handler = context.actorOf(Props[WorkerActor])
      val connection = sender
      connection ! Register(handler)
  }
}