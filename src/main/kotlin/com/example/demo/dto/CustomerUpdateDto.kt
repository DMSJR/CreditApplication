package com.example.demo.dto

import com.example.demo.entity.Customer
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CustomerUpdateDto(
    @field:NotEmpty(message = "Invalid input") val firstName: String,
    @field:NotEmpty(message = "Invalid input")   val lastName: String,
    @field:NotNull val income: BigDecimal,
    @field:NotEmpty(message = "Invalid input")  val zipCode: String,
    @field:NotEmpty(message = "Invalid input")  val street: String
){
    fun toEntity(customer: Customer):Customer{
        customer.firstName = this.firstName
        customer.lastName = this.lastName
        customer.income = this.income
        customer.adress.zipCode = this.zipCode
        customer.adress.street = this.street

        return customer
    }
}
