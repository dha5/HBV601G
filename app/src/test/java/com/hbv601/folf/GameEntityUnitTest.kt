package com.hbv601.folf

import com.hbv601.folf.Entities.GameEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate


class GameEntityUnitTest {
    @Test
    fun createGameEntity(){
        val gameEntity = GameEntity("A","B",LocalDate.now(),"C")
        assertEquals(gameEntity.gameTitle,"A")
        assertEquals(gameEntity.creatingPlayer,"C")
        assertEquals(gameEntity.course, "B")

    }
}