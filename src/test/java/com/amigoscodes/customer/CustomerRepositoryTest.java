package com.amigoscodes.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none"
})
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        UUID id = UUID.randomUUID();
        String phoneNumber = "0000";
        //Given
        Customer customer = new Customer (id, "Abel", phoneNumber);
        //When
        underTest.save(customer);
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).isEqualToComparingFieldByField(customer);

                });


    }
    @Test
    void itNotShouldSelectCustomerByPhoneNumber() {
        UUID id = UUID.randomUUID();
        String phoneNumber = "0000";
        //Given
        Customer customer = new Customer (id, "Abel", phoneNumber);
        //When
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);
        assertThat(optionalCustomer).isNotPresent();

    }

    @Test
    void itShouldSaveCustomer() {
        UUID id = UUID.randomUUID();
        //Given
       Customer customer = new Customer (id, "Abel", "0000");
        //When
        underTest.save(customer);
        //Then
        Optional<Customer> optionalCustomer = underTest.findById(id);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo("Abel");
                    assertThat(c.getPhoneNumber()).isEqualTo("0000");
                    assertThat(c).isEqualToComparingFieldByField(customer);

                });
    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        UUID id = UUID.randomUUID();
        //Given
        Customer customer = new Customer (id, null, "0000");
        //When
        assertThatThrownBy(()->underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.amigoscodes.customer.Customer.name; nested exception is org.hibernate.PropertyValueException: not-null property references a null or transient value : com.amigoscodes.customer.Customer.name")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}