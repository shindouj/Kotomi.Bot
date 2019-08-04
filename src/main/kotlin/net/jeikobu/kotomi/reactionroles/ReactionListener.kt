package net.jeikobu.kotomi.reactionroles

import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent
import net.dv8tion.jda.core.hooks.EventListener
import net.dv8tion.jda.core.requests.RequestFuture
import net.jeikobu.jbase.config.AbstractConfigManager
import org.pmw.tinylog.Logger
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class ReactionListener(private val configManager: AbstractConfigManager, private val reactionConfig: ReactionConfig) : EventListener {
    private val requestMap: MutableMap<MessageReactionAddEvent, MutableList<Future<Void>>> = mutableMapOf()

    override fun onEvent(event: Event?) {
        when (event) {
            is MessageReactionAddEvent -> onReactionAddEvent(event)
            is MessageReactionRemoveEvent -> onReactionRemoveEvent(event)
        }
    }

    private fun requestMapCleanup() {
        with(requestMap.iterator()) {
            forEach {
                if (it.value.isEmpty()) remove()
            }
        }
    }

    private fun addRequest(e: MessageReactionAddEvent, request: RequestFuture<Void>) {
        requestMap[e]?.add(request)
        request.thenRun { requestMap[e]?.remove(request) }
    }

    private fun cancelAllMemberRequests(member: Member) {
        requestMap.forEach { mapElem ->
            with(mapElem.value.iterator()) {
                forEach {
                    if (mapElem.key.member == member) {
                        it.cancel(true)
                        remove()
                    } else if (it.isCancelled || it.isDone) {
                        remove()
                    }
                }
            }
        }
    }

    fun onReactionAddEvent(e: MessageReactionAddEvent) {
        requestMapCleanup()
        if (e.user.isBot) return

        val guild = e.guild
        val message = e.channel.getMessageById(e.messageIdLong).complete()
        val reaction = e.reaction
        val member = e.member

        if (reactionConfig.isMessageRegistered(message)) {
            Logger.debug("${member.effectiveName} reacted to message #${message.id} in ${guild.name} (${reaction.reactionEmote.name})")
            val role = reactionConfig.getRole(message, reaction, guild)
            guild.controller.addRolesToMember(member, role).queue()

            when (reactionConfig.getMode(message)) {
                ReactionMessageTypes.NORMAL -> {
                }

                ReactionMessageTypes.TOGGLE -> {
                    requestMap[e] = requestMap[e] ?: mutableListOf()
                    cancelAllMemberRequests(member)

                    for (it in message.reactions) {
                        if (it.reactionEmote.name != reaction.reactionEmote.name) {
                            val emoteRole = reactionConfig.getRole(message, it, guild)
                            addRequest(e, it.removeReaction(member.user).submit())
                            guild.controller.removeRolesFromMember(member, emoteRole).queue()
                        }
                    }
                }

                ReactionMessageTypes.ONETIME -> {
                    reaction.removeReaction(member.user).queue()
                }
            }
        }
    }

    fun onReactionRemoveEvent(e: MessageReactionRemoveEvent) {
        requestMapCleanup()
        if (e.user.isBot) return

        val guild = e.guild
        val message = e.channel.getMessageById(e.messageIdLong).complete()
        val reaction = e.reaction
        val member = e.member

        if (reactionConfig.isMessageRegistered(message) && reactionConfig.getMode(message) == ReactionMessageTypes.NORMAL) {
            Logger.debug("${member.effectiveName} removed a reaction to message #${message.id} in ${guild.name} (${reaction.reactionEmote.name})")
            val role = reactionConfig.getRole(message, reaction, guild)

            guild.controller.removeRolesFromMember(member, role).queueAfter(2, TimeUnit.SECONDS)
        }
    }

}