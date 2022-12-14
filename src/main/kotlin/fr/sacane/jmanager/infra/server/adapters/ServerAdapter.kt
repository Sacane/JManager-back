package fr.sacane.jmanager.infra.server.adapters

import com.toxicbakery.bcrypt.Bcrypt
import fr.sacane.jmanager.common.Hash
import fr.sacane.jmanager.domain.model.*
import fr.sacane.jmanager.domain.port.serverside.TransactionRegister
import fr.sacane.jmanager.infra.server.entity.AccountResource
import fr.sacane.jmanager.infra.server.entity.SheetResource
import fr.sacane.jmanager.infra.server.entity.UserResource
import fr.sacane.jmanager.infra.server.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Month

@Service
class ServerAdapter() : TransactionRegister{

    companion object{
        private val logger = LoggerFactory.getLogger("ServerAdapter")
    }

    @Autowired
    private lateinit var userRepository: UserRepository
    private fun SheetResource.toModel(): Sheet{
        return Sheet(this.idSheet!!, this.label!!, this.date!!, this.amount!!, this.isEntry!!)
    }

    private fun AccountResource.toModel(): Account{
        return Account(this.idAccount!!, this.amount!!, this.label!!, this.sheets?.map { sheet -> sheet.toModel() }!!.toMutableList())
    }

    private fun UserResource.toModel(): User{
        return User(UserId(this.id_user!!), this.username!!, this.email!!, this.pseudonym!!, this.accounts!!.map { account -> account.toModel() }.toMutableList(), Password(this.password!!.toString()))
    }

    private fun User.asResource(): UserResource {
        return UserResource(null, pseudonym, username, password.get(), email, mutableListOf())
    }

    override suspend fun getSheets(user: UserId, accountLabel: String): List<Sheet> {

        val userResource = userRepository.findById(user.get()).get()
        return userResource.accounts
            ?.find { account -> account.label == accountLabel }
            ?.sheets!!
            .map { sheetResource -> Sheet(sheetResource.idSheet!!, sheetResource.label!!, sheetResource.date!!, sheetResource.amount!!, sheetResource.isEntry!!) }
            .toList()
    }

    override suspend fun getSheetsByDateAndAccount(
        userId: UserId,
        month: Month,
        year: Int,
        labelAccount: String
    ): List<Sheet> {
        val sheets = getSheets(userId, labelAccount)
        return sheets.filter { s -> s.date.month == month && s.date.year == year }
    }

    override suspend fun getAccounts(user: UserId): List<Account> {
        logger.debug("Trying to reach accounts of user ${user.get()}")
        val accs = userRepository.findById(user.get())
        .get().accounts!!.map { resource -> resource.toModel() }
        println(accs)
        return accs
    }

    override suspend fun saveUser(user: User): User {
        userRepository.save(user.asResource())

        return user
    }

    override suspend fun findUserById(userId: UserId): User {
        return userRepository.findById(userId.get()).get().toModel()
    }

    private fun Sheet.asResource(): SheetResource{
        val resource = SheetResource()
        resource.isEntry = this.isEntry
        resource.label = this.label
        resource.date = this.date
        resource.amount = this.value
        return resource
    }

    private fun Account.asResource(): AccountResource{
        val resource = AccountResource()
        resource.amount = this.amount()
        resource.label = this.label()
        if(this.sheets().isNullOrEmpty()){
            resource.sheets = null
        }else {
            resource.sheets = sheets()?.toMutableList()?.map { model -> model.asResource() }?.toMutableList()
        }
        return resource
    }


    override suspend fun saveAccount(userId: UserId, account: Account) {
        val user = userRepository.findById(userId.get()).get()

        user.accounts?.add(account.asResource())
        userRepository.save(user)

    }



    override suspend fun findUserByPseudonym(pseudonym: String): User? {
        return userRepository.findByPseudonym(pseudonym)?.toModel()
    }

    override suspend fun createUser(user: User): User? {
        return try {
            println("register : ${user.password.value}")
            val entity = userRepository.save(user.asResource())

            entity.toModel()
        }catch (e: IllegalArgumentException){
            null
        }
    }

    override suspend fun saveSheet(userId: UserId, accountLabel: String, sheet: Sheet): Boolean {
        val user = userRepository.findById(userId.get()).get()
        val account = user.accounts?.find { it.label == accountLabel }
        if(account != null){
            return try{
                account.sheets?.add(sheet.asResource())
                account.amount = if(sheet.isEntry) account.amount?.plus(sheet.value) else account.amount?.minus(sheet.value)
                userRepository.save(user)
                true
            }catch(iae: IllegalArgumentException){
                false
            }
        }
        return false
    }


    override suspend fun checkUser(pseudonym: String, pwd: Password): Boolean{
        val user = userRepository.findByPseudonym(pseudonym)
        return pwd.get() == user?.password
    }

}
