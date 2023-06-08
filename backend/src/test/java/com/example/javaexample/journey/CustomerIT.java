package com.example.javaexample.journey;

import com.example.javaexample.domain.Customer;
import com.example.javaexample.domain.CustomerRegistrationRequest;
import com.example.javaexample.domain.CustomerUpdateRequest;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIT {

    @Autowired
    private  WebTestClient webTestClient;

    private static final Random random = new Random();


    public static final String API_ROOT_URL = "/api/v1/customers";

    @Test
    void canRegisterCustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = name.toLowerCase() + "-" + UUID.randomUUID().toString() + "@gmail.com";
        int age = random.nextInt(1, 100);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );

        //send a post request
        webTestClient.post()
                .uri(API_ROOT_URL)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(request), CustomerIT.class)
                .exchange()
                .expectStatus()
                .isCreated();
        //get all customer
        List<Customer> customers = webTestClient.get()
                .uri(API_ROOT_URL)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();



        //make sure that customer is present
        Customer expectedCustomer = new Customer(name, email, age);

        assertThat(customers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        //get customer by id
        assert customers != null;
        var id = customers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        expectedCustomer.setId(id);

        webTestClient.get()
                .uri(API_ROOT_URL + "/{id}", id)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .isEqualTo(expectedCustomer);

    }

    @Test
    void canDeleteACustomer() {
        //create a customer
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = name.toLowerCase() + "-" + UUID.randomUUID().toString() + "@gmail.com";
        int age = random.nextInt(1, 100);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );

        webTestClient.post()
                .uri(API_ROOT_URL)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(request), CustomerIT.class)
                .exchange()
                .expectStatus()
                .isCreated();

        //get all customer
        List<Customer> customers = webTestClient.get()
                .uri(API_ROOT_URL)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();

        assert customers != null;
        var id = customers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        webTestClient.delete()
                .uri(API_ROOT_URL + "/{id}", id)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent();


        webTestClient.get()
                .uri(API_ROOT_URL + "/{id}", id)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();

    }

    @Test
    void canUpdateCustomer() {
        //create a customer
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = name.toLowerCase() + "-" + UUID.randomUUID().toString() + "@gmail.com";
        int age = random.nextInt(1, 100);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );

        webTestClient.post()
                .uri(API_ROOT_URL)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(request), CustomerIT.class)
                .exchange()
                .expectStatus()
                .isCreated();

        //Get All Customers
        List<Customer> customers = webTestClient.get()
                .uri(API_ROOT_URL)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        //Get the id of Customer that we need to update
        assert customers != null;
        var id = customers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //Update the customer
        String newName = fakerName.fullName();
        String newEmail = name.toLowerCase() + "-" + UUID.randomUUID().toString() + "@gmail.com";
        int newAge = random.nextInt(1, 100);
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName,newEmail, newAge
        );

        webTestClient.put()
                        .uri(API_ROOT_URL + "/{id}", id)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                        .exchange()
                        .expectStatus()
                        .isOk();
        //We Fetch de updated Customer and check it
        Customer expectedCustomer = new Customer(
                id, newName, newEmail, newAge
        );
        webTestClient.get()
                .uri(API_ROOT_URL + "/{id}", id)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expectedCustomer);

    }
}
