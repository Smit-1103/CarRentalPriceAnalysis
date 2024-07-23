package org.example;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.example.SearchQuery.getSearchCount;
import static org.example.SpellChecker.getSuggestionWord;
import static org.example.WordCompletionTries.getWordsWithPrefix;
import static org.example.WordFrequencyInFile.countOccurrences;
import static org.example.PageRanking.getAllItems; // Import the method to get all items

public class IntegratedFinalFile {
    private static Scanner scanner = new Scanner(System.in);
    private static UserManager userManager = new UserManager();
    private static final Pattern INVALID_INPUT_PATTERN = Pattern.compile("[^a-zA-Z0-9 ]"); // Regex to detect special characters

    public static void main(String[] args) {
        while (true) {
            System.out.print("Enter 'login' to log in, 'signup' to sign up, or 'exit' to quit: ");
            String action = getInput().trim().toLowerCase();

            if (action.equals("exit")) {
                break;
            }

            if (action.equals("signup")) {
                handleSignup();
            } else if (action.equals("login")) {
                handleLogin();
            } else {
                System.out.println("Invalid option. Please enter 'login', 'signup', or 'exit'.");
            }
        }

        scanner.close();
    }

    private static void handleSignup() {
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

    private static void handleLogin() {
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

    private static void handleSearchAndSuggestions() {
        while (true) {
            System.out.print("Enter your search query (or write exit): ");
            String query = getInput().trim().toLowerCase();

            if (query.equals("exit")) {
                break;
            }

            if (query.isEmpty() || containsSpecialCharacters(query)) {
                System.out.println("Invalid input. Please do not use special characters.");
                continue; // Prompt the user again
            }

            int frequency = getSearchCount(query); // Assuming this method is implemented elsewhere
            System.out.println("This query is searched for " + frequency + " times.");

            int wordInFileFreq = countOccurrences(query);
            System.out.println("This word occurred " + wordInFileFreq + " times in CSV file.");

            List<String> suggestions = getSuggestionWord(query); // Assuming this method is implemented elsewhere
            List<String> results = getWordsWithPrefix(query);

            if(results.size() >= 1) {
                System.out.println("Word Suggestions: ");
                for (String result : results) {
                    System.out.println(result);
                }
            }


            if (suggestions.isEmpty()) {
                System.out.println("No suggestions available.");
            } else if (query.equals(suggestions.get(0))) {
                System.out.println("Word is Correct");

                PageRanking pageRanking = new PageRanking(); // Adjust as necessary to fit your setup
                List<Product> topProducts = pageRanking.getTopItems("cars1.csv", query);

                System.out.println("Top 10 Products:");
                int count = 0;
                for (Product product : topProducts) {
                    System.out.println(product);
                    count++;
                    if (count >= 10) break;
                }

                System.out.println("Press 1 to sort by rating or 2 to view the full list of products:");
                String choice = getInput().trim();
                if (choice.equals("1")) {
                    List<Product> allProducts = pageRanking.readProductData("cars1.csv");
                    List<Product> fp = allProducts.stream()
                            .filter(product -> product.name.toLowerCase().contains(query))
                            .collect(Collectors.toList());
                    List<Product> sortedProducts = pageRanking.rankPagesByRating(fp);

                    System.out.println("Products sorted by rating:");
                    for (Product product : sortedProducts) {
                        System.out.println(product);
                    }
                } else if (choice.equals("2")) {
                    List<Product> allProducts = pageRanking.readProductData("cars1.csv");
                    List<Product> fp = allProducts.stream()
                            .filter(product -> product.name.toLowerCase().contains(query))
                            .collect(Collectors.toList());
                    System.out.println("Full list of products:");
                    for (Product product : fp) {
                        System.out.println(product);
                    }
                } else {
                    System.out.println("Invalid option. Please choose 1 or 2.");
                }
            } else {
                System.out.println("Spelling is Wrong. Did you mean " + suggestions.get(0) + "?");
                // No need to display top 10 products if the word is incorrect
            }
        }
    }



    private static boolean containsSpecialCharacters(String input) {
        return INVALID_INPUT_PATTERN.matcher(input).find();
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
