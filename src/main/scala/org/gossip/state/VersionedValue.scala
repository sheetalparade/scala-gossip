package org.gossip.state

import java.io.DataOutput
import java.io.DataInput

/**
 * VersionedValues are the values that are shared amongst the gossiping nodes.
 * Each VersionedValue contains a value and a version number associated with the value.
 * The version number is used to determine whether the value is stale or fresh. <br/>
 * Each value that needs to be shares should be of this type.
 */
class VersionedValue(val value: String, val version: Int) {

}

/**
 * A companion object that handles the serialization/deserialization
 */
object VersionedValue extends MessageSerializer[VersionedValue] {

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

/**
 * This trait class contains the key for the versioned value {@link VersionedValue}
 * The key along with the value will be stored in  {@link NodeState}
 *
 * For custom values, just extend this trait.
 */
class VersionedValueKey(val key: String) {

}

/**
 * A companion object that handles the serialization/deserialization
 */
object VersionedValueKey extends MessageSerializer[VersionedValueKey] {

  /* some helper strings */
  val INTERNAL_IP: String = "INTERNAL_IP"
  val RPC_ADDRESS: String = "RPC_ADDRESS"
  val RELEASE_VERSION: String = "RELEASE_VERSION"

  /**
   * Serialize the message
   */
  def serializer(valueKey: VersionedValueKey, out: DataOutput, version: Int) = {
    out.writeUTF(valueKey.key)
  }

  /**
   * Deserialize a message to the HeartBeatState
   */
  def deserializer(in: DataInput): VersionedValueKey = {
    return new VersionedValueKey(in.readUTF())
  }
}