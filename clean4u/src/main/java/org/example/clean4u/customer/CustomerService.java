package org.example.clean4u.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception400;
import org.example.clean4u.order.Order;
import org.example.clean4u.order.OrderRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {
    private final CustomerRepository repository;
    private final OrderRepository orderRepository;

    @Transactional
    public Customer save(@Valid CustomerRequest.SaveDTO dto) {
        Customer customer = dto.toEntity();
        customer.setGrade(Grade.NEW);
        return repository.save(customer);
    }

    public List<CustomerResponse.ListDTO> getAllCustomers() {
        List<Customer> customerList = repository.findAll();

        return customerList.stream()
                .map(CustomerResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    public CustomerResponse.DetailDTO getDetail(Long customerId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new Exception400("해당 고객이 없습니다."));

        List<Order> orders = orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);

        return new CustomerResponse.DetailDTO(customer, orders);
    }

    public CustomerResponse.UpdateDTO getFormForUpdate(Long customerId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new Exception400("해당 고객이 없습니다."));

        List<Order> orders = orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);

        return new CustomerResponse.UpdateDTO(customer);
    }

    @Transactional
    public CustomerResponse.UpdateDTO update(Long customerId, @Valid CustomerRequest.UpdateDTO updateDTO) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new Exception400("해당 고객이 없습니다."));

        customer.update(updateDTO);

        return new CustomerResponse.UpdateDTO(customer);
    }

    @Transactional
    public void delete(Long customerId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new Exception400("해당 고객이 없습니다."));

        repository.deleteById(customerId);
    }

    public List<CustomerResponse.ListDTO> searchByName(String keyword) {
        return repository.findByNameContaining(keyword).stream()
                .map(CustomerResponse.ListDTO::new)
                .toList();
    }

    public List<CustomerResponse.ListDTO> searchByPhone(String keyword) {
        return repository.findByPhoneContaining(keyword).stream()
                .map(CustomerResponse.ListDTO::new)
                .toList();
    }

    public List<CustomerResponse.ListDTO> getAllCustomersContainingKeyword(String keyword) {
        return repository.searchByKeyword(keyword).stream()
                .map(CustomerResponse.ListDTO::new)
                .toList();
    }
}
