package net.jeikobu.kotomi.reactionroles

import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent
import net.dv8tion.jda.core.hooks.EventListener
import net.jeikobu.jbase.config.AbstractConfigManager

class ReactionListener(private val configManager: AbstractConfigManager, private val reactionConfig: ReactionConfig): EventListener {
    override fun onEvent(event: Event?) {
        when (event) {
            is MessageReactionAddEvent -> onReactionAddEvent(event)
            is MessageReactionRemoveEvent -> onReactionRemoveEvent(event)
        }
    }

    fun onReactionAddEvent(e: MessageReactionAddEvent) {
        if (e.user.isBot) return

        val guild = e.guild
        val message = e.channel.getMessageById(e.messageIdLong).complete()
        val reaction = e.reaction
        val member = e.member

        if (reactionConfig.isMessageRegistered(message)) {
            val role = reactionConfig.getRole(message, reaction, guild)

            if (!member.roles.contains(role)) {
                guild.controller.addRolesToMember(member, role).complete()
            }

            when (reactionConfig.getMode(message)) {
                ReactionMessageTypes.NORMAL -> {
                }

                ReactionMessageTypes.TOGGLE -> {
                    message.reactions.mapNotNull { if (it.users.contains(member.user) && it.reactionEmote != reaction.reactionEmote) it else null }.forEach {
                        it.removeReaction(member.user).queue()
                    }
                }

                ReactionMessageTypes.ONETIME -> {
                    reaction.removeReaction(member.user).queue()
                }
            }
        }
    }

    fun onReactionRemoveEvent(e: MessageReactionRemoveEvent) {
        if (e.user.isBot) return

        val guild = e.guild
        val message = e.channel.getMessageById(e.messageIdLong).complete()
        val reaction = e.reaction
        val member = e.member

        if (reactionConfig.isMessageRegistered(message) && reactionConfig.getMode(message) != ReactionMessageTypes.ONETIME) {
            val role = reactionConfig.getRole(message, reaction, guild)

            if (member.roles.contains(role)) {
                guild.controller.removeRolesFromMember(member, role).complete()
            }
        }
    }
}