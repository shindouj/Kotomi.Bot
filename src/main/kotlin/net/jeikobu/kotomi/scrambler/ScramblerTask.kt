package net.jeikobu.kotomi.scrambler

import net.jeikobu.jbase.config.AbstractConfigManager
import sx.blah.discord.handle.obj.IGuild
import java.util.concurrent.Executors
import java.util.concurrent.Future
import net.jeikobu.kotomi.scrambler.ScramblerKeys.*

class ScramblerTask(private val configManager: AbstractConfigManager, private val guilds: List<IGuild>) : Runnable {
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
            val guildConfig = configManager.getGuildConfig(guild)
            val configMap: Map<ScramblerKeys, String> = ScramblerConfig(guildConfig).configMap

            if (configMap[SCRAMBLER_ENABLED] != null && configMap[SCRAMBLER_ENABLED]!!.toBoolean() && configMap[SCRAMBLER_INTERVAL] != null) {
                val scramblerInterval = ScramblerInterval.fromString(configMap[SCRAMBLER_INTERVAL]!!)
                var scramblerTick = configMap[SCRAMBLER_TICK] ?: "1"

                if (scramblerInterval.type == ScramblerIntervalType.EVERY_N_SECS) {
                    scramblerTick = if (scramblerInterval.data == scramblerTick) {
                        ScramblerListener.scrambleRoles(configMap, guild)
                        "1"
                    } else {
                        (scramblerTick.toInt() + 1).toString()
                    }

                    guildConfig.setValue(ScramblerKeys.SCRAMBLER_TICK.configKey, scramblerTick)
                }
            }
        }
    }
}