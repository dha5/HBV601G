package com.hbv601.folf.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL


class NetworkService : Service() {
    val BackEnd = "https://hbv601g-backend.onrender.com/"

    override fun onBind(intent: Intent?): IBinder? {
        return null
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        println("I'm created")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("I'm destroyed")
    }
    fun getAPI(intent: Intent) {
        val url = URL(BackEnd+"api_endpoints")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"  // optional default is GET

            println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")

            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    println(line)
                }
            }
        }

    }




    /*            val connection = URL("example.com").openConnection() as HttpURLConnection
            connection.connect()
            println(connection.responseCode)
            println(connection.getHeaderField("Content-Type"))
            val text = connection.inputStream.use { it.reader().use { reader -> reader.readText() } }
*/
}