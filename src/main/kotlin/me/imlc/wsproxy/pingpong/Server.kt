package me.imlc.wsproxy.pingpong

import io.vertx.core.Vertx

class Server {

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      val vertx = Vertx.vertx()
      val server = vertx.createHttpServer()

      server
        .webSocketHandler { serverWs ->
          serverWs.textMessageHandler {data ->
            println("< $data")
            val pong = "pong"
            serverWs.writeTextMessage(pong)
            println("> $pong")
          }
        }
        .listen(8080)

    }
  }
}
