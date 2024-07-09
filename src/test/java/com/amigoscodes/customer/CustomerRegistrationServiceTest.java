package com.amigoscodes.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;
    @Mock
    private CustomerRepository customerRepository;
    private CustomerRegistrationService underTest;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerRegistrationService(customerRepository);
    }

    @Test
    void itShouldSaveNewCustomer() {
        //Given
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Maryam", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.empty());
        //When

        underTest.registerNewCustomer(request);

        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToComparingFieldByField(customer);
        assertThat(customerArgumentCaptorValue).isEqualTo(customer);
    }

    @Test
    void itShouldNotSaveNewCustomer() {
        //Given
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Maryam", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        //When
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customer));
        underTest.registerNewCustomer(request);
        then(customerRepository).should(never()).save((any()));
    }
    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        //Given
        String phoneNumber = "000099";
        Customer customer = new Customer(null, "Maryam", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.empty());
        //When

        underTest.registerNewCustomer(request);

        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToIgnoringGivenFields(customer, "id");
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }
    @Test
    void itShouldNotSaveCustomerWhenCustomerExists() {
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Maryam", phoneNumber);
        Customer customer2 = new Customer(UUID.randomUUID(), "John", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        //When
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customer2));

        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(String.format("Customer with this number already exists"));
        then(customerRepository).should(never()).save(any(Customer.class));
    }
}