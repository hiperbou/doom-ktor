package com.hiperbou.service.room

import com.hiperbou.plugins.GameRoom
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class GameRoomInstances() {
    private val gameRooms = ConcurrentHashMap<String, GameRoom>()

    fun newRoom():GameRoom {
        val roomID = UUID.randomUUID().toString()
        val gameRoom = GameRoom(roomID) {
            gameRooms.remove(roomID)
        }
        gameRooms.put(roomID, gameRoom)
        return gameRoom
    }

    fun getGameRoom(roomID: String): GameRoom? {
        return gameRooms.getOrElse(roomID) { null }
    }
}
