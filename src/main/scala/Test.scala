import org.apache.log4j.{BasicConfigurator, Logger}

/**
  * Created by rauricoste on 05/10/16.
  */
object Test extends App {

  def log() {
    BasicConfigurator.configure()
    lazy val logger = Logger.getLogger(getClass)

    org.apache.log4j.MDC.put("appName", "Bing")

    logger.error("example1")

    org.apache.log4j.MDC.put("appName", "Plop")

    logger.error("example2")
  }

  log()
}
