package me.imlc.wsproxy.pingpong

import io.vertx.core.Vertx
import io.vertx.core.http.HttpClient
import kotlin.Array
import kotlin.String

class Client {

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      val vertx = Vertx.vertx()
      val client: HttpClient = vertx.createHttpClient()

      client.webSocket(8080, "localhost", "/ws")
        .onSuccess { webSocket ->
          println("succeeded to connect ws://localhost:8080")
          webSocket.textMessageHandler { data ->
            println("< $data")
            client.close()
          }
          val ping = "ping"
          println("> $ping")
          webSocket.writeTextMessage(ping)
        }
        .onFailure { throwable ->
          println("failed to connect ws://localhost:8080")
          throwable.printStackTrace()
        }
    }
  }

}
