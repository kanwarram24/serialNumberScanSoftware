package com.example.skuSearch.app;

import com.example.skuSearch.models.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;

public class CustomerManagementApp extends JFrame {
    private JTextField customerNameField, invoiceNumberField;
    private JTextArea skuField;
    private JDateChooser dateChooser;
    private JTextPane resultTextPane;

    private JTable searchResultTable;


    public CustomerManagementApp() {
        setTitle("Serial Number Scanner ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set width to 40% of screen width
        int width = (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.4);

        // Set height to 100% of screen height
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;

        setSize(width, height);

        setResizable(false); // Prevent resizing for simplicity

        initComponents();
        mouseEditing();
        layoutComponents();
    }

    private void mouseEditing(){
        ContextMenuMouseListener contextMenuMouseListener = new ContextMenuMouseListener();
        skuField.addMouseListener(contextMenuMouseListener);
        customerNameField.addMouseListener(contextMenuMouseListener);
        invoiceNumberField.addMouseListener(contextMenuMouseListener);
        resultTextPane.addMouseListener(contextMenuMouseListener);
    }

    private void initComponents() {
        customerNameField = new JTextField(20);
        skuField = new JTextArea(10, 20);
        invoiceNumberField = new JTextField(20);

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd-MM-yyyy");

        Calendar today = Calendar.getInstance();
        dateChooser.setDate(today.getTime());

        resultTextPane = new JTextPane();
        resultTextPane.setEditable(false);
        resultTextPane.setPreferredSize(new Dimension(200, 100));
    }

    private String sanitizeInput(String input) {
        Map<Character, Character> replacements = new HashMap<>();
        replacements.put('â', 'a');
        replacements.put('ä', 'a');
        replacements.put('á', 'a');
        replacements.put('å', 'a');
        replacements.put('ö', 'o');
        replacements.put('ô', 'o');
        replacements.put('ó', 'o');
        replacements.put('í', 'i');
        replacements.put('ì', 'i');
        replacements.put('î', 'i');
        replacements.put('é', 'e');
        replacements.put('è', 'e');
        replacements.put('ê', 'e');
        replacements.put('ñ', 'n');

        StringBuilder sanitized = new StringBuilder();
        for (char c : input.toCharArray()) {
            sanitized.append(replacements.getOrDefault(c, c));
        }
        return sanitized.toString();
    }


    private void updateSearchResult(String message) {
        // Create a new table model with a single row and a single column
        DefaultTableModel model = new DefaultTableModel(new Object[][]{{message}}, new Object[]{"Message"});
        searchResultTable.setModel(model);
    }

    // Method to update the selected customer
    private void updateCustomer() {
        // Get the selected row index
        int rowIndex = searchResultTable.getSelectedRow();
        if (rowIndex == -1) {
            addCustomer();
            return;
        }

        // Get the selected customer's ID
        String customerId = searchResultTable.getValueAt(rowIndex, 0).toString();

        // Get updated values from input fields
        String customerName = customerNameField.getText();
        String date = ((JTextField) dateChooser.getDateEditor().getUiComponent()).getText();
        String[] skuList = skuField.getText().split("\n");
        String invoiceNumber = invoiceNumberField.getText();

        if (customerName.isEmpty() || date.isEmpty() || skuList.length == 0) {
            updateSearchResult("Please fill in the customerName, Date, and Serial Numbers fields.");
            return;
        }

        customerName = customerName.toUpperCase();

        // Convert all elements of skuList to lowercase
        for (int i = 0; i < skuList.length; i++) {
            skuList[i] = sanitizeInput(skuList[i].toLowerCase());
        }


        // Construct the updated customer object
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(customerId);
        updatedCustomer.setCustomerName(customerName);
        updatedCustomer.setDate(date);
        updatedCustomer.setSkuList(Arrays.asList(skuList));
        updatedCustomer.setInvoiceNumber(invoiceNumber);

        // Make a PUT request to update the customer
        String apiUrl = "http://localhost:9090/api/customers/" + customerId;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(updatedCustomer);

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(requestBody.getBytes());
            }

            int responseCode = connection.getResponseCode();
            if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
                succesfullySaveCustomer(connection, "Customer Updated successfully.");
            } else {
                updateSearchResult("Failed to update customer. Response code: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
            updateSearchResult("Error occurred while updating customer.");
        }
    }

