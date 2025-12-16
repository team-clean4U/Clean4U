package org.example.clean4u.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.exception.Exception400;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {
    private final CustomerRepository repository;

    public Customer save(@Valid CustomerRequest.SaveDTO dto) {
        Customer customer = dto.toEntity();
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

        return new CustomerResponse.DetailDTO(customer);
    }

    public CustomerResponse.DetailDTO getFormForUpdate(Long customerId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new Exception400("해당 고객이 없습니다."));

        return new CustomerResponse.DetailDTO(customer);
    }

    public CustomerResponse.DetailDTO update(Long customerId, @Valid CustomerRequest.UpdateDTO updateDTO) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new Exception400("해당 고객이 없습니다."));

        customer.update(updateDTO);
        return new CustomerResponse.DetailDTO(customer);
    }

    public void delete(Long customerId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new Exception400("해당 고객이 없습니다."));

        repository.deleteById(customerId);
    }
}
