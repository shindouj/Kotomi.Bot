package net.jeikobu.kotomi.reactionroles

import net.dv8tion.jda.core.entities.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.Exception
import javax.sql.DataSource

class ReactionConfig(dataSource: DataSource) {
    private val db by lazy {
        Database.connect(dataSource)
    }

    object ReactionMessage : Table() {
        val messageID = long("messageID").primaryKey(0)
        val setting = enumerationByName("setting", 32, ReactionMessageTypes::class)
    }

    object RoleToReactionMessage : Table() {
        val message = reference("reactionMessage", ReactionMessage.messageID).primaryKey(0)
        val role = long("roleID").primaryKey(1)
        val reactionEmojiID = reference("emoteID", Emoji.emojiID)
    }

    object Emoji : Table() {
        val emojiID = integer("emojiID").autoIncrement().primaryKey()
        val discordEmoteID = long("discordEmoteID").nullable()
        val emojiName = varchar("emojiName", 64)

        init {
            uniqueIndex(discordEmoteID, emojiName)
        }
    }

    fun registerMessage(message: Message, setting: ReactionMessageTypes) {
        transaction(db) {
            create(ReactionMessage)

            ReactionMessage.insertOrUpdate(ReactionMessage.setting) {
                it[ReactionMessage.messageID] = message.idLong
                it[ReactionMessage.setting] = setting
            }
        }
    }

    fun isMessageRegistered(message: Message): Boolean {
        return transaction(db) {
            create(ReactionMessage)
            ReactionMessage.select { ReactionMessage.messageID eq message.idLong }.count() > 0
        }
    }

    fun getRole(message: Message, reaction: MessageReaction, guild: Guild): Role {
        return transaction(db) {
            create(ReactionMessage)
            create(RoleToReactionMessage)

            val emojiID = if (reaction.reactionEmote.isEmote) {
                Emoji.select { Emoji.discordEmoteID eq reaction.reactionEmote.idLong }.first()[Emoji.emojiID]
            } else {
                Emoji.select { Emoji.emojiName eq reaction.reactionEmote.name }.first()[Emoji.emojiID]
            }

            guild.getRoleById(RoleToReactionMessage.select {
                (RoleToReactionMessage.message eq message.idLong) and (RoleToReactionMessage.reactionEmojiID eq emojiID)
            }.first()[RoleToReactionMessage.role])
        }
    }

    fun getMode(message: Message): ReactionMessageTypes {
        return transaction(db) {
            create(ReactionMessage)
            ReactionMessage.select{ ReactionMessage.messageID eq message.idLong }.first()[ReactionMessage.setting]
        }
    }

    fun addReactionRole(message: Message, role: Role, emoji: Emote) {
        val emojiID = try {
            addEmoji(emoji)
        } catch (e: Exception) {
            getEmojiID(emoji)
        }
        addReactionRoleImpl(message, role, emojiID)
    }

    fun addReactionRole(message: Message, role: Role, emoteName: String) {
        val emojiID = try {
            addEmoji(emoteName)
        } catch (e: Exception) {
            getEmojiID(emoteName)
        }
        addReactionRoleImpl(message, role, emojiID)
    }

    private fun addReactionRoleImpl(message: Message, role: Role, emojiID: Int) {
        transaction(db) {
            create(ReactionMessage)
            create(RoleToReactionMessage)

            if (this@ReactionConfig.isMessageRegistered(message)) {
                return@transaction RoleToReactionMessage.insertOrUpdate(RoleToReactionMessage.reactionEmojiID) {
                    it[RoleToReactionMessage.message] = message.idLong
                    it[RoleToReactionMessage.role] = role.idLong
                    it[RoleToReactionMessage.reactionEmojiID] = emojiID
                }
            } else {
                throw ReactionConfigException("Message not registered!")
            }
        }
    }

    fun getEmojiID(emote: Emote): Int {
        return transaction(db) {
            Emoji.select { Emoji.discordEmoteID eq emote.idLong }.first()[Emoji.emojiID]
        }
    }

    fun getEmojiID(name: String): Int {
        return transaction(db) {
            Emoji.select { Emoji.emojiName eq name }.first()[Emoji.emojiID]
        }
    }

    fun addEmoji(emote: Emote): Int {
        return transaction(db) {
            create(Emoji)
            Emoji.insertOrUpdate {
                it[discordEmoteID] = emote.idLong
                it[emojiName] = emote.name
            }

            Emoji.select { Emoji.discordEmoteID eq emote.idLong }.first()[Emoji.emojiID]
        }
    }

    fun addEmoji(name: String): Int {
        return transaction(db) {
            create(Emoji)
            Emoji.insertOrUpdate {
                it[discordEmoteID] = null
                it[emojiName] = name
            }

            Emoji.select { Emoji.emojiName eq name }.first()[Emoji.emojiID]
        }
    }
}

class ReactionConfigException(message: String?) : Exception(message)

fun <T : Table> T.insertOrUpdate(vararg onDuplicateUpdateKeys: Column<*>,
                                 body: T.(InsertStatement<Number>) -> Unit) = InsertOrUpdate<Number>(onDuplicateUpdateKeys, this).apply {
    body(this)
    execute(TransactionManager.current())
}

class InsertOrUpdate<Key : Any>(private val onDuplicateUpdateKeys: Array<out Column<*>>, table: Table,
                                isIgnore: Boolean = false) : InsertStatement<Key>(table, isIgnore) {
    override fun prepareSQL(transaction: Transaction): String {
        val onUpdateSQL = if (onDuplicateUpdateKeys.isNotEmpty()) {
            " ON DUPLICATE KEY UPDATE " + onDuplicateUpdateKeys.joinToString { "${transaction.identity(it)}=VALUES(${transaction.identity(it)})" }
        } else ""
        return super.prepareSQL(transaction) + onUpdateSQL
    }
}