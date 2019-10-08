package net.jeikobu.kotomi

import net.dv8tion.jda.core.OnlineStatus
import net.dv8tion.jda.core.entities.Game
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PresenceManager(kotomi: KotomiBot) {
    private val tickExecutor = Executors.newScheduledThreadPool(1)
    private val presences = listOf(
            Game.playing("Version ${kotomi.version}"),
            Game.playing("the violin"),
            Game.watching("Tomoya eat")
    )
    private var currentPresencePos = presences.size
    private val presenceChanger = Runnable {
        currentPresencePos++
        if (currentPresencePos > presences.size - 1) {
            currentPresencePos = 0
        }
        kotomi.client.presence.setPresence(OnlineStatus.ONLINE, presences[currentPresencePos])
    }

    fun startPresenceChanger() {
        tickExecutor.scheduleAtFixedRate(presenceChanger, 0, 1, TimeUnit.MINUTES)
    }

    fun stopPresenceChanger() {
        tickExecutor.shutdown()
    }
}