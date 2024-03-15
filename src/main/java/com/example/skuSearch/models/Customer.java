package com.example.skuSearch.models;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.util.List;

@Document(collection = "customers")
public class Customer {
    @Id
    private String id;

    private String customerName;

    private List<String> skuList;

    private String date;

    private String invoiceNumber;

    public String  getId() {
        return id;
    }

    public void setId(String  id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<String> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<String> skuList) {
        this.skuList = skuList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}

