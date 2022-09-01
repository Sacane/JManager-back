package fr.sacane.jmanager.domain

import fr.sacane.jmanager.domain.model.Account
import fr.sacane.jmanager.domain.model.Password
import fr.sacane.jmanager.domain.model.User
import fr.sacane.jmanager.domain.model.UserId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    fun `password should be encrypt`(){
        val pwd = Password("password")
        assertThat(pwd.get()).isNotEqualTo("password")
    }

    @Test
    fun `password should match`(){
        val pwd = Password("password")
        val pwd2 = Password("password")
        assertThat(pwd.get()).isEqualTo(pwd2.get())
    }

    @Test
    fun `password should not match even with uppercase`(){
        val pwd = Password("password")
        val pwd2 = Password("PAsswoRD")
        assertThat(pwd.get()).isNotEqualTo(pwd2.get())
    }

    @Test
    fun `user pwd should match with same`(){
        val pwd = Password("D5301012000MAMacita")
        val pwdUser = Password("D5301012000MAMacita")

        val user = User(UserId(1), "johan", "johan.test@test.fr", "tester", mutableListOf(), pwdUser)

        assertThat(user.doesPwdMatch(pwd.get())).isTrue
    }

    @Test
    fun `user pwd should not match`(){
        val pwd = Password("D5301012000MAMaCitA")
        val pwdUser = Password("D5301012000MAMacita")
        val user = User(UserId(1), "johan", "johan.test@test.fr", "tester", mutableListOf(), pwdUser)
        assertThat(user.doesPwdMatch(pwd.get())).isFalse
    }

    @Test
    fun `the user's accounts should not contains the same value more than once`(){
        val constantValue = 102.toDouble()
        val accounts = mutableListOf(
                Account(constantValue, "test", mutableListOf()),
                Account(constantValue, "Courant", mutableListOf()),
                Account(constantValue, "test", mutableListOf()),
                Account(constantValue, "Secondaire", mutableListOf())
        )

        val pwdUser = Password("D5301012000MAMacita")
        val user = User(UserId(1), "johan", "johan.test@test.fr", "tester", accounts, pwdUser)
        assertThat(user.accounts()).containsOnlyOnce(Account(constantValue, "test", mutableListOf()))
    }

}
