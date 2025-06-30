package ge.mino.androidmessengerapp

data class User(
    val nickname: String,
    val nicknameLowercase: String = "",
    val occupation: String,
    val imageUrl: String
){
    constructor() : this("", "", "", "")
}




