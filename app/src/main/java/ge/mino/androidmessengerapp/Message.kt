package ge.mino.androidmessengerapp

data class Message(
    val sender: String = "",
    val receiver: String = "",
    val id: String = "",
    val message: String = "",
    val timestamp: Long = 0
) {
    constructor() : this("", "", "", "", 0)
}