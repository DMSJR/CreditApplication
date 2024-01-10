package com.example.demo.service.impl

import com.example.demo.entity.Customer
import com.example.demo.exception.BusinessException
import com.example.demo.repository.CustomerRepository
import com.example.demo.service.ICustomerService
import org.springframework.stereotype.Service

@Service
class CustomerService(
        private val customerRepository: CustomerRepository
): ICustomerService {
    override fun save(customer: Customer): Customer = this.customerRepository.save(customer)


    override fun findById(id: Long): Customer = this.customerRepository.findById(id).orElseThrow{
        throw BusinessException("ID $id not found")
    }


    override fun delete(id: Long) {
        val customer: Customer = this.findById(id)
        this.customerRepository.delete(customer)
    }



}