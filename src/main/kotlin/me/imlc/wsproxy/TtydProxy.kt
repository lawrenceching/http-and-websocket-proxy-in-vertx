package me.imlc.wsproxy

import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.http.HttpServerOptions
import io.vertx.httpproxy.HttpProxy
import io.vertx.httpproxy.ProxyOptions

class TtydProxy(private val vertx: Vertx = Vertx.vertx()) {

  fun start(startPromise: Promise<Void>) {

    val httpClientOptions = HttpClientOptions()
    httpClientOptions.setTryUsePerMessageWebSocketCompression(false)
    val proxyClient: HttpClient = vertx.createHttpClient()
    val proxyOptions = ProxyOptions()
    proxyOptions.supportWebSocket = true
    // https://vertx.io/docs/vertx-http-proxy/java/
    val proxy = HttpProxy.reverseProxy(proxyOptions, proxyClient)
    proxy.origin(30000, "localhost")

    val options = HttpServerOptions()
    options.addWebSocketSubProtocol("tty")
    // ttyd server does not support permessage-deflate extension
    options.setPerMessageWebSocketCompressionSupported(false)

    vertx
      .createHttpServer(options)
      .requestHandler(proxy)
      .requestHandler {
        if(it.uri().startsWith("/terminal")) {
          proxy.handle(it)
        } else {
          it.response().end("404 Not Found")
        }
      }
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

    @JvmStatic
    fun main(args: Array<String>) {
      val proxy = TtydProxy()
      val promise = Promise.promise<Void>()
      proxy.start(promise)
    }

  }
}
