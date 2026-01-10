package org.example.clean4u.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.employee.EmployeeRepository;
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
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public Customer save(@Valid CustomerRequest.SaveDTO dto, Long employeeId) {
        if (customerRepository.existsByPhone(dto.getPhone())) {
            throw new Exception400("이미 등록된 연락처입니다. 다른 번호로 시도하세요");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception404("해당 직원이 없습니다"));

        Customer customer = dto.toEntity(employee);
        customer.setGrade(Grade.NEW);
        return repository.save(customer);
    }

    public List<CustomerResponse.ListDTO> getAllCustomers() {
        List<Customer> customerList = repository.findAll();

        return customerList.stream()
                .map(CustomerResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    public CustomerResponse.DetailDTO getDetail(Long customerId, Long employeeId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new Exception404("해당 고객이 없습니다."));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new Exception404("해당 직원이 없습니다"));

        List<Order> orders = orderRepository.findByCustomerIdOrderByIdDesc(customerId);

        List<CustomerResponse.OrderDTO> orderDTOs = orders.stream()
                .map(CustomerResponse.OrderDTO::new)
                .toList();

        return new CustomerResponse.DetailDTO(customer, orderDTOs);
    }

    public CustomerResponse.UpdateDTO getFormForUpdate(Long customerId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new Exception404("해당 고객이 없습니다."));

        List<Order> orders = orderRepository.findByCustomerIdOrderByIdDesc(customerId);

        return new CustomerResponse.UpdateDTO(customer);
    }

    @Transactional
    public CustomerResponse.UpdateDTO update(Long customerId, @Valid CustomerRequest.UpdateDTO updateDTO) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new Exception404("해당 고객이 없습니다."));

        customer.update(updateDTO);

        return new CustomerResponse.UpdateDTO(customer);
    }

    @Transactional
    public void delete(Long customerId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new Exception404("해당 고객이 없습니다."));

        repository.deleteById(customerId);
    }

    @Transactional
    public void deactivateCustomer(Long customerId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new Exception404("해당 고객이 없습니다"));

        customer.deactivate();
    }

    @Transactional
    public void activateCustomer(Long customerId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new Exception404("해당 고객이 없습니다"));

        customer.activate();
    }

    public PageResponse<CustomerResponse.ListDTO> getAllCustomersWithSearch(
            int page, int size, String keyword, String category) {
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
            customerPage = repository.searchByKeyword(keyword.trim(), pageable);
        } else if ("phone".equalsIgnoreCase(category)) {
            customerPage = repository.searchByKeyword(keyword.trim(), pageable);
        } else if ("grade".equalsIgnoreCase(category)) {
            try {
                Grade grade = Grade.valueOf(keyword.trim().toUpperCase());
                customerPage = repository.findCustomersByGrade(grade, pageable);
            } catch (IllegalArgumentException e) {
                throw new Exception404("존재하지 않는 등급입니다.");
            }
        } else {
            customerPage = repository.findAllCustomers(pageable);
        }

        return new PageResponse<>(customerPage, CustomerResponse.ListDTO::new);
    }

    public PageResponse<CustomerResponse.ListDTO> getAllCustomersForEmployee(
            int page, int size, String keyword, String category) {
        int validPage = Math.max(0, page);
        int validSize = Math.max(1, Math.min(50, size));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(validPage, validSize, sort);

        Page<Customer> customerPage;

        if (keyword == null || keyword.trim().isBlank()) {
            customerPage = repository.findAllAndIsActiveTrue(pageable);
        } else if ("all".equalsIgnoreCase(category)) {
            customerPage = repository.searchByKeywordAndIsActiveTrue(keyword.trim(), pageable);
        } else if ("name".equalsIgnoreCase(category)) {
            customerPage = repository.searchByKeywordAndIsActiveTrue(keyword.trim(), pageable);
        } else if ("phone".equalsIgnoreCase(category)) {
            customerPage = repository.searchByKeywordAndIsActiveTrue(keyword.trim(), pageable);
        } else if ("grade".equalsIgnoreCase(category)) {
            try {
                Grade grade = Grade.valueOf(keyword.trim().toUpperCase());
                customerPage = repository.findAllByGradeAndIsActiveTrue(grade, pageable);
            } catch (IllegalArgumentException e) {
                throw new Exception404("존재하지 않는 등급입니다.");
            }
        } else {
            customerPage = repository.findAllAndIsActiveTrue(pageable);
        }

        return new PageResponse<>(customerPage, CustomerResponse.ListDTO::new);
    }
}
