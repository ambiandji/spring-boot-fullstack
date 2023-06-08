package com.example.javaexample.domain;

import com.example.javaexample.exception.DuplicateResourceException;
import com.example.javaexample.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDAO customerDAO;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDAO);
    }

    @Test
    void getAllCustomers() {
        //When
        underTest.getAllCustomers();
        //Then
        verify(customerDAO).getAllCustomers();
    }

    @Test
    void canGetCustomer() {
        //Given
        Long id = 1L;
        Customer customer = new Customer(id,"Brice Adrien", "brice.mbiandji@gmail", 36);
        when(customerDAO.getCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        Customer result = underTest.getCustomerById(id);

        //Then
        verify(customerDAO).getCustomerById(id);
        assertThat(result)
                .isNotNull()
                .isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        //Given
        Long id = 1L;
        when(customerDAO.getCustomerById(id)).thenReturn(Optional.empty());

        //When
        //Then
        assertThatThrownBy(() -> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("Customer with id %s not found".formatted(id));
        verify(customerDAO).getCustomerById(id);
    }

    @Test
    void addCustomer() {
        //Given
        String email = "brice.miandji@gmail.com";
        when(customerDAO.existPersonWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Brice Adrien", email, 36
        );

        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());

    }

    @Test
    void willThrowWhenEmailExistsWhileAddingCustomer() {
        //Given
        String email = "brice.miandji@gmail.com";
        when(customerDAO.existPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Brice Adrien", email, 36
        );

        //When
        //Then
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Customer with email %s already exist".formatted(email));

        verify(customerDAO, never()).insertCustomer(any());

    }

    @Test
    void willDeleteCustomerThatExistInDatabase() {
        //Given
        Long id = 1L;
        Customer customer = new Customer(id, "Brice Adrien", "brice.adrien@gmail.com", 17);
        when(customerDAO.getCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        underTest.deleteCustomer(id);

        //Then
        verify(customerDAO).deleteCustomer(customer);

    }

    @Test
    void willThrownWhenDeleteCustomerByIdWhenNotExist() {
        //Given
        Long id = 1L;
        when(customerDAO.getCustomerById(id)).thenReturn(Optional.empty());

        //When
        //Then
        assertThatThrownBy(() -> underTest.deleteCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id %s not found".formatted(id));

        verify(customerDAO, never()).deleteCustomer(any());

    }

    @Test
    void willUpdateCustomerThatExist() {
        //Given
        Long id = 1L;
        CustomerUpdateRequest request =  new CustomerUpdateRequest(
                "Brice Adrien", "brice.adrien@gmail.com", 36

        );
        Customer customerInDB = new Customer(
                id, "Brice Adrien", "brice.adri@gmail.com", 31
        );
        when(customerDAO.getCustomerById(id)).thenReturn(Optional.of(customerInDB));

        //When
        underTest.updateCustomer(id, request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer customerToUpdate = customerArgumentCaptor.getValue();
        assertThat(customerToUpdate.getId())
                .isNotNull()
                .isEqualTo(id);
        assertThat(customerToUpdate.getName()).isEqualTo(request.name());
        assertThat(customerToUpdate.getEmail()).isEqualTo(request.email());
        assertThat(customerToUpdate.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrownWhenUpdatingCustomerThatDoesNotExist() {
        //Given
        Long id = 1L;
        CustomerUpdateRequest request =  new CustomerUpdateRequest(
                "Brice Adrien", "brice.adrien@gmail.com", 36

        );

        when(customerDAO.getCustomerById(id)).thenReturn(Optional.empty());

        //When
        //Then
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id %s not found", id);
        verify(customerDAO, never()).updateCustomer(any());
    }
}