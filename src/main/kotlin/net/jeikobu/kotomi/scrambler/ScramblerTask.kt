package net.jeikobu.kotomi.scrambler

import net.dv8tion.jda.core.entities.Guild
import net.jeikobu.jbase.config.AbstractConfigManager
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ScramblerTask(private val configManager: AbstractConfigManager, private val guilds: List<Guild>) : Runnable {
    private val executor = Executors.newSingleThreadExecutor()
    private var lastExecution: Future<out Any>? = null

    override fun run() {
        val lastExecutionRightNow = lastExecution
        if (lastExecutionRightNow != null && !lastExecutionRightNow.isDone) {
            return
        }

        lastExecution = executor.submit(task)
    }

    private val task = Runnable {
        for (guild in guilds) {
            val scramblerConfig = ScramblerConfig(configManager.getGuildConfig(guild))

            if (scramblerConfig.scramblerEnabled == true) {
                val scramblerInterval = scramblerConfig.scramblerInterval
                val scramblerTick = scramblerConfig.scramblerTick
                if (scramblerInterval?.type == ScramblerIntervalType.EVERY_N_SECS && scramblerTick != null) {
                    scramblerConfig.scramblerTick = if (scramblerInterval.data == scramblerTick.toString()) {
                        ScramblerListener.scrambleRoles(scramblerConfig, guild)
                        1
                    } else {
                        scramblerTick + 1
                    }
                }
            }
        }
    }
}