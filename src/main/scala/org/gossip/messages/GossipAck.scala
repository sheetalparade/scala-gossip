package org.gossip.messages
import java.net.InetAddress
import org.gossip.state.State

case class GossipAck(states: Map[InetAddress, State]) extends Message {

}