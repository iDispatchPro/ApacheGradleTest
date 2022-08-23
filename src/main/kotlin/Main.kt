package org.apache.hc.core5.http.examples

import org.apache.http.HttpHost
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.conn.routing.HttpRoute
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.BasicHttpClientConnectionManager
import java.io.IOException
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit

/**
 * Example of GET requests execution using classic I/O.
 */
fun main(args: Array<String>)
{
    apacheConnection()
}

fun apacheConnection()
{
    val context = HttpClientContext.create()
    val connMrg = BasicHttpClientConnectionManager()
    val route = HttpRoute(HttpHost.create("https://ya.ru"))

    // Request new connection. This can be a long process
    val connRequest = connMrg.requestConnection(route, null)

    // Wait for connection up to 10 sec
    val conn = connRequest.get(10, TimeUnit.SECONDS)

    try
    {
        // If not open
        if (!conn.isOpen)
        {
            // establish connection based on its route info
            connMrg.connect(conn, route, 1000, context)
            // and mark it as route complete
            connMrg.routeComplete(conn, route, context)
        }

        val httpClient = HttpClients.custom()
            .setConnectionManager(connMrg)
            .build()


        val httpGet = HttpGet("https://ya.ru")

        try
        {
            val response = httpClient.execute(httpGet, context)

            response.use { it ->
                print(it.statusLine)
            }
        }
        catch (ex: ClientProtocolException)
        {
            // Handle protocol errors
        }
        catch (ex: IOException)
        {
            // Handle I/O errors
        }

        sleep(1000)
        httpClient.close()
    }
    finally
    {
        connMrg.releaseConnection(conn, null, 1, TimeUnit.MINUTES)
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