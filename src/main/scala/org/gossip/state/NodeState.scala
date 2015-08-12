package org.gossip.state

import java.net.InetAddress
import java.io.DataOutput
import java.io.DataInput

/**
 * NodeState keeps the up-to-date known state for a corresponding node.
 * This object will be passes around "gossipped" to other nodes.
 */
class NodeState(var heartBeat: HeartBeatState) {

  /**
   * Key value map that is "gossipped" around.
   */
  var nodeStates = scala.collection.mutable.Map[VersionedValueKey, VersionedValue]()
  var isAlive: Boolean = false
  var updatedTimestamp: Long = System.currentTimeMillis()

}

/**
 * A companion object that handles the serialization/deserialization
 */
object NodeState extends MessageSerializer[NodeState] {

  /**
   * Serialize the message
   */
  def serializer(nodeState: NodeState, out: DataOutput, version: Int) = {

    val heatbeat = nodeState.heartBeat
    /* serialize the heartbeat first */
    HeartBeatState.serializer(heatbeat, out, version)

    /* serialize the nodeStates */
    val size: Int = nodeState.nodeStates.size
    out.writeInt(size)

    nodeState.nodeStates.foreach { state =>
      VersionedValueKey.serializer(state._1, out, version)
      VersionedValue.serializer(state._2, out, version)
    }

  }

  /**
   * Deserialize a message to the HeartBeatState
   */
  def deserializer(in: DataInput): NodeState = {

    val heatbeat: HeartBeatState = HeartBeatState.deserializer(in)
    var nodeState: NodeState = new NodeState(heatbeat)

    val size: Int = in.readInt()

    for (i <- 1 to size) {
      val key: VersionedValueKey = VersionedValueKey.deserializer(in)
      val value: VersionedValue = VersionedValue.deserializer(in)
      nodeState.nodeStates += key -> value
    }

    return nodeState

  }

}
