package net.jeikobu.kotomi.commands

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.serialization.responseObject
import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.EmbedBuilder
import java.awt.Color

@Command(name = "dadJoke", argsLength = 0)
class DadJokeCommand(data: CommandData?) : AbstractCommand(data) {
    override fun run(message: IMessage?) {
        if (args.size > 0 && args[0] != null) {
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
                        val embed = EmbedBuilder().withDesc(data.joke)
                                .withAuthorName(getLocalized("providerName"))
                                .withAuthorIcon(getLocalized("iconURL"))
                                .withAuthorUrl(getLocalized("providerURL"))
                                .withFooterText("ID: " + data.id)
                                .withColor(Color.ORANGE)
                                .build()
                        destinationChannel.sendMessage(embed)
                    } else {
                        destinationChannel.sendMessage(getLocalized("fetchRandomFailed"))
                    }
                }
    }

    fun sendEmbed(id: String) {
        "https://icanhazdadjoke.com/j/$id".httpGet().header("Accept" to "application/json")
                .header("User-Agent" to "Kotomi (https://github.com/shindouj/KotomiBot)")
                .responseObject<DadJoke> { _, _, result ->
                    val (data, error) = result
                    if (error == null && data != null) {
                        val embed = EmbedBuilder().withDesc(data.joke)
                                .withAuthorName(getLocalized("providerName"))
                                .withAuthorIcon(getLocalized("iconURL"))
                                .withAuthorUrl(getLocalized("providerURL"))
                                .withFooterText("ID: " + data.id)
                                .withColor(Color.ORANGE)
                                .build()
                        destinationChannel.sendMessage(embed)
                    } else {
                        destinationChannel.sendMessage(getLocalized("fetchIDFailed"))
                    }
                }
    }
}