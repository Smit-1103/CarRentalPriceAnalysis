package org.example;

import java.util.*;

import static org.example.SearchQuery.getSearchCount;
import static org.example.SpellChecker.getSuggestionWord;

public class IntegratedFinalFile {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        UserManager userManager = new UserManager();

        while (true) {
            System.out.print("Enter 'login' to log in, 'signup' to sign up, or 'exit' to quit: ");
            String action = getInput().trim().toLowerCase();

            if (action.equals("exit")) {
                break;
            }

            if (action.equals("signup")) {
                String username;
                String password;

                while (true) {
                    System.out.print("Enter username (email): ");
                    username = getInput().trim();
                    if (!userManager.isEmailValid(username)) {
                        System.out.println("The username must be a valid email address. Please try again.");
                    } else {
                        break;
                    }
                }

                while (true) {
                    System.out.print("Enter password: ");
                    password = getInput().trim();
                    if (!userManager.isPasswordStrong(password)) {
                        System.out.println("Your password is not strong enough. Please follow these guidelines:");
                        System.out.println("1. At least 8 characters long");
                        System.out.println("2. Includes both upper and lower case letters");
                        System.out.println("3. Includes at least one digit");
                        System.out.println("4. Includes at least one special character (e.g., !@#$%^&*)");
                    } else if (userManager.isPasswordUsed(password)) {
                        System.out.println("This password has been used before. Please choose a different password.");
                    } else {
                        break;
                    }
                }

                boolean success = userManager.signup(username, password);
                if (success) {
                    System.out.println("Signup successful.");
                } else {
                    System.out.println("Signup failed.");
                }
            }

            if (action.equals("login")) {
                System.out.print("Enter username (email): ");
                String username = getInput().trim();
                System.out.print("Enter password: ");
                String password = getInput().trim();

                boolean success = userManager.login(username, password);
                if (success) {
                    System.out.println("Login successful.");
                    handleSearchAndSuggestions();
                } else {
                    System.out.println("Login failed.");
                }
            }
        }

        scanner.close();
    }

    private static void handleSearchAndSuggestions() {
        while (true) {
            System.out.print("Enter your search query (or write exit): ");
            String query = getInput().trim().toLowerCase();

            if (query.equals("exit")) {
                break;
            }

            int frequency = getSearchCount(query); // Assuming this method is implemented elsewhere
            System.out.println("This query is searched for " + frequency + " times.");

            List<String> suggestions = getSuggestionWord(query); // Assuming this method is implemented elsewhere

            if (query.equals(suggestions.get(0))) {
                System.out.println("Word is Correct");

                // Display top 10 items from PageRanking
                PageRanking pageRanking = new PageRanking(); // Adjust as necessary to fit your setup
                List<Product> topProducts = pageRanking.searchProducts("cars1.csv", query);
                System.out.println("Top 10 Products:");
                int count = 0;
                for (Product product : topProducts) {
                    System.out.println("Product: " + product.name + ", Link: " + product.link + ", Price: " + product.price);
                    count++;
                    if (count >= 10) break;
                }
            } else {
                System.out.println("Spelling is Wrong. Do you mean " + suggestions.get(0) + "?");
                // No need to display top 10 products if the word is incorrect
            }
        }
    }

    private static String getInput() {
        try {
            return scanner.nextLine();
        } catch (NoSuchElementException e) {
            // Handle the case where no input is available
            System.out.println("Input error. Exiting...");
            System.exit(1);
            return ""; // This line is unreachable but required for compilation
        }
    }
}
