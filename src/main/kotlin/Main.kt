package org.apache.hc.core5.http.examples

import org.apache.http.HttpHost
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.conn.routing.HttpRoute
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.LogManager

/**
 * Example of GET requests execution using classic I/O.
 */
fun main(args: Array<String>)
{
    apacheConnection()
}

fun apacheConnection()
{
    LogManager.getLogManager().reset()
    repeat(10) {
        println("")
        var start = Date().time

        val address = "https://ya.ru"

        val context = HttpClientContext.create()
        val connMrg = BasicHttpClientConnectionManager()

        val route = HttpRoute(HttpHost.create(address), null, true)

        // Wait for connection up to 10 sec
        val connection = connMrg.requestConnection(route, null).get(10, TimeUnit.SECONDS)

        try
        {
            // establish connection based on its route info
            connMrg.connect(connection, route, 1000, context)
            // and mark it as route complete
            //connMrg.routeComplete(conn, route, context)
            connMrg.releaseConnection(connection, null, 1, TimeUnit.DAYS)

            println("Connection: " + (Date().time - start).toString())

            start = Date().time

            //val routePlanner = DefaultProxyRoutePlanner(HttpHost("localhost", 8888))

            val httpClient = HttpClients.custom()
            .setConnectionManager(connMrg)
            //  .setRoutePlanner(routePlanner)
            .build()

            //val httpClient = HttpClients.createDefault()

            //            val exeRequest = HttpRequestExecutor()

            //context.targetHost = HttpHost(address, 443)
            val get = HttpGet(address)

            println(get.toString())

            val response = httpClient.execute(get)

            //          get.addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
            //            get.addHeader("accept-encoding", "gzip, deflate, br")
            //            get.addHeader("accept-language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")

            //          val response = exeRequest.execute(get, conn, context)

            //val httpGet = HttpGet("https://ya.ru")

            try
            {
                // val response = httpClient.execute(httpGet)

                println(response.statusLine)
            }
            catch (ex: ClientProtocolException)
            {
                // Handle protocol errors
            }
            catch (ex: IOException)
            {
                // Handle I/O errors
            }

            //sleep(1000)
            //   httpClient.close()
        }
        finally
        {
            //connMrg.releaseConnection(conn, null, 1, TimeUnit.MINUTES)
        }

        println("Request: " + (Date().time - start).toString())
    }
}
/*
fun testApache()
{
    val httpRequester: HttpRequester = RequesterBootstrap.bootstrap()
        .setStreamListener(object : Http1StreamListener
                           {
                               override fun onRequestHead(connection: HttpConnection, request: HttpRequest?)
                               {
                                   println(connection.remoteAddress.toString() + " " + RequestLine(request))
                               }

                               override fun onResponseHead(connection: HttpConnection, response: HttpResponse?)
                               {
                                   println(connection.remoteAddress.toString() + " " + StatusLine(response))
                               }

                               override fun onExchangeComplete(connection: HttpConnection, keepAlive: Boolean)
                               {
                                   if (keepAlive)
                                   {
                                       println(connection.remoteAddress.toString() + " exchange completed (connection kept alive)")
                                   }
                                   else
                                   {
                                       println(connection.remoteAddress.toString() + " exchange completed (connection closed)")
                                   }
                               }
                           })

        .setSocketConfig(SocketConfig.custom()
                             .setSoTimeout(5, TimeUnit.SECONDS)
                             .build())
        .create()

    val coreContext: HttpCoreContext = HttpCoreContext.create()
    val target = HttpHost("ya.ru")
    val requestUris = arrayOf("/", "/ip", "/user-agent", "/headers")

    for (i in requestUris.indices)
    {
        val requestUri = requestUris[i]

        val request: ClassicHttpRequest = ClassicRequestBuilder.get()
            .setHttpHost(target)
            .setPath(requestUri)
            .build()

        httpRequester.execute(target, request, Timeout.ofSeconds(5), coreContext).use { response ->
            println(requestUri + "->" + response.getCode())
            // System.out.println(EntityUtils.toString(response.getEntity()))
            println("==============")
        }
    }
}*/