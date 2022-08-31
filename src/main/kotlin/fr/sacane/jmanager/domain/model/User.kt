package fr.sacane.jmanager.domain.model

data class User(
    val username: String,
    val email: String,
    val pseudonym: String,
    val accounts: MutableList<Account>?,
    val password: Password
)

class Password(private val value: String){

    private fun String.toDigit(code: Int) : Int = this.map { it.code + code }.sum()

    fun get(): String{
        val builder = StringBuilder()
        var c: Char
        value.forEach {
            char ->
            c = char
            var res = c.code.toLong()
            res += value.toInt()
            res *= value.length
            res += value.toDigit(value.length)
            res %= 255
            c = res.toInt().toChar()
            builder.append(c)
        }
        return builder.toString()
    }
}
