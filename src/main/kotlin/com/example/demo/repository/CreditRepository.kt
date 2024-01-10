package com.example.demo.repository

import com.example.demo.entity.Credit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*
import kotlin.collections.List

@Repository
interface CreditRepository: JpaRepository<Credit, Long> {

fun findByCreditCode(creditCode: UUID) : Credit?

@Query(value = "SELECT * FROM CREDIT WHERE CUSTOMER_ID = ?1", nativeQuery = true)
fun findAllByCustomerID(customerID: Long): List <Credit>
}