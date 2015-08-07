package org.gossip.state


/**
 * This trait contains the methods to serialize and deserialize messages.
 */
trait MessageSerializer extends Serializable {
  
  def serializer() 
  
  def deserializer()
  
}