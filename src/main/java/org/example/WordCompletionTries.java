package org.example;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.InputStreamReader;

public class WordCompletionTries {

    // Every node in the Trie is represented by the TrieNode class.
    static class TrieNode {
        Map<Character, TrieNode> children; // Map for storing offspring nodes
        boolean isEndOfWord; // Symbol to indicate the end of a word

        // Initialize the constructor TrieNode
        public TrieNode() {
            this.children = new HashMap<>(); // Set up the children's map initially.
            this.isEndOfWord = false; // Set the end of word flag to its initial value.
        }
    }

    // The Trie data structure is represented by the Trie class.
    static class Trie {
        private TrieNode root; // the Trie's root node

        // Initialize the constructor Attempt using the root node
        public Trie() {
            this.root = new TrieNode(); // Set up the Trie's root node initially.
        }

        // Technique for adding a word to the Trie
        public void insert(String word) {
            TrieNode current = root; // Commence at the base
            word = word.toUpperCase(); // Change to capital letters due to case insensitivity

            // Go over every character in the phrase.
            for (int i = 0; i < word.length(); i++) {
                char ch = word.charAt(i); // Obtain the current persona.
                TrieNode node = current.children.get(ch); // Verify whether the character is a child node.

                // Make a new node for the character if it does not already exist.
                if (node == null) {
                    node = new TrieNode();
                    current.children.put(ch, node); // Insert a new node in the kids' map
                }
                current = node; // Proceed to the following node.
            }
            current.isEndOfWord = true; // Declare the final node to be the word's conclusion.
        }

        // How to look up every word in the Trie that begins with a specific prefix
        // Prefix is the parameter to be searched for.
        public List<String> searchPrefix(String prefix) {
            List<String> results = new ArrayList<>(); // List to store matching words
            TrieNode current = root; // Start from the root

            prefix = prefix.toUpperCase(); // Prefix should be changed to uppercase for case insensitivity.

            // Go over every character in the prefix.
            for (int i = 0; i < prefix.length(); i++) {
                char ch = prefix.charAt(i); // Obtain the current persona.
                TrieNode node = current.children.get(ch); // Verify whether the character is a child node.

                // Return empty results if the character does not exist (no words match the prefix).
                if (node == null) {
                    return results;
                }
                current = node; // Proceed to the following node.
            }

            // Presently, current is pointing to the node that represents the prefix's final character.
            findAllWords(current, prefix, results); // Look up every word that begins with this node.
            return results; // Provide the list of words that match.
        }

        // Recursive approach to locate all words starting from a given node with an aid
        // Param: node - The current node in the Trie
        // Param: prefix - The prefix accumulated so far
        // Param: results - List to store matching words
        private void findAllWords(TrieNode node, String prefix, List<String> results) {
            // Include the node in the results list if it indicates the end of a term.
            if (node.isEndOfWord) {
                results.add(prefix);
            }
            // Go through each child node recursively
            for (char ch : node.children.keySet()) {
                findAllWords(node.children.get(ch), prefix + ch, results);
            }
        }
    }

    // The primary way to use the word completion program
    public static void main(String[] args) {
        Trie trie = new Trie(); // Launch a fresh instance of Trie.
        String csvFile = "cars1.csv";
        String line = ""; // each line that is read from the CSV into a string.
        String cvsSplitBy = ","; // CSV delimiter

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Read each line from the CSV file
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy); // Split the line by commas
                if (data.length > 0) {
                    String carModel = data[0].trim(); // Obtain the model of the car (assuming it is the first column).
                    trie.insert(carModel); // Put the automobile model into the Trie
                }
            }

            // Set up a console reader to accept input from users.
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.print("Enter prefix (type 'exit' to quit): ");
                String prefix = consoleReader.readLine().trim(); // Check for prefixes in user input.

                // 'exit' is typed by the user to quit the condition
                if (prefix.equalsIgnoreCase("exit")) {
                    break;
                }

                // Search for words starting with the entered prefix
                List<String> results = trie.searchPrefix(prefix);

                // Depending on whether any words were found, display the findings.
                if (results.isEmpty()) {
                    System.out.println("No words found with prefix '" + prefix + "'");
                } else {
                    System.out.println("Words found with prefix '" + prefix + "':");
                    for (String result : results) {
                        System.out.println(result); // Print each word found
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace(); // For any IO exceptions, print the stack trace.
        }
    }
}
// summary
/**
 * Define Trie Classes and TrieNode: Make a Trie class with methods to input words and search for prefixes, and a TrieNode class with a map of children and an end-of-word flag.
 *
 *Insert Words into Trie: Use the insert technique to enter each name of a car model that you read from a CSV file into the Trie.
 *
 * Find all words in the Trie that begin with a specific prefix by using the searchPrefix method, which ensures case insensitivity.
 *
 * Find All Words: To recursively gather all words beginning from a specific Trie node, use the helper method findAllWords.
 *
 * Interactive User Input: Continuously prompt the user to enter a prefix, search for matching words in the Trie, and display the results, or indicate if no matches are found.
 * */