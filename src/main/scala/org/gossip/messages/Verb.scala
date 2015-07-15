package org.gossip.messages

object Verb extends Enumeration {
  type Verb = Value
  val GOSSIP_SYN, GOSSIP_ACK, GOSSIP_SYN_ACK = Value  
}