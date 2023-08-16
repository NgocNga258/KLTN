package com.example.myapplication

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AzureRequest {

    // create singleton
    companion object {
        val instance = AzureRequest()
    }

    fun get_data(url: String, body: String, header: String, content_type: String): AzureData {
        try {
            val client = OkHttpClient()
            val mediaType = content_type.toMediaType()
            val body = body.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("authorization", header)
                .addHeader("Content-Type", content_type)
                .build()

            val response = client.newCall(request).execute()
            val str_data = response.body!!.string()

            val jsonObject = JSONObject(str_data)
            val resultsArray = jsonObject.getJSONArray("results")

            if (resultsArray.length() > 0) {
                val resultObject = resultsArray.getJSONObject(0)

                val temperature = resultObject.optDouble("temperature", 0.0)
                val humidity = resultObject.optDouble("humidity", 0.0)
                val led1 = resultObject.optInt("led1", 0)
                val led2 = resultObject.optInt("led2", 0)
                val timestamp = resultObject.optString("\$ts", "")


                // convert timestamp to date time
                val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                utcFormat.timeZone = TimeZone.getTimeZone("UTC")
                val utcDate = utcFormat.parse(timestamp)

                // get date, time in GMT+7
                val localFormat = SimpleDateFormat("yyyy-MM-dd")
                localFormat.timeZone = TimeZone.getTimeZone("GMT+7")
                val localDate = localFormat.format(utcDate)

                val localTimeFormat = SimpleDateFormat("HH:mm:ss")
                localTimeFormat.timeZone = TimeZone.getTimeZone("GMT+7")
                val localTime = localTimeFormat.format(utcDate)

                return AzureData(temperature, humidity, led1, led2, localTime, localDate)
            } else {
                println("No results found.")
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        return AzureData(0.0, 0.0, 0, 0, "", "")

    }

    fun sendCommand(deviceId: String, commandName: String, methodName: String, authorization: String) {
        val client = OkHttpClient()

        // Build the command URL
        val url = "https://khoa-luan-iot-fpga-soc.azureiotcentral.com/api/devices/$deviceId/commands/$commandName?api-version=2022-10-31-preview"

        // Create the request body with the methodName
        val requestBody = JSONObject().apply {
            put("methodName", methodName)
        }.toString().toRequestBody("application/json".toMediaType())

        // Build the request
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Authorization", authorization)
            .build()

        // Send the request asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle request failure
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle request success
                if (response.isSuccessful) {
                    println("Command sent successfully.")
                } else {
                    println("Failed to send command. Error: ${response.code}")
                }
            }
        })
    }

}