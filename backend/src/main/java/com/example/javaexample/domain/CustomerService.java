package com.example.javaexample.domain;

import com.example.javaexample.exception.DuplicateResourceException;
import com.example.javaexample.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService  {

    private final CustomerDAO customerDAO;

    public CustomerService(@Qualifier("jdbcTemplateRepository") CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    public Customer getCustomerById(Long id) {
        return customerDAO.getCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Customer with id %s not found", id)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        //check if email is used
        if(customerDAO.existPersonWithEmail(customerRegistrationRequest.email())) {
            throw new DuplicateResourceException(String.format("Customer with email %s already exist", customerRegistrationRequest.email()));
        }
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );

        customerDAO.insertCustomer(customer);
    }

    public void deleteCustomer(Long id) {
        Optional<Customer> optionalCustomer = customerDAO.getCustomerById(id);
        if(optionalCustomer.isEmpty()) {
            throw new ResourceNotFoundException("Customer with id %s not found".formatted(id));
        }
        customerDAO.deleteCustomer(optionalCustomer.get());
    }

    public void updateCustomer(Long id, CustomerUpdateRequest request) {
        Optional<Customer> optionalCustomer = customerDAO.getCustomerById(id);
        if(optionalCustomer.isEmpty()) {
            throw new ResourceNotFoundException("Customer with id %s not found".formatted(id));
        }
        Customer customer = optionalCustomer.get();

        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setAge(request.age());

        customerDAO.updateCustomer(customer);

    }
}
