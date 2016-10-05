package appender

import java.util.concurrent.ConcurrentHashMap

import org.apache.log4j
import org.apache.log4j.spi.LoggingEvent
import org.apache.log4j.{AppenderSkeleton, FileAppender}

import scala.collection.JavaConversions._

/**
  * Created by rauricoste on 05/10/16.
  */
class MySiftAppender extends AppenderSkeleton {
  var key: String = null
  var default: String = "default"
  var file: String = null
  private val cacheAppenders = new ConcurrentHashMap[String, FileAppender]()

  override def append(event: LoggingEvent): Unit = {
    val mdcValue = Option(event.getMDC(key).asInstanceOf[String]).getOrElse(default)
    val delegateAppender = Option(cacheAppenders.get(mdcValue)).getOrElse {
      println(s"creating appender for MDC key $mdcValue")
      println(s"layout: ${getLayout}")
      println(s"file: ${file}")
      val replacedFile = file.replaceAll("\\$\\[" + key + "\\]", mdcValue)
      println(s"file: ${replacedFile}")
      val newAppender = new log4j.FileAppender(getLayout, replacedFile, true)
      newAppender.setName(s"fileAppender-$mdcValue")
      cacheAppenders.put(mdcValue, newAppender)
      newAppender
    }
    delegateAppender.append(event)
  }

  override def requiresLayout(): Boolean = true

  override def close(): Unit = {
    cacheAppenders.toMap.foreach(_._2.close())
  }

  def setKey(newKey: String): Unit = key = newKey

  def setDefault(newDefault: String): Unit = default = newDefault

  def setFile(newFile: String) = file = newFile
}
