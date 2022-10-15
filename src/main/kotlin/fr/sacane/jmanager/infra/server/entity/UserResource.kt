package fr.sacane.jmanager.infra.server.entity

import org.springframework.lang.Nullable
import javax.persistence.*


@Table(name="User")
@Entity
class UserResource(
    @Id
    @Nullable
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_user", nullable = false)
    open var id_user: Long? = null,

    @Column(unique = true, nullable = true)
    var pseudonym: String? = null,

    @Column(unique = true)
    var username: String? = null,
    var password: String? = null,

    @Column(unique = true, nullable = true)
    var email: String? = null,

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_account",
        joinColumns = [JoinColumn(name = "id_user")],
        inverseJoinColumns = [JoinColumn(name = "idAccount")]
    )
    var accounts: MutableList<AccountResource>?,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name="user_categories",
        joinColumns = [JoinColumn(name="id_user")],
        inverseJoinColumns = [JoinColumn(name="id_category")]
    )
    var categories: MutableList<CategoryResource>?
){
    constructor(
        pseudonym: String?,
        username: String?,
        password: String?,
        email: String?,
        accounts: MutableList<AccountResource>?,
        categories: MutableList<CategoryResource>?

    ) : this(null, pseudonym, username, password, email, accounts, categories)
}
