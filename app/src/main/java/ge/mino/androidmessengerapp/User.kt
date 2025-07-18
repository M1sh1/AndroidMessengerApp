package ge.mino.androidmessengerapp

data class User(
    val uid: String,
    val nickname: String,
    val nicknameLowercase: String = "",
    val occupation: String,
    val profileImageUrl: String,
    var lastMessage: String? = null
) {
    constructor() : this("", "", "", "", "", null)
}



