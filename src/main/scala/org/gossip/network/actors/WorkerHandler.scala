package org.gossip.network.actors

import java.nio.ByteBuffer

/**
 * @author sparade
 */
trait WorkerHandler {
  
  def handleRemoteMessage(data: ByteBuffer): ByteBuffer

  def handleStorageMessage(data: Any): ByteBuffer

  def firstMessage: ByteBuffer

}