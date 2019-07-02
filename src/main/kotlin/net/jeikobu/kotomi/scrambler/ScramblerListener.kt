package net.jeikobu.kotomi.scrambler

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Role
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.core.hooks.EventListener
import net.dv8tion.jda.core.requests.restaction.order.RoleOrderAction
import net.jeikobu.jbase.config.AbstractConfigManager

class ScramblerListener(private val configManager: AbstractConfigManager) : EventListener {
    companion object {

        private fun Role.getMemberCount(): Int {
            return guild.members.count { it.roles.contains(this) }
        }

        fun scrambleRoles(scramblerConfig: ScramblerConfig, guild: Guild) {
            val scramblerMode = scramblerConfig.scramblerMode
            val roleList = scramblerConfig.scramblerRoles

            if (scramblerMode != null && roleList != null) {
                val roleListInCurrentOrder = guild.roles.filter { roleList.contains(it) }

                val scrambledRoleList = when (scramblerMode) {
                    ScramblerMode.MODE_MEMBER_COUNT -> {
                        roleList.sortedBy { it.getMemberCount() }
                    }
                    ScramblerMode.MODE_RANDOM -> {
                        roleList.shuffled()
                    }
                }

                val action = RoleOrderAction(guild, true)

                for (role in scrambledRoleList) {
                    action.selectPosition(role)
                    val posDiff = action.selectedPosition + (roleListInCurrentOrder.indexOf(role) - scrambledRoleList.indexOf(role))
                    action.moveTo(posDiff)
                }

                action.complete()
            }
        }
    }

    override fun onEvent(e: Event) {
        if (e is GuildMemberJoinEvent) {
            val scramblerConfig = ScramblerConfig(configManager.getGuildConfig(e.guild))

            if (scramblerConfig.scramblerEnabled == true) {
                if (scramblerConfig.scramblerInterval?.type == ScramblerIntervalType.EVERY_MEMBER_JOIN_EVENT) {
                    scrambleRoles(scramblerConfig, e.guild)
                }
            }
        }
    }
}