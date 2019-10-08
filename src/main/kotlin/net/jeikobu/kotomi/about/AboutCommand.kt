package net.jeikobu.kotomi.about

import net.dv8tion.jda.core.entities.Message
import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData

@Command(name = "about", argsLength = 0)
class AboutCommand(data: CommandData) : AbstractCommand(data) {
    override fun run(message: Message) {
        destinationChannel.sendMessage(getLocalized("about")).queue()
    }
}