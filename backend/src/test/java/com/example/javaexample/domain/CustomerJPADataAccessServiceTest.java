package com.example.javaexample.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock
    private CustomerRepository customerRepository;


    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void getAllCustomers() {
        //When
        underTest.getAllCustomers();

        //Then
        verify(customerRepository).findAll();
    }

    @Test
    void getCustomerById() {
        //Given
        Long id = 1L;

        //When
        underTest.getCustomerById(id);

        //Then
        verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        //Given
        Customer customer = new Customer("Brice Adrien", "brice.adrien@gmail.com", 36);

        //When
        underTest.insertCustomer(customer);

        //Then
        verify(customerRepository).save(customer);
    }

    @Test
    void existPersonWithEmail() {
        //Given
        String email = "brice.mbiandji@gmail.com";

        //When
        underTest.existPersonWithEmail(email);

        //Then
        verify(customerRepository).existsByEmail(email);
    }

    @Test
    void deleteCustomer() {
        //Given
        Customer customer = new Customer("Brice Adrien", "brice.adrien@gmail.com", 36);

        //When
        underTest.deleteCustomer(customer);

        //Then
        verify(customerRepository).delete(customer);
    }

    @Test
    void updateCustomer() {
        //Given
        Customer customer = new Customer("Brice Adrien", "brice.adrien@gmail.com", 36);

        //When
        underTest.updateCustomer(customer);

        //Then
        verify(customerRepository).save(customer);
    }
}