package org.gossip.network.actors

import java.nio.ByteBuffer

/**
 * Extends @WorkerActor functionality without exposing Actor internals.
 * 
 * @author sparade
 */
trait WorkerHandler {
  
  /**
   * Handle all messages received from TCP connection.
   * The output is forwarded to sender.
   * If the output is null, Close message is forwarded.
   */
  def handleRemoteMessage(data: ByteBuffer): ByteBuffer

  /**
   * Handles all messages received outside TCP connection.
   */
  def handleStorageMessage(data: Any)

  /**
   * Any message the server system should transmit to the sender 
   * on successful connection request.
   * 
   * if the message returned is null, no message is transmitted.
   */
  def firstMessage: ByteBuffer

}