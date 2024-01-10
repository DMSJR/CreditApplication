package com.example.demo.service

import com.example.demo.entity.Address
import com.example.demo.entity.Credit
import com.example.demo.entity.Customer
import com.example.demo.enummeration.Status
import com.example.demo.exception.BusinessException
import com.example.demo.repository.CreditRepository
import com.example.demo.repository.CustomerRepository
import com.example.demo.service.impl.CreditService
import com.example.demo.service.impl.CustomerService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import jakarta.persistence.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK lateinit var creditRepository: CreditRepository
    @InjectMockKs lateinit var creditService: CreditService
    @MockK lateinit var customerService: CustomerService
    @MockK lateinit var customerRepository: CustomerRepository
    @Test
    fun `should create credit`(){
        //given
        val fakeCustomer: Customer = buildCustomer()
        val fakeCredit: Credit = buildCredit(customer = fakeCustomer)


        every{ creditRepository.save(any())} returns fakeCredit
        every{ customerRepository.save(any())} returns fakeCustomer
        every{ customerService.save(any())} returns fakeCustomer
        every{ customerService.findById(any())} returns fakeCustomer


        //when
        val actual: Credit = creditService.save(fakeCredit)
        val current: Customer = customerService.save(fakeCustomer)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        verify(exactly = 1){ creditRepository.save(fakeCredit)  }
    }

    @Test
    fun `should throw number of installments business exception`(){
        //given
        var fakeCustomer = buildCustomer()
        val invalidNumberOfInstallments = 49
        val credit: Credit = buildCredit(customer = fakeCustomer, numberOfInstallments = invalidNumberOfInstallments)

        every { creditRepository.save(credit) } answers { credit }
        //when
        Assertions.assertThatThrownBy { creditService.save(credit) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Number of installments must be less than 48")
        //then
        verify(exactly = 0) { creditRepository.save(any()) }
    }

    fun `should throw first installment business exception`(){
        //given
        var fakeCustomer = buildCustomer()
        val invalidDayFirstInstallment: LocalDate = LocalDate.now().plusMonths(5)
        val credit: Credit = buildCredit(customer = fakeCustomer, dayFirstInstallment = invalidDayFirstInstallment)

        every { creditRepository.save(credit) } answers { credit }
        //when
        Assertions.assertThatThrownBy { creditService.save(credit) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Day of first installment must be less than 3 months from now")
        //then
        verify(exactly = 0) { creditRepository.save(any()) }}
    @Test
    fun `should find all by id`(){
        //given
        val fakeCustomer: Customer = buildCustomer()

        val fakeCredit1: Credit = buildCredit(customer = fakeCustomer)
        val fakeCredit2: Credit = buildCredit(customer = fakeCustomer)
        val fakeCreditList: List<Credit> = listOf(fakeCredit1, fakeCredit2)
        every{ creditService.findAllByCustomer(fakeCustomer.id!!)} returns listOf(fakeCredit1, fakeCredit2)
        every{ customerService.findById(any())} returns fakeCustomer
        //when
        val actualList: List<Credit> = creditService.findAllByCustomer(fakeCustomer.id!!)
        //then
        Assertions.assertThat(actualList).isNotNull
        Assertions.assertThat(actualList).isNotEmpty
        Assertions.assertThat(actualList).isEqualTo(fakeCreditList)
        verify(exactly = 1) { creditService.findAllByCustomer(fakeCustomer.id!!)}
    }

    @Test
    fun `should not find by customerId`(){


        val fakeCustomerId = Random().nextLong()

        every{ creditService.findAllByCustomer(any())} returns listOf()

        //when

        val actualList: List<Credit> = creditService.findAllByCustomer(fakeCustomerId)
        //then
        Assertions.assertThat(actualList).isNotNull
        Assertions.assertThat(actualList).isEmpty()
        verify(exactly = 1) { creditService.findAllByCustomer(fakeCustomerId)}
    }
    @Test
    fun `should find credit by creditId`(){
        //given
        val customerId: Long = 1L
        val creditCode: UUID = UUID.randomUUID()
        val fakeCredit: Credit = buildCredit(customer = Customer(id = customerId))

        every{ creditRepository.findByCreditCode(creditCode)} returns fakeCredit

        //when

        val foundCredit: Credit = creditService.findByCreditCode(customerId, creditCode)
        //then
        Assertions.assertThat(foundCredit).isNotNull
        Assertions.assertThat(foundCredit).isSameAs(fakeCredit)
        verify(exactly = 1) { creditRepository.findByCreditCode( creditCode)}
    }

    @Test
    fun `creditService should throw Illegal Argument Exception`(){
        val fakeCustomer: Customer = buildCustomer()
        val fakeCustomer2: Customer = buildCustomer()
        val fakeCredit: Credit = buildCredit(customer = fakeCustomer)
        every{ customerService.findById(any())} returns fakeCustomer
        every {
            creditRepository.findByCreditCode(any())
        } throws IllegalArgumentException("Contact admin")
        //when
        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { creditService.findByCreditCode(fakeCustomer2.id!!, fakeCredit.creditCode) }
            .withMessage("Contact admin")
    }

    @Test
    fun `Credit service should not find credit`(){
        val fakeCustomer: Customer = buildCustomer()
        val fakeCreditCode = UUID.randomUUID()
        every{ customerService.findById(any())} returns fakeCustomer
        every {
            creditRepository.findByCreditCode(any())
        } throws BusinessException("Creditcode ${fakeCreditCode} not found")
        //when

        //given
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.findByCreditCode(fakeCustomer.id!!, fakeCreditCode) }
            .withMessage("Creditcode ${fakeCreditCode} not found")
        verify(exactly = 1) { creditRepository.findByCreditCode(fakeCreditCode) }

    }


    private fun buildCustomer (
        firstName: String = "Dori" ,
        lastName: String = "Silva",
        cpf: String = "39544008802",
        email: String = "marques.dsjr@gmail.com",
        password: String = "123456",
        zipCode: String = "15505160",
        street: String = "Rua Sergipe",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        id: Long = Random().nextLong()

    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        adress = Address(
            zipCode = zipCode,
            street = street
        ),
        income = income,
        id = id
    )

    private fun buildCredit(
        creditCode: UUID = UUID.randomUUID(),
        creditValue: BigDecimal = BigDecimal.valueOf(500.0),
        dayFirstInstallment: LocalDate = LocalDate.of(2024, 2, 1),
        numberOfInstallments: Int = 10,
        status: Status = Status.IN_PROGRESS,
        customer: Customer? ,
        id: Long? = 1L
    ) = Credit(
        creditCode = creditCode,
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        status = status,
        customer = customer,
        id = id
    )


}


