package com.example.skuSearch.updateScripts;

import com.example.skuSearch.models.Customer;
import com.example.skuSearch.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CaseSensitiveUpdate implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        //updateCustomers();
        System.out.println("Script Already Run Through No need to run this again.");

    }

    private void updateCustomers() {
        List<Customer> customers = customerRepository.findAll();

        for (Customer customer : customers) {
            // Update customer name to uppercase
            String updatedCustomerName = customer.getCustomerName().toUpperCase();
            customer.setCustomerName(updatedCustomerName);

            // Convert SKU list elements to lowercase
            List<String> lowerCasedSkuList = customer.getSkuList().stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            customer.setSkuList(lowerCasedSkuList);

            // Save the updated customer
            customerRepository.save(customer);
        }

        System.out.println("Database update completed.");
    }
}



