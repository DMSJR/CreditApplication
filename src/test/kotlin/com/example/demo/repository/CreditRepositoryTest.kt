package com.example.demo.repository

import com.example.demo.entity.Address
import com.example.demo.entity.Credit
import com.example.demo.entity.Customer
import com.example.demo.enummeration.Status
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

class CreditRepositoryTest {
    @Autowired lateinit var creditRepository: CreditRepository
    @Autowired lateinit var testEntityManager: TestEntityManager

    private lateinit var customer: Customer
    private lateinit var credit1: Credit
    private lateinit var credit2: Credit

    @BeforeEach
    fun setup(){
        customer = testEntityManager.persist(buildCustomer())
        credit1 = testEntityManager.persist(buildCredit(customer = customer))
        credit2 = testEntityManager.persist(buildCredit(customer = customer))


    }

    @Test
    fun `should find credit by credit code`(){
        //given
        val creditCode1 = UUID.fromString("7f8b5b05-85f9-4d63-856e-93cc365c3af7")
        val creditCode2 = UUID.fromString("a772d137-796c-4c56-807a-f219c7bbce9b")
        credit1.creditCode = creditCode1
        credit2.creditCode = creditCode2

        //when
        val fakeCredit1: Credit = creditRepository.findByCreditCode(creditCode1)!!
        val fakeCredit2: Credit = creditRepository.findByCreditCode(creditCode2)!!
        //then
        Assertions.assertThat(fakeCredit1).isNotNull
        Assertions.assertThat(fakeCredit2).isNotNull

    }
    @Test
    fun `should find all credits by customer Id`(){
        //given
        val customerId = 1L

        //when
        val creditList: List<Credit> = creditRepository.findAllByCustomerID(customerId)


        //then
        Assertions.assertThat(creditList).isNotEmpty
        Assertions.assertThat(creditList.size).isEqualTo(2)
        Assertions.assertThat(creditList).contains(credit1, credit2)
    }

    private fun buildCustomer (
        firstName: String = "Dori",
        lastName: String = "Silva",
        cpf: String = "39544008802",
        email: String = "marques.dsjr@gmail.com",
        password: String = "123456",
        zipCode: String = "15505160",
        street: String = "Rua Sergipe",
        income: BigDecimal = BigDecimal.valueOf(1000.0),


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
    )

    private fun buildCredit(
        creditValue: BigDecimal = BigDecimal.valueOf(500.0),
        dayFirstInstallment: LocalDate = LocalDate.of(2024, 2, 1),
        numberOfInstallments: Int = 10,
        status: Status = Status.IN_PROGRESS,
        customer: Customer,
    ) = Credit(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        status = status,
        customer = customer
    )
}
