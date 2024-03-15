package com.example.skuSearch.controller;
import com.example.skuSearch.models.Customer;
import com.example.skuSearch.repository.CustomerRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final MongoTemplate mongoTemplate;
    private final CustomerRepository customerRepository;

    public CustomerController(MongoTemplate mongoTemplate, CustomerRepository customerRepository) {
        this.mongoTemplate = mongoTemplate;
        this.customerRepository = customerRepository;
    }
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @PostMapping
    public Customer addCustomer(@RequestBody Customer customer) {
        return mongoTemplate.save(customer);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable String id) {
        customerRepository.deleteById(id);
    }

    @GetMapping("/search/sku/{sku}")
    public List<Customer> searchBySku(@PathVariable String sku) {
        return customerRepository.findBySkuListContaining(sku);
    }

    @GetMapping("/search/customerName/{customerName}")
    public List<Customer> searchByCustomerName(@PathVariable String customerName) {
        String decodedCustomerName = URLDecoder.decode(customerName, StandardCharsets.UTF_8);
        return customerRepository.findByCustomerName(decodedCustomerName);
    }

    @GetMapping("/search/invoiceNumber/{invoiceNumber}")
    public List<Customer> searchByInvoiceNumber(@PathVariable String invoiceNumber) {
        return customerRepository.findByInvoiceNumber(invoiceNumber);
    }

    @GetMapping("/search/date/{date}")
    public List<Customer> searchByDate(@PathVariable String date) {
        return customerRepository.findByDate(date);
    }


    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable String id, @RequestBody Customer updatedCustomer) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        existingCustomer.setCustomerName(updatedCustomer.getCustomerName());
        existingCustomer.setDate(updatedCustomer.getDate());
        existingCustomer.setSkuList(updatedCustomer.getSkuList());
        existingCustomer.setInvoiceNumber(updatedCustomer.getInvoiceNumber());

        return mongoTemplate.save(existingCustomer);
    }

}
