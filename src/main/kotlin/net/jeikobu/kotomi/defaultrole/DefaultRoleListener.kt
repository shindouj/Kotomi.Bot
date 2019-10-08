package net.jeikobu.kotomi.defaultrole

import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.core.hooks.EventListener
import net.jeikobu.jbase.config.AbstractConfigManager
import org.pmw.tinylog.Logger

class DefaultRoleListener(private val configManager: AbstractConfigManager, private val defaultRoleConfig: DefaultRoleConfig) : EventListener {
    override fun onEvent(event: Event?) {
        if (event is GuildMemberJoinEvent) {
            val guildConfig = configManager.getGuildConfig(event.guild)
            if (guildConfig.getValue<Boolean>(DefaultRoleConfig.DEFAULT_ROLE_ENABLED_CONFIG_KEY) == false) {
                return
            }

            val roles = try {
                defaultRoleConfig.getRoles(DefaultRoleTriggers.JOIN_EVENT, event.guild)
            } catch (e: Exception) {
                Logger.error(e, "An error occured when trying to get Default Join Roles!")
                null
            }

            if (roles != null) {
                event.guild.controller.addRolesToMember(event.member, roles).queue()
            }
        }
    }
}