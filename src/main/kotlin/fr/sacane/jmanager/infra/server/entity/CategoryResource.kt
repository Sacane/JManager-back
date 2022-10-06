package fr.sacane.jmanager.infra.server.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class CategoryResource(
    @Id
    @GeneratedValue
    @Column(unique = true, name = "id_category", nullable = false)
    var idCategory: Long?,
    @Column(name="label", unique = true)
    var label: String?
){
    constructor(
        label: String?
    ): this(null, label)
}