package com.hiperbou

import com.hiperbou.plugins.configureRouting
import com.hiperbou.plugins.configureSerialization
import com.hiperbou.plugins.configureWeb
import com.hiperbou.service.MockRoomService
import com.hiperbou.service.NewRoomResponse
import com.hiperbou.service.RoomResponse
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }
        application {
            configureSerialization()
            configureWeb()
            configureRouting(MockRoomService())
        }

        client.get("/api/newroom").apply {
            assertEquals(HttpStatusCode.OK, status)
            println(bodyAsText())
            println(headers.flattenEntries())
            val newRoomResponse = body<NewRoomResponse>()
            assertEquals("12344567890-1234567890", newRoomResponse.room)
        }

        client.get("/api/room/12344567890-1234567890").apply {
            assertEquals(HttpStatusCode.OK, status)
            val roomResponse = body<RoomResponse>()
            assertEquals("12344567890-1234567890", roomResponse.room)
            assertFalse(roomResponse.gameStarted)
        }

        client.get("/123").apply {
            assertEquals(HttpStatusCode.OK, status)
            println(bodyAsText())
        }
    }
}