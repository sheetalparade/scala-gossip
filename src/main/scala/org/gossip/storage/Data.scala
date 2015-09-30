package org.gossip.storage

import scala.collection.mutable.ListBuffer

/**
 * @author sparade
 */

object StorageManager {
  
  
  
  def getVersions (namespace: String):Map[String, Int] = {
    null;
  }
  
  def save(namespace: String, keys: Array[String]) : Data = {
    null;
  }
  
  def checkVersionsDiff(namespace: String, iVersions: Map[String, Int]) : Map[String, Int] = {
    null;
  }
}

class Data {
  
  private var dataMap = ListBuffer[Record]()
  
  def save = {}
  
  def update = {}
  
  def get = dataMap
  
  case class Record (namespace: String, version: Int, bytes: Array[Byte]);
}