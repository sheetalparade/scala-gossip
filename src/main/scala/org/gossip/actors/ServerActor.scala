package org.gossip.actors

import scala.actors.Actor
import java.net.InetSocketAddress
import akka.io.IO
import akka.io.Tcp

/**
 * Server actor starts the server to listen for gossip on the netowork
 */
class ServerActor(inetSocketAddress: InetSocketAddress) extends Actor {

  import akka.io.Tcp._

  IO(Tcp) ! Bind(self, inetSocketAddress)

  def reveive = {

  }
}