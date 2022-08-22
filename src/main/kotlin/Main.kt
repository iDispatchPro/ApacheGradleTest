package org.apache.hc.core5.http.examples

import org.apache.hc.core5.http.ClassicHttpRequest
import org.apache.hc.core5.http.ClassicHttpResponse
import org.apache.hc.core5.http.HttpConnection
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.HttpRequest
import org.apache.hc.core5.http.HttpResponse
import org.apache.hc.core5.http.impl.Http1StreamListener
import org.apache.hc.core5.http.impl.bootstrap.HttpRequester
import org.apache.hc.core5.http.impl.bootstrap.RequesterBootstrap
import org.apache.hc.core5.http.io.SocketConfig
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder
import org.apache.hc.core5.http.message.RequestLine
import org.apache.hc.core5.http.message.StatusLine
import org.apache.hc.core5.http.protocol.HttpCoreContext
import org.apache.hc.core5.util.Timeout
import java.util.concurrent.TimeUnit

/**
 * Example of GET requests execution using classic I/O.
 */
fun main(args: Array<String>)
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
}