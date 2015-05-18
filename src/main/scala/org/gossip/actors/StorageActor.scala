package org.gossip.actors

import akka.actor.Actor

/**
 * StorageActor manages the state of data received from various agents on the cluster.
 * 
 * This is the only place with read/write of data
 * 
 */
class StorageActor extends Actor{

  def receive = {
    null
  }
}