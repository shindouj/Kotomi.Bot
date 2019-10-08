package net.jeikobu.kotomi.dadjoke

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.serialization.responseObject
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageEmbed
import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData
import java.awt.Color

@Command(name = "dadJoke", argsLength = 0)
class DadJokeCommand(data: CommandData) : AbstractCommand(data) {
    override fun run(message: Message) {
        if (args.isNotEmpty()) {
            sendEmbed(args[0])
        } else {
            sendEmbed()
        }
    }

    fun sendEmbed() {
        "https://icanhazdadjoke.com/".httpGet().header("Accept" to "application/json")
                .header("User-Agent" to "Kotomi (https://github.com/shindouj/KotomiBot)")
                .responseObject<DadJoke> { _, _, result ->
                    val (data, error) = result
                    if (error == null && data != null) {
                        destinationChannel.sendMessage(buildEmbed(data)).queue()
                    } else {
                        destinationChannel.sendMessage(getLocalized("fetchRandomFailed")).queue()
                    }
                }
    }

    fun sendEmbed(id: String) {
        "https://icanhazdadjoke.com/j/$id".httpGet().header("Accept" to "application/json")
                .header("User-Agent" to "Kotomi (https://github.com/shindouj/KotomiBot)")
                .responseObject<DadJoke> { _, _, result ->
                    val (data, error) = result
                    if (error == null && data != null) {
                        destinationChannel.sendMessage(buildEmbed(data)).queue()
                    } else {
                        destinationChannel.sendMessage(getLocalized("fetchIDFailed")).queue()
                    }
                }
    }

    fun buildEmbed(data: DadJoke): MessageEmbed =
        EmbedBuilder().setDescription(data.joke)
                .setAuthor(getLocalized("providerName"), getLocalized("providerURL"), getLocalized("iconURL"))
                .setFooter("ID: " + data.id, null)
                .setColor(Color.ORANGE)
                .build()

}