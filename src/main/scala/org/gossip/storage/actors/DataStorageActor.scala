package org.gossip.storage.actors

import akka.actor.{ActorRef, Actor, ActorLogging, Props}
import akka.io.{IO, Tcp}

import com.typesafe.scalalogging._

import org.gossip.storage.Messages._

import akka.actor.Props

/**
 * @author sparade
 * 
 * Not in any use yet
 */

object DataStorageSystem {
  def props: Props = Props.create(classOf[DataStorageActor])

}

class DataStorageActor extends Actor with ActorLogging {
  
  override def receive : Receive  = {
    case GET =>
      log.debug(s"Get message received.")
    case UPDATE =>
      log.debug(s"Update message received.")
    case SAVE =>
      log.debug(s"Save message received.")
    case _ =>
    log.debug(s"Default handler")
  }
}