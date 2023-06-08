package com.example.javaexample.domain;

import com.example.javaexample.AbstractJDBCTemplateTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class CustomerJDBCDataAccessServiceTest extends AbstractJDBCTemplateTest {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(getJDBCTemplate(), customerRowMapper);
    }

    @Test
    void getAllCustomers() {

        //Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );

        underTest.insertCustomer(customer);

        //When
        List<Customer> customers = underTest.getAllCustomers();

        //Then
        assertThat(customers).isNotEmpty();
        assertThat(customers.size()).isGreaterThan(0);
    }

    @Test
    void getCustomerById() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        underTest.insertCustomer(customer);

        Long customerId = underTest.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Optional<Customer> result = underTest.getCustomerById(customerId);

        assertThat(Optional.of(result))
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.get().getId()).isEqualTo(customerId);
                    assertThat(c.get().getName()).isEqualTo(customer.getName());
                    assertThat(c.get().getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.get().getAge()).isEqualTo(customer.getAge());
                });
    }

    @Test
    void willThrowResourceNotFoundExceptionWhenSelectCustomerById() {
        //Given
        Long id = -1L;

        //Then
        Optional<Customer> optionalCustomer = underTest.getCustomerById(id);

        assertThat(optionalCustomer).isEmpty();
    }

    @Test
    void insertCustomer() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        //When
        underTest.insertCustomer(customer);

        List<Customer> customers = underTest.getAllCustomers();
        Optional<Customer> createdCustomer = customers.stream().filter(c -> c.getEmail().equals(email)).findFirst();

        //Then
        assertThat(customers).isNotEmpty();
        assertThat(createdCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getId()).isNotNull();
                    assertThat(c.getName()).isEqualTo(customer.getName());
                    assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.getAge()).isEqualTo(customer.getAge());
                });
    }

    @Test
    void existPersonWithEmail() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        underTest.insertCustomer(customer);

        //When

        boolean result = underTest.existPersonWithEmail(email);

        //Then

        assertThat(result).isTrue();

    }

    @Test
    void existPersonWithEmailWillReturnFalseWhenPersonIsNotPresent() {
        //Given
        String email = "test@gmail.com";
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );

        underTest.insertCustomer(customer);

        //When
        boolean result = underTest.existPersonWithEmail(email);

        //Then
        assertThat(result).isFalse();


    }

    @Test
    void deleteCustomer() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        underTest.insertCustomer(customer);
        Customer fetchedCustomer = underTest.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElseThrow();

        //When
        underTest.deleteCustomer(fetchedCustomer);
        Long customerId = fetchedCustomer.getId();
        Optional<Customer> optionalCustomer = underTest.getCustomerById(customerId);

        //Then
        assertThat(optionalCustomer).isEmpty();
    }

    @Test
    void updateCustomer() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        underTest.insertCustomer(customer);

        Customer fetchedCustomer = underTest.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElseThrow();

        String newName = "NewName";
        fetchedCustomer.setName(newName);

        //When
        Long customerId = fetchedCustomer.getId();
        underTest.updateCustomer(fetchedCustomer);

        Optional<Customer> updatedCustomer = underTest.getCustomerById(customerId);

        assertThat(updatedCustomer).satisfies(customer1 -> {
            assertThat(customer1.get().getId()).isEqualTo(customerId);
            assertThat(customer1.get().getName()).isEqualTo(newName);
            assertThat(customer1.get().getEmail()).isEqualTo(customer.getEmail());
            assertThat(customer1.get().getAge()).isEqualTo(customer.getAge());
        });
    }
}