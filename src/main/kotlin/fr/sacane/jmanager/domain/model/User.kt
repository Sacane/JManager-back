package fr.sacane.jmanager.domain.model

class UserId(private val id: Long){
    fun get(): Long = id
}

class Password(private val value: String){

    private fun String.toDigit(code: Int) : Int = this.map { it.code + code }.sum()

    fun get(): String{
        val builder = StringBuilder()
        var c: Char
        value.forEach {
            char ->
            c = char
            var res = c.code.toLong()
            res *= value.length
            res += value.toDigit(value.length)
            res %= 255
            c = res.toInt().toChar()
            builder.append(c)
        }
        return builder.toString()
    }
}

class User(
        private val id: UserId,
        private val username: String,
        private val email: String,
        private val pseudonym: String,
        private val accounts: MutableList<Account>,
        private val password: Password,
){
    fun doesPwdMatch(pwd: String): Boolean = pwd == password.get()

    fun accounts(): List<Account> = accounts.distinct()

}
