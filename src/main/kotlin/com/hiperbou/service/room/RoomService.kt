package com.hiperbou.service.room

import com.hiperbou.multiplayer.GameRoomInstances
import kotlinx.serialization.Serializable

@Serializable
data class NewRoomResponse(val room:String)

@Serializable
data class RoomResponse(val room:String, var gameStarted:Boolean)

@Serializable
data class RoomNotFoundResponse(val reason:String)

interface RoomService {
    fun newRoom():NewRoomResponse
    fun room(id:String):RoomResponse?
    fun setRoomStarted(id:String):RoomResponse?
}

class MockRoomService: RoomService {
    override fun newRoom(): NewRoomResponse {
        return NewRoomResponse("12344567890-1234567890")
    }

    override fun room(id: String): RoomResponse {
        return RoomResponse(id, false)
    }

    override fun setRoomStarted(id: String): RoomResponse {
        return RoomResponse(id, true)
    }
}

class InMemoryRoomService(private val gameRoomInstances: GameRoomInstances): RoomService {
    override fun newRoom(): NewRoomResponse {
        val gameRoom = gameRoomInstances.newRoom()
        return NewRoomResponse(gameRoom.id)
    }

    override fun room(id: String): RoomResponse? {
        val gameRoom = gameRoomInstances.getGameRoom(id) ?: return null
        return RoomResponse(gameRoom.id, gameRoom.gameStarted)
    }

    override fun setRoomStarted(id: String): RoomResponse? {
        val gameRoom = gameRoomInstances.getGameRoom(id) ?: return null
        gameRoom.gameStarted = true
        return RoomResponse(gameRoom.id, gameRoom.gameStarted)
    }
}