    private void clearFields() {
        // Clear input fields
        customerNameField.setText("");
        skuField.setText("");
        invoiceNumberField.setText("");
        Calendar today = Calendar.getInstance();
        dateChooser.setDate(today.getTime());

        // Clear output field
        updateSearchResult("");

        searchResultTable.setModel(new DefaultTableModel());

    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        mainPanel.add(new JLabel("Serial Number Scanner Software || Developed By Kanwar Ram"), gbc);


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;

        mainPanel.add(new JLabel("Date:"), gbc);
        gbc.gridy++;
        mainPanel.add(new JLabel("Customer Name:"), gbc);
        gbc.gridy++;
        mainPanel.add(new JLabel("Invoice Number:"), gbc);
        gbc.gridy++;
        mainPanel.add(new JLabel("Serial Number:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        mainPanel.add(dateChooser, gbc);
        gbc.gridy++;
        mainPanel.add(customerNameField, gbc);
        gbc.gridy++;
        mainPanel.add(invoiceNumberField, gbc);
        gbc.gridy++;
        mainPanel.add(new JScrollPane(skuField), gbc);


        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;

        mainPanel.add(createClearButton(), gbc);
        gbc.gridx++;
        gbc.gridx++;

        mainPanel.add(createUpdateButton(), gbc);
        gbc.gridx--;
        gbc.gridx--;
        gbc.gridy++;

        mainPanel.add(createSearchButton("Search by Serial Number", this::searchBySKU), gbc);
        gbc.gridy++;
        mainPanel.add(createSearchButton("Search by Customer Name", this::searchByCustomerName), gbc);
        gbc.gridy++;
        mainPanel.add(createSearchButton("Search by Invoice Number", this::searchByInvoiceNumber), gbc);
        gbc.gridy++;
        mainPanel.add(createSearchButton("Search by Date", this::searchByDate), gbc);

        // Create table for search results
        searchResultTable = new JTable();
        searchResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResultTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int rowIndex = searchResultTable.getSelectedRow();
                if (rowIndex != -1) {
                    // Get selected customer's details and autofill input fields
                    customerNameField.setText(searchResultTable.getValueAt(rowIndex, 1).toString());
                    String skuList = searchResultTable.getValueAt(rowIndex, 2).toString();
                    skuField.setText(skuList.replaceAll(", ", "\n"));
                    ((JTextField) dateChooser.getDateEditor().getUiComponent()).setText(searchResultTable.getValueAt(rowIndex, 3).toString());
                    invoiceNumberField.setText(searchResultTable.getValueAt(rowIndex, 4).toString());
                }
            }
        });


        JScrollPane searchResultScrollPane = new JScrollPane(searchResultTable);
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(searchResultScrollPane, gbc);

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        resultPanel.add(new JLabel("Search Results:"), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(searchResultTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.NORTH);
        getContentPane().add(resultPanel, BorderLayout.CENTER);

        setResizable(true);


    }


    private JButton createSearchButton(String label, ActionListener actionListener) {
        JButton searchButton = new JButton(label);
        searchButton.addActionListener(actionListener);
        return searchButton;
    }

    private JButton createClearButton() {
        JButton clearButton = new JButton("Add New Customer");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
        return clearButton;
    }

