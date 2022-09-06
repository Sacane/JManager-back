package fr.sacane.jmanager.infra.server.adapters

import fr.sacane.jmanager.domain.model.*
import fr.sacane.jmanager.domain.port.serverside.ServerPort
import fr.sacane.jmanager.infra.server.entity.AccountResource
import fr.sacane.jmanager.infra.server.entity.SheetResource
import fr.sacane.jmanager.infra.server.entity.UserResource
import fr.sacane.jmanager.infra.server.repositories.UserRepository
import org.springframework.stereotype.Service
import java.time.Month

@Service
class ServerAdapter(val userRepository: UserRepository) : ServerPort{

    private fun SheetResource.toModel(): Sheet{
        return Sheet(this.label!!, this.date!!, this.amount!!, this.isEntry!!)
    }

    private fun AccountResource.toModel(): Account{
        return Account(this.amount!!, this.label!!, this.sheets?.map { sheet -> sheet.toModel() }!!.toMutableList())
    }

    private fun UserResource.toModel(): User{
        return User(UserId(this.id_user!!), this.username!!, this.email!!, this.pseudonym!!, this.accounts!!.map { account -> account.toModel() }.toMutableList(), Password(this.password!!))
    }

    private fun User.asResource(): UserResource{
        val resource = UserResource()
        resource.pseudonym = this.pseudonym
        resource.password = this.password.get()
        resource.email = this.email
        resource.username = this.username
        return resource
    }

    override suspend fun getSheets(user: UserId, accountLabel: String): List<Sheet> {

        val userResource = userRepository.findById(user.get()).get()
        return userResource.accounts
            ?.find { account -> account.label == accountLabel }
            ?.sheets!!
            .map { sheetResource -> Sheet(sheetResource.label!!, sheetResource.date!!, sheetResource.amount!!, sheetResource.isEntry!!) }
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
        return userRepository.findById(user.get()).get().accounts!!.map { resource -> resource.toModel() }
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

}