package me.imlc.wsproxy

import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.http.*
import io.vertx.httpproxy.HttpProxy
import io.vertx.httpproxy.ProxyOptions
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.concurrent.CompletableFuture


class WebSocketProxy(private val vertx: Vertx = Vertx.vertx()) {

  fun start(startPromise: Promise<Void>) {

    val proxyClient: HttpClient = vertx.createHttpClient()
    val proxyOptions = ProxyOptions()
    proxyOptions.supportWebSocket = true
    val proxy = HttpProxy.reverseProxy(proxyOptions, proxyClient)
    proxy.origin(30000, "localhost")

    val options = HttpServerOptions()
    options.addWebSocketSubProtocol("tty")

    vertx
      .createHttpServer(options)
      .requestHandler(proxy)
      .listen(8888) { http ->
        if (http.succeeded()) {
          startPromise.complete()
          println("HTTP server started on port 8888")
        } else {
          startPromise.fail(http.cause());
        }
      }
  }

  companion object {

    private val logger = LoggerFactory.getLogger(TtydProxy::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
      val proxy = WebSocketProxy()
      val promise = Promise.promise<Void>()
      proxy.start(promise)
    }

  }
}
