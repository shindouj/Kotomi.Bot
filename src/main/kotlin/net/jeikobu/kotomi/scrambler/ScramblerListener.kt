package net.jeikobu.kotomi.scrambler

import net.jeikobu.jbase.config.AbstractConfigManager
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IRole

import net.jeikobu.kotomi.scrambler.ScramblerKeys.*

class ScramblerListener(private val configManager: AbstractConfigManager) {
    companion object {
        private fun IRole.getMemberCount(): Int {
            return guild.users.count { it.hasRole(this) }
        }

        private fun getRoleList(configMap: Map<ScramblerKeys, String>, guild: IGuild): List<IRole> {
            val scramblerRoles = configMap[SCRAMBLER_ROLES]

            return scramblerRoles?.split(", ")?.mapNotNull { x ->
                try {
                    guild.getRoleByID(x.toLong())
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()
        }

        fun scrambleRoles(configMap: Map<ScramblerKeys, String>, guild: IGuild) {
            val scramblerMode = configMap[SCRAMBLER_MODE]

            if (scramblerMode != null) {
                var roleList = getRoleList(configMap, guild)
                val roleListInCurrentOrder: List<IRole> = guild.roles.mapNotNull {
                    if (roleList.contains(it)) {
                        it
                    } else {
                        null
                    }
                }

                roleList = when (ScramblerMode.fromName(scramblerMode)) {
                    ScramblerMode.MODE_MEMBER_COUNT -> {
                        roleList.sortedBy { it.getMemberCount() }
                    }
                    ScramblerMode.MODE_RANDOM -> {
                        roleList.shuffled()
                    }
                }

                val allRoles: MutableList<IRole> = guild.roles.toMutableList()

                for (i in 0 until roleList.size) {
                    val index = guild.roles.indexOf(roleListInCurrentOrder[i])
                    allRoles.removeAt(index)
                    allRoles.add(index, roleList[i])
                }

                guild.reorderRoles(*allRoles.toTypedArray())
            }
        }
    }

    @EventSubscriber
    fun onJoinEvent(e: UserJoinEvent) {
        val configMap: Map<ScramblerKeys, String> = ScramblerConfig(configManager.getGuildConfig(e.guild)).configMap

        if (configMap[SCRAMBLER_ENABLED] != null
                && configMap.getValue(SCRAMBLER_ENABLED).toBoolean()
                && configMap[SCRAMBLER_INTERVAL] != null
                && ScramblerInterval.fromString(configMap[SCRAMBLER_INTERVAL].toString()).type == ScramblerIntervalType.EVERY_MEMBER_JOIN_EVENT) {
            scrambleRoles(configMap, e.guild)
        }
    }
}