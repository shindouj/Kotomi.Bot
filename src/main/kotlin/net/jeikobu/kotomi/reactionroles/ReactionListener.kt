package net.jeikobu.kotomi.reactionroles

import net.jeikobu.jbase.config.AbstractConfigManager
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionRemoveEvent

class ReactionListener(private val configManager: AbstractConfigManager, private val reactionConfig: ReactionConfig) {
    @EventSubscriber
    fun onReactionAddEvent(e: ReactionAddEvent) {
        val guild = e.guild
        val message = e.message
        val reaction = e.reaction
        val user = e.user

        if (reactionConfig.isMessageRegistered(message)) {
            val role = reactionConfig.getRole(message, reaction, guild)

            if (!user.hasRole(role)) {
                user.addRole(role)
            }

            when (reactionConfig.getMode(message)) {
                ReactionMessageTypes.NORMAL -> {
                }

                ReactionMessageTypes.TOGGLE -> {
                    message.reactions.mapNotNull { if (it.users.contains(user) && it.emoji != reaction.emoji) it else null }.forEach {
                        message.removeReaction(user, it)
                    }
                }

                ReactionMessageTypes.ONETIME -> {
                    message.removeReaction(user, reaction)
                }
            }
            user.addRole(role)
        }
    }

    @EventSubscriber
    fun onReactionRemoveEvent(e: ReactionRemoveEvent) {
        val guild = e.guild
        val message = e.message
        val reaction = e.reaction
        val user = e.user

        if (reactionConfig.isMessageRegistered(message)) {
            val role = reactionConfig.getRole(message, reaction, guild)

            if (user.hasRole(role)) {
                user.removeRole(role)
            }
        }
    }
}