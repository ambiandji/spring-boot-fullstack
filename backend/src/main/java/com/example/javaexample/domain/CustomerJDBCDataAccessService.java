package com.example.javaexample.domain;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbcTemplateRepository")
public class CustomerJDBCDataAccessService implements CustomerDAO{

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return jdbcTemplate.query("SELECT id, name, email, age FROM customer", customerRowMapper);
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return jdbcTemplate.query("SELECT id, name, email, age FROM customer WHERE id = ?", customerRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = "INSERT INTO customer(name, email, age) VALUES(?, ?, ?)";
        int update = jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge());
    }

    @Override
    public boolean existPersonWithEmail(String email) {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM customer WHERE email = ?", Integer.class, email);
        return count != null && count != 0;
    }

    @Override
    public void deleteCustomer(Customer customer) {
        jdbcTemplate.update("DELETE FROM customer WHERE id = ?", customer.getId());
    }

    @Override
    public void updateCustomer(Customer customer) {
        jdbcTemplate.update("UPDATE customer SET name = ?, email = ?, age = ? WHERE id = ?",
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                customer.getId());
    }
}
