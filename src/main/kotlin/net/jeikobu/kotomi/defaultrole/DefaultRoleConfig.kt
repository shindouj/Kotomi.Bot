package net.jeikobu.kotomi.defaultrole

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Role
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException
import javax.sql.DataSource
import kotlin.Exception

class DefaultRoleConfig(dataSource: DataSource) {
    companion object {
        const val DEFAULT_ROLE_ENABLED_CONFIG_KEY = "defaultRoleEnabled"
    }

    private val db by lazy {
        Database.connect(dataSource)
    }

    object RoleToEvent : Table() {
        val roleID = long("roleID").primaryKey(0)
        val eventID = reference("eventID", DefaultRoleEvent.eventID)
    }

    object DefaultRoleEvent: Table() {
        val eventID = integer("eventID").autoIncrement().primaryKey(0)
        val guildID = long("guildID")
        val trigger = enumerationByName("trigger", 32, DefaultRoleTriggers::class).primaryKey(1)
    }

    fun register(trigger: DefaultRoleTriggers, role: Role, guild: Guild) {
        val eventID = try {
            getEvent(trigger, guild) ?: registerEvent(trigger, guild)
        } catch (e: SQLException) {
            registerEvent(trigger, guild)
        }

        val roles = try {
            getRoles(eventID)
        } catch (e: SQLException) {
            null
        }

        if (roles?.contains(role.idLong) == true) {
            throw DefaultRoleConfigException("Role already exists!")
        } else {
            registerRole(role, eventID)
        }
    }

    fun getRoles(trigger: DefaultRoleTriggers, guild: Guild): List<Role> {
        val event = getEvent(trigger, guild)
        val serverRoles: MutableList<Role> = mutableListOf()

        if (event != null) {
            val databaseRoles = getRoles(event)
            serverRoles.addAll(databaseRoles.mapNotNull { role -> guild.getRoleById(role) })

            return serverRoles
        }

        return listOf()
    }

    private fun getRoles(eventID: Int): List<Long> {
        return transaction(db) {
            create(RoleToEvent)
            create(DefaultRoleEvent)

            RoleToEvent.select { RoleToEvent.eventID eq eventID }.mapNotNull { it[RoleToEvent.roleID] }
        }
    }

    private fun getEvent(trigger: DefaultRoleTriggers, guild: Guild): Int? {
        return transaction(db) {
            create(RoleToEvent)
            create(DefaultRoleEvent)

            DefaultRoleEvent.select {
                (DefaultRoleEvent.guildID eq guild.idLong) and (DefaultRoleEvent.trigger eq trigger)
            }.firstOrNull()?.get(DefaultRoleEvent.eventID)
        }
    }

    private fun registerEvent(trigger: DefaultRoleTriggers, guild: Guild): Int {
        return transaction(db) {
            create(RoleToEvent)
            create(DefaultRoleEvent)

            DefaultRoleEvent.insertOrUpdate {
                it[guildID] = guild.idLong
                it[DefaultRoleEvent.trigger] = trigger
            }

            DefaultRoleEvent.select {
                (DefaultRoleEvent.guildID eq guild.idLong) and (DefaultRoleEvent.trigger eq trigger)
            }.first()[DefaultRoleEvent.eventID]
        }
    }

    private fun registerRole(role: Role, eventID: Int) {
        transaction(db) {
            create(RoleToEvent)
            create(DefaultRoleEvent)

            RoleToEvent.insert {
                it[roleID] = role.idLong
                it[RoleToEvent.eventID] = eventID
            }
        }
    }
}

class DefaultRoleConfigException(message: String) : Exception(message)

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