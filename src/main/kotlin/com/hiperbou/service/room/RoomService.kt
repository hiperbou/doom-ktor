package com.hiperbou.service

import kotlinx.serialization.Serializable

@Serializable
data class NewRoomResponse(val room:String)

@Serializable
data class RoomResponse(val room:String, val gameStarted:Boolean)

interface RoomService {
    fun newRoom():NewRoomResponse
    fun room(id:String):RoomResponse
}

class MockRoomService: RoomService{
    override fun newRoom(): NewRoomResponse {
        return NewRoomResponse("12344567890-1234567890")
    }

    override fun room(id: String): RoomResponse {
        return RoomResponse(id, false)
    }
}