    private JButton createUpdateButton() {
        JButton updateButton = new JButton("Save");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCustomer();
            }
        });
        return updateButton;
    }

    private void addCustomer() {
        // Get values from input fields
        String customerName = customerNameField.getText();
        String date = ((JTextField) dateChooser.getDateEditor().getUiComponent()).getText();
        String[] skuList = skuField.getText().split("\n");
        String invoiceNumber = invoiceNumberField.getText();

        if (customerName.isEmpty() || date.isEmpty() || skuList.length == 0) {
            updateSearchResult("Please fill in the customerName, Date, and Serial Numbers fields.");
            return;
        }

        customerName = customerName.toUpperCase();

        // Convert all elements of skuList to lowercase
        for (int i = 0; i < skuList.length; i++) {
            skuList[i] = sanitizeInput(skuList[i].toLowerCase());
        }

        // Build the URL for the backend API
        String apiUrl = "http://localhost:9090/api/customers";

        try {
            // Create JSON payload for the new customer
            StringBuilder requestBody = new StringBuilder();
            requestBody.append("{");
            requestBody.append("\"customerName\": \"").append(customerName).append("\",");
            requestBody.append("\"date\": \"").append(date).append("\",");
            requestBody.append("\"skuList\": [");
            for (int i = 0; i < skuList.length; i++) {
                requestBody.append("\"").append(skuList[i]).append("\"");
                if (i < skuList.length - 1) {
                    requestBody.append(",");
                }
            }
            requestBody.append("],");
            requestBody.append("\"invoiceNumber\": \"").append(invoiceNumber).append("\"");
            requestBody.append("}");

            // Make a POST request to the backend API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Write the request body
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(requestBody.toString().getBytes());
            }

            // Check the response code
            int responseCode = connection.getResponseCode();

            if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
                succesfullySaveCustomer(connection, "Customer added successfully.");
            } else {
                updateSearchResult("Failed to add customer. Response code: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void succesfullySaveCustomer(HttpURLConnection connection, String message) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Customer customer = objectMapper.readValue(response.toString(), Customer.class);

            // Display data in a JTable
            String[] columnNames = {"ID", "Customer Name", "Serial Numbers", "Date", "Invoice Number"};
            Object[][] rowData = new Object[1][columnNames.length];

            rowData[0][0] = customer.getId();
            rowData[0][1] = customer.getCustomerName();
            rowData[0][2] = String.join(", ", customer.getSkuList());
            rowData[0][3] = customer.getDate();
            rowData[0][4] = customer.getInvoiceNumber();


            DefaultTableModel model = new DefaultTableModel(rowData, columnNames);
            searchResultTable.setModel(model);

            TableColumnModel columnModel = searchResultTable.getColumnModel();
            columnModel.getColumn(0).setWidth(0);
            columnModel.getColumn(0).setMinWidth(0);
            columnModel.getColumn(0).setMaxWidth(0);
            columnModel.getColumn(0).setPreferredWidth(0);

            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);

            searchResultTable.getSelectionModel().setSelectionInterval(0, 0);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchBySKU(ActionEvent e) {
        String sku = skuField.getText();
        sku = sku.toLowerCase();
        if (!sku.isEmpty()) {
            performSearch("/search/sku/" + sku);
            customerNameField.setText("");
            invoiceNumberField.setText("");
            Calendar today = Calendar.getInstance();
            dateChooser.setDate(today.getTime());
        } else {
            updateSearchResult("SKU field cannot be empty.");
        }
    }

    private void searchByCustomerName(ActionEvent e) {
        String customerName = customerNameField.getText();
        customerName = customerName.toUpperCase();
        String encodedName = URLEncoder.encode(customerName, StandardCharsets.UTF_8);
        if (!customerName.isEmpty()) {
            performSearch("/search/customerName/" + encodedName);
            skuField.setText("");
            invoiceNumberField.setText("");
            Calendar today = Calendar.getInstance();
            dateChooser.setDate(today.getTime());
        } else {
            updateSearchResult("Customer name field cannot be empty.");
        }
    }

    private void searchByInvoiceNumber(ActionEvent e) {
        String invoiceNumber = invoiceNumberField.getText();
        if (!invoiceNumber.isEmpty()) {
            performSearch("/search/invoiceNumber/" + invoiceNumber);
            customerNameField.setText("");
            skuField.setText("");
            Calendar today = Calendar.getInstance();
            dateChooser.setDate(today.getTime());
        } else {
            updateSearchResult("Invoice number field cannot be empty.");
        }
    }

    private void searchByDate(ActionEvent e) {
        String date = ((JTextField) dateChooser.getDateEditor().getUiComponent()).getText();
        if (!date.isEmpty()) {
            performSearch("/search/date/" + date);
            customerNameField.setText("");
            skuField.setText("");
            invoiceNumberField.setText("");
        } else {
            updateSearchResult("Date field cannot be empty.");
        }
    }

    private void performSearch(String searchUrl) {
        try {
            URL url = new URL("http://localhost:9090/api/customers" + searchUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line).append("\n");
                    }

                    // Convert JSON to array of objects
                    ObjectMapper objectMapper = new ObjectMapper();
                    Customer[] customers = objectMapper.readValue(responseBuilder.toString(), Customer[].class);

                    // Display data in a JTable
                    String[] columnNames = {"ID", "Customer Name", "Serial Numbers", "Date", "Invoice Number"};
                    Object[][] rowData = new Object[customers.length][columnNames.length];
                    for (int i = 0; i < customers.length; i++) {
                        rowData[i][0] = customers[i].getId();
                        rowData[i][1] = customers[i].getCustomerName();
                        rowData[i][2] = String.join(", ", customers[i].getSkuList());
                        rowData[i][3] = customers[i].getDate();
                        rowData[i][4] = customers[i].getInvoiceNumber();
                    }

                    DefaultTableModel model = new DefaultTableModel(rowData, columnNames);
                    searchResultTable.setModel(model);

                    TableColumnModel columnModel = searchResultTable.getColumnModel();
                    columnModel.getColumn(0).setWidth(0);
                    columnModel.getColumn(0).setMinWidth(0);
                    columnModel.getColumn(0).setMaxWidth(0);
                    columnModel.getColumn(0).setPreferredWidth(0);


                } catch (IOException e) {
                    e.printStackTrace();
                    updateSearchResult("Error reading response.");
                }
            } else {
                updateSearchResult("Failed to search. Response code: " + connection.getResponseCode());
            }

            connection.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
            updateSearchResult("Error occurred while searching.");
        }
    }

    public void start() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
        });
    }

    public static void main(String[] args) {
        CustomerManagementApp app = new CustomerManagementApp();
        app.start();
    }
}
