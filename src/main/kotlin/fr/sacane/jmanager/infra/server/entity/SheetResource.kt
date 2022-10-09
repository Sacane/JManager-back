package fr.sacane.jmanager.infra.server.entity

import java.time.LocalDate
import javax.persistence.*


@Entity
@Table(name="sheet")
class SheetResource(
    @Id
    @GeneratedValue
    @Column(unique = true, name = "id_sheet", nullable = false)
    var idSheet: Long? = null,
    @Column(unique = true, name = "label_sheet")
    var label: String? = null,
    @Column(name= "amount", unique = false)
    var amount: Double? = null,
    @Column(name="date")
    var date: LocalDate? = null,
    @Column(name="isEntry")
    var isEntry: Boolean? = null,
    @OneToOne
    @JoinTable(
        name = "sheet_category",
        joinColumns = [JoinColumn(name = "id_sheet")],
        inverseJoinColumns = [JoinColumn(name = "id_category")]
    )
    var category: CategoryResource?=null

){
    constructor(
        label: String?,
        amount: Double?,
        date: LocalDate?,
        isEntry: Boolean?,
        category: CategoryResource?
    ): this(null, label, amount, date, isEntry, category)
}
