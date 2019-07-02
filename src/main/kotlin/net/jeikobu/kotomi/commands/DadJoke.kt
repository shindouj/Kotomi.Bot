package net.jeikobu.kotomi.commands

import kotlinx.serialization.Serializable

@Serializable
data class DadJoke(val id: String = "", val joke: String = "", val status: Int = 404)