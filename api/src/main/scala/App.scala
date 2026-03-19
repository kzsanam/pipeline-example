import scala.util.Try

import configs.Config
import controllers.DbController
import controllers.HealthController
import pureconfig._
import pureconfig.generic.auto._
import services.DynamoDbService
import zio.ZIOAppArgs
import zio.ZIOAppDefault
import zio.ZLayer
import zio.http.Server
import zio.http.netty.NettyConfig
import zio.http.netty.NettyConfig.LeakDetectionLevel

object App extends ZIOAppDefault {
  val conf = ConfigSource.default.loadOrThrow[Config]
  // Set a port
  private val PORT = conf.port

  // services
  private val dbService = {
    val db = new DynamoDbService(conf.dbConfig)
    db.init()
    db
  }

  // controllers
  private val healthController = new HealthController
  private val dbController = new DbController(dbService)

  // routes
  private val healthRoutes = healthController.routes
  private val dbRoutes = dbController.routes

  // run
  val run = ZIOAppArgs.getArgs.flatMap { args =>
    // Configure thread count using CLI
    val nThreads: Int =
      args.headOption.flatMap(x => Try(x.toInt).toOption).getOrElse(0)

    val config = Server.Config.default.port(PORT)
    val nettyConfig = NettyConfig.default
      .leakDetection(LeakDetectionLevel.PARANOID)
      .maxThreads(nThreads)
    val configLayer = ZLayer.succeed(config)
    val nettyConfigLayer = ZLayer.succeed(nettyConfig)

    (healthRoutes ++ dbRoutes)
      .serve[Any]
      .provide(configLayer, nettyConfigLayer, Server.customized)
  }
}
