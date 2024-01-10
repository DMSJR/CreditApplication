package com.example.demo.service

import com.example.demo.entity.Customer

interface ICustomerService {
    fun save (customer: Customer): Customer
    fun findById(id: Long) : Customer
    fun delete (id: Long)
}