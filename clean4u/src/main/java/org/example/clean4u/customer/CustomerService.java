package org.example.clean4u.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.order.Order;
import org.example.clean4u.order.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public PageResponse<CustomerResponse.ListDTO> getAllCustomersWithSearch(int page, int size, String keyword, String category) {
        int validPage = Math.max(0, page);
        int validSize = Math.max(1, Math.min(50, size));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(validPage, validSize, sort);

        Page<Customer> customerPage;

        if (keyword == null || keyword.trim().isBlank()) {
            customerPage = repository.findAllCustomers(pageable);
        } else if ("all".equalsIgnoreCase(category)) {
            customerPage = repository.searchByKeyword(keyword.trim(), pageable);
        } else if ("name".equalsIgnoreCase(category)) {
            customerPage = repository.findByNameContaining(keyword.trim(), pageable);
        } else if ("phone".equalsIgnoreCase(category)) {
            customerPage = repository.findByPhoneContaining(keyword.trim(), pageable);
        } else {
            customerPage = repository.findAllCustomers(pageable);
        }

        return new PageResponse<>(customerPage, CustomerResponse.ListDTO::new);
    }
}
