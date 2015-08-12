package org.gossip.state
import java.io.DataOutput
import java.io.DataInput

/**
 * This trait contains the methods to serialize and deserialize messages.
 */
trait MessageSerializer[T] extends Serializable {

  def serializer(message: T, out: DataOutput, version: Int)

  def deserializer(in: DataInput): T

}