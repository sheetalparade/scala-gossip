package org.gossip.state

import java.io.DataOutput
import java.io.DataInput

/**
 * A Hearbeat class has the following fields
 * 1) generation, time in seconds when the the node started (keep track of restarts)
 * 2) beats, updated every seconds to keep track of versions.
 */
class HeartBeatState(val generation: Int, val beats: Int) {


}

/**
 * A companion object that handles the serialization/deserialization
 */
object HeartBeatState extends MessageSerializer[HeartBeatState] {

  /**
   * Serialize the message
   */
  override def serializer(message: HeartBeatState, out: DataOutput, version: Int) = {
    out.writeInt(message.generation)
    out.writeInt(message.beats)
  }

  /**
   * Deserialize a message to the HeartBeatState
   */
  override def deserializer(in: DataInput): HeartBeatState = {
    return new HeartBeatState(in.readInt(), in.readInt())
  }

}