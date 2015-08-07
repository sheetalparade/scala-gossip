package org.gossip.actors

import java.net.InetSocketAddress
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.io.{IO, Tcp}
import akka.actor.Props

import com.typesafe.scalalogging._


object ServerActor {
  def props(inetSocketAddress: InetSocketAddress): Props = Props(classOf[ServerActor], inetSocketAddress)
}
/**
 * Server actor starts the server to listen for gossip on the netowork
 */
class ServerActor(inetSocketAddress: InetSocketAddress) extends Actor with ActorLogging {
  import context.system
  import akka.io.Tcp._
  log.debug(s"now in ServerActor $inetSocketAddress")

  IO(Tcp) ! Bind(self, inetSocketAddress)

  override def receive : Receive  = {
    case Connected(r, l) =>
      log.info(s"remote ${r} connected to ${l}")
      val handler = context.actorOf(Props[WorkerActor])
      val connection = sender
      connection ! Register(handler)
    case CommandFailed(_: Bind) =>
      log.info(s"not connected")
      context stop self
    case b @ Bound(localAddress) =>
      log.info(s"Connected and accepting request. $b")
    case x @ _ =>
      log.info(s"in default $x")
  }
}