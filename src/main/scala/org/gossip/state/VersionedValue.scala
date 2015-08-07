package org.gossip.state

import java.io.DataOutput
import java.io.DataInput

/**
 * VersionedValues are the values that are shared amongst the gossiping nodes.
 * Each VersionedValue contains a value and a version number associated with the value.
 * The version number is used to determine whether the value is stale or fresh. <br/>
 * Each value that needs to be shares should be of this type.
 */
class VersionedValue(val value: String, val version: Int) extends MessageSerializer {

  /**
   * Serialize the message
   */
  def serializer(value: VersionedValue, out: DataOutput, version: Int) = {
    out.writeUTF(value.value)
    out.writeInt(value.version)
  }

  /**
   * Deserialize a message to the HeartBeatState
   */
  def deserializer(in: DataInput): VersionedValue = {
    return new VersionedValue(in.readUTF(), in.readInt())
  }

}