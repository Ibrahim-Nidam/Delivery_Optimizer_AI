package com.deliveryoptimizer.service.impl;

import com.deliveryoptimizer.dto.CustomerDTO;
import com.deliveryoptimizer.mapper.CustomerMapper;
import com.deliveryoptimizer.model.Customer;
import com.deliveryoptimizer.repository.CustomerRepository;
import com.deliveryoptimizer.service.interfaces.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);

        // Additional validation if needed
        if (customer.getName() == null || customer.getName().isBlank()) {
            throw new RuntimeException("Customer name cannot be empty");
        }

        Customer saved = customerRepository.save(customer);
        return customerMapper.toDTO(saved);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return customerMapper.toDTO(customer);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toDTO)
                .toList();
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        // Update fields
        existing.setName(customerDTO.getName());
        existing.setAddress(customerDTO.getAddress());
        existing.setLatitude(customerDTO.getLatitude());
        existing.setLongitude(customerDTO.getLongitude());
        existing.setPreferredTimeSlot(customerDTO.getPreferredTimeSlot());

        Customer saved = customerRepository.save(existing);
        return customerMapper.toDTO(saved);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        customerRepository.delete(existing);
    }
}
