package org.gossip.test

import akka.actor.{Actor, Props, ActorSystem}
import akka.actor.ActorLogging
import org.gossip.messages.{Message, Verb, GossipSyn, Header, MessageHandler}
import org.gossip.state.GossipDigest


object MessageTest extends App {
  
  val header = new Header(java.net.InetAddress.getLocalHost(), Verb.GOSSIP_SYN, Map[String, Array[Byte]]())
  
  val syn = GossipSyn(header, Array[Byte](), 1, "Test Cluster", List[GossipDigest]())
  
  /**
   * Test sender
   */
  class TestGossiper extends Actor with ActorLogging {
    
    def receive = {
        
      case msg : Message => 
        log.info(s"Received message ${msg}")
        MessageHandler.handle(msg)
        
      case _ =>
        log.error("Unexpected message recieved")
    }
    
  }
  
  
  
  val system = ActorSystem("gossip")
  
  val sender = system.actorOf(Props(classOf[TestGossiper]), "sender")
  val receiver = system.actorOf(Props(classOf[TestGossiper]), "receiver")

  receiver.tell(syn, sender) 

}