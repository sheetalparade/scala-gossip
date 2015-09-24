package org.gossip.network.actors

import akka.actor.{ActorRef, Actor, ActorLogging, Props}
import akka.io.{IO, Tcp}

import com.typesafe.scalalogging._


object ServerSystem {
  def props (workers : Class[_ <: WorkerActor]) : Props = Props.create(classOf[ServerActor], workers)
}
/**
 * Server actor starts the server to listen for gossip on the netowork
 */
class ServerActor(workers : Class[_ <: WorkerActor]) extends Actor with ActorLogging {
  import context.system
  import akka.io.Tcp._

  def getHandler: ActorRef = {
    context.actorOf(WorkerSystem.props(workers))
  }

  override def receive : Receive  = {
    case bind : Bind =>
      log.info(s"ServerActor Binding $bind")
      IO(Tcp) ! bind
    case Connected(r, l) =>
      log.info(s"ServerActor remote ${r} connected to ${l}")
      val connection = sender
      connection ! Register(getHandler)
    case CommandFailed(_: Bind) =>
      log.info(s"ServerActor not connected")
      context stop self
    case b : Bound =>
      log.info(s"ServerActor Connected and accepting request. $b")
    case c @ Close =>
      log.info(s"ServerActor Closing .. $c")
      context stop self
    case x @ _ =>
      log.info(s"ServerActor in default event received is $x")
  }
}