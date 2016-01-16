package org.gossip.network.actors

import java.nio.ByteBuffer
import java.io.InputStream

/**
 * Extends @WorkerActor functionality without exposing Actor internals.
 * 
 * @author sparade
 */
trait WorkerHandler {
    
  /**
   * Request metadata i.e versions to forward for comparisions
   * null output indicates close of connection.
   */
  def requestMetaData: ByteBuffer
  
  /**
   * null output indicates close of connection.
   */
  def requestDelta(data: ByteBuffer) : ByteBuffer 
  
  
  def merge(data: ByteBuffer) : ByteBuffer

}