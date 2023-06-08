package com.example.javaexample.domain;

import java.util.List;
import java.util.Optional;

public interface CustomerDAO {

    List<Customer> getAllCustomers();
    Optional<Customer> getCustomerById(Long id);
    void insertCustomer(Customer customer);
    boolean existPersonWithEmail(String email);
    void deleteCustomer(Customer customer);
    void updateCustomer(Customer customer);

}
