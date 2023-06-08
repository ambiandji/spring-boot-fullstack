package com.example.javaexample.domain;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        //Given
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet resultSet = mock(ResultSet.class);
        String name = "Brice Adrien";
        String email = "brice.adrien@gmail.com";
        int age = 36;
        int id = 1;
        when(resultSet.getInt("id")).thenReturn(id);
        when(resultSet.getString("name")).thenReturn(name);
        when(resultSet.getString("email")).thenReturn(email);
        when(resultSet.getInt("age")).thenReturn(age);

        //When
        Customer customer = customerRowMapper.mapRow(resultSet, 1);

        assertThat(customer)
                .isNotNull()
                .satisfies(c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo(name);
                    assertThat(c.getEmail()).isEqualTo(email);
                    assertThat(c.getAge()).isEqualTo(age);
                });


    }
}