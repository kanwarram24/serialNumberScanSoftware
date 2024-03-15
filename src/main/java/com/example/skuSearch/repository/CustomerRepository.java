package com.example.skuSearch.repository;

import com.example.skuSearch.models.Customer;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    List<Customer> findBySkuListContaining(String sku);

    List<Customer> findAll();
    List<Customer> findByInvoiceNumber(String invoiceNumber);
    List<Customer> findByDate(String date);

    List<Customer> findByCustomerName(String customerName);

}
