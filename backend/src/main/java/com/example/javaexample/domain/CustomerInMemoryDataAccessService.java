package com.example.javaexample.domain;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("inMemoryRepository")
public class CustomerInMemoryDataAccessService implements CustomerDAO{

    private static List<Customer> customers;

    static {
        customers = new ArrayList<>();

        Customer alex = new Customer(
                1L,
                "Alex",
                "alex@gmail.com",
                21
        );

        Customer jasmine = new Customer(
                2L,
                "Jasmine",
                "jasmine@gmail.com",
                19
        );

        customers.add(alex);
        customers.add(jasmine);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return customers.stream()
                .filter(customer -> customer.getId().equals(id))
                .findFirst();

    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public boolean existPersonWithEmail(String email) {
        return customers.stream().anyMatch(customer -> customer.getEmail().equals(email));
    }

    @Override
    public void deleteCustomer(Customer customer) {
        customers.remove(customer);
    }

    @Override
    public void updateCustomer(Customer customer) {
        customers.add(customer);
    }


}
