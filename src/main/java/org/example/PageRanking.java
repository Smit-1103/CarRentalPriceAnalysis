package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Product {
    String name;
    String link;
    String price;
    String rating;
    int frequency;
     // Added rating attribute


    public Product(String name, String link, String price, String rating, int frequency) {
        this.name = name;
        this.link = link;
        this.price = price;
        this.rating = rating;
        this.frequency = frequency;

    }
    @Override
    public String toString() {
        return "Product: " + name + ", Link: " + link + ", Price: " + price + ", Rating: " + rating;
    }
}

class PageRanking {

    // Function to read product data from CSV file
    public static List<Product> readProductData(String filePath) {
        List<Product> productList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 2) {
                    productList.add(new Product(values[0], values[4], values[2], values[3], 0));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return productList;
    }

    // Boyer-Moore algorithm to count occurrences of a keyword in a text
    public static int boyerMoore(String text, String pattern) {
        Map<Character, Integer> badChar = preprocessPattern(pattern);
        int m = pattern.length();
        int n = text.length();
        int s = 0; // s is the shift of the pattern with respect to text
        int count = 0;

        while (s <= (n - m)) {
            int j = m - 1;

            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j))
                j--;

            if (j < 0) {
                count++;
                s += (s + m < n) ? m - badChar.getOrDefault(text.charAt(s + m), -1) : 1;
            } else {
                s += Math.max(1, j - badChar.getOrDefault(text.charAt(s + j), -1));
            }
        }
        return count;
    }

    // Preprocess the pattern and build the bad character table
    public static Map<Character, Integer> preprocessPattern(String pattern) {
        Map<Character, Integer> badChar = new HashMap<>();
        int m = pattern.length();

        for (int i = 0; i < m; i++) {
            badChar.put(pattern.charAt(i), i);
        }

        return badChar;
    }

    // Function to calculate frequency of keywords using Boyer-Moore algorithm
    public static void calculateKeywordFrequency(List<Product> productList, List<String> searchKeywords) {
        for (Product product : productList) {
            int totalFrequency = 0;
            for (String keyword : searchKeywords) {
                totalFrequency += boyerMoore(product.name.toLowerCase(), keyword.toLowerCase());
            }
            product.frequency = totalFrequency;
        }
    }

    // Function to rank pages based on frequency count
    public static List<Product> rankPages(List<Product> productList) {
        productList.sort((p1, p2) -> Integer.compare(p2.frequency, p1.frequency));
        return productList;
    }

    // Function to search for products and display results
    public static List<Product> searchProducts(String filePath, String query) {
        List<Product> productList = readProductData(filePath);
        List<String> searchKeywords = Arrays.asList(query.toLowerCase().split("\\s+"));
        calculateKeywordFrequency(productList, searchKeywords);
        return rankPages(productList);
    }

    // Function to get top 10 items based on search query
    public static List<Product> getTopItems(String filePath, String query) {
        List<Product> rankedProducts = searchProducts(filePath, query);
        return rankedProducts.size() > 10 ? rankedProducts.subList(0, 10) : rankedProducts;
    }

    // Function to get all items based on search query
    public static List<Product> getAllItems(String filePath, String query) {
        List<Product> rankedProducts = searchProducts(filePath, query);
        return rankedProducts; // Return the entire list
    }

    // Function to display all details of all products based on search query
    public static void displayAllDetails(String filePath, String query) {
        List<Product> allProducts = getAllItems(filePath, query);

        System.out.println("All Products Details:");
        for (Product product : allProducts) {
            System.out.println("Name: " + product.name);
            System.out.println("Link: " + product.link);
            System.out.println("Price: " + product.price);
            System.out.println("Frequency: " + product.frequency);
            System.out.println(); // Add a blank line for readability
        }
    }
    // Function to rank pages based on rating (String)
    public static List<Product> rankPagesByRating(List<Product> productList) {
        productList.sort((p1, p2) -> {
            try {
                double rating1 = Double.parseDouble(p1.rating);
                double rating2 = Double.parseDouble(p2.rating);
                return Double.compare(rating2, rating1);
            } catch (NumberFormatException e) {
                return p1.rating.compareTo(p2.rating); // Fallback to string comparison if parsing fails
            }
        });
        return productList;
    }


    // Main function to demonstrate the page ranking and search functionality
    public static void main(String[] args) {
        String csvFilePath = "cars1.csv";
        Scanner scanner = new Scanner(System.in);
        String searchQuery;

        while (true) {
            System.out.print("Enter Search Query (or type 'exit' to quit): ");
            searchQuery = scanner.nextLine();

            if (searchQuery.equalsIgnoreCase("exit")) {
                break;
            }

            // Display top 10 items
            List<Product> topProducts = getTopItems(csvFilePath, searchQuery);
            System.out.println("Top Search Results:");
            int count = 0;
            for (Product product : topProducts) {
                System.out.println("Product: " + product.name + ", Link: " + product.link + ", Price: " + product.price);
                count++;
                if (count >= 10) {
                    break;
                }
            }

            System.out.println();

            // Display all details of all products
            displayAllDetails(csvFilePath, searchQuery);
        }

        scanner.close();
    }
}
