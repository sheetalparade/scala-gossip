package org.gossip.messages
import java.net.InetAddress
import org.gossip.messages.Verb._


/**
 * Header for the message
 */
class Header(val from: InetAddress, val verb: Verb, val options: Map[String, Array[Byte]]) {

}