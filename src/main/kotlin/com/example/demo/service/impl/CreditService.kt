package com.example.demo.service.impl

import com.example.demo.entity.Credit
import com.example.demo.exception.BusinessException
import com.example.demo.repository.CreditRepository
import com.example.demo.service.ICreditService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*


@Service
class CreditService(
        private val creditRepository: CreditRepository,
        private val customerService: CustomerService
): ICreditService {
    override fun save(credit: Credit): Credit {
        if (credit.numberOfInstallments > 48){
            throw BusinessException("Number of installments must be less than 48")
        }
        val date  = LocalDate.now()
        if (getDaysDif(date, credit.dayFirstInstallment ) > 90 ){
            throw BusinessException("Day of first installment must be less than 3 months from now")

        }
        credit.apply{
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.creditRepository.save(credit)
    }
    fun getDaysDif(fromDate: LocalDate, toDate: LocalDate): Long {
        return ChronoUnit.DAYS.between(fromDate, toDate)
    }
    override fun findAllByCustomer(customerID: Long): List<Credit> = this.creditRepository.findAllByCustomerID(customerID)

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
       val credit =  this.creditRepository.findByCreditCode(creditCode) ?:
       throw BusinessException("Creditcode $creditCode not found")
        return if (credit.customer?.id == customerId) credit else throw IllegalArgumentException("Contact admin")
    }
}