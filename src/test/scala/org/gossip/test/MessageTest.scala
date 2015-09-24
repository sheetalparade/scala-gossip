package org.gossip.test

import akka.actor.{Actor, Props, ActorSystem}
import akka.actor.ActorLogging
import org.gossip.messages.{Message, Verb, GossipSyn, MessageHandler}
import org.gossip.state.GossipDigest
import org.gossip.messages.GossipSyn


object MessageTest extends App {
  
  //val header = new Header(java.net.InetAddress.getLocalHost(), Verb.GOSSIP_SYN, Map[String, Array[Byte]]())
  
  val syn = GossipSyn("Test Cluster", List[GossipDigest]())
  syn.from = java.net.InetAddress.getLocalHost()
  syn.verb = Verb.GOSSIP_SYN
  syn.options = Map[String, Array[Byte]]()
  
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
  
  /* we are done here */
  system.shutdown;

}