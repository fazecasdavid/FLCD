package com.dici;

import com.dici.model.FiniteAutomaton;

import java.util.Scanner;

public class Main {

    public static void printMenu() {
        System.out.println("0. Read FA from file");
        System.out.println("1. Print the set of states");
        System.out.println("2. Print the alphabet");
        System.out.println("3. Print the set of final states");
        System.out.println("4. Print the initial state");
        System.out.println("5. Print the set of transitions");
        System.out.println("6. Check if sequence is accepted by FA");
        System.out.println("7. Exit");
    }

    public static void main(final String[] args) {
        printMenu();

        FiniteAutomaton fa = null;

        final Scanner scanner = new Scanner(System.in);
        while (true) {
            final String option = scanner.nextLine();
            try {
                switch (option) {
                    case "0":
                        final String filename = scanner.nextLine();
                        fa = FiniteAutomaton.readFromFile(filename);
                        break;
                    case "1":
                        System.out.println(fa.getStates());
                        break;
                    case "2":
                        System.out.println(fa.getAlphabet());
                        break;
                    case "3":
                        System.out.println(fa.getFinalStates());
                        break;
                    case "4":
                        System.out.println(fa.getInitialState());
                        break;
                    case "5":
                        for (final var transition : fa.getTransitions().entrySet()) {
                            System.out.printf("%s -> %s%n", transition.getKey(), transition.getValue());
                        }
                        break;
                    case "6":
                        final String sequence = scanner.nextLine();
                        System.out.println(fa.accepts(sequence));
                        break;
                    case "7":
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (final Exception exception) {
                System.out.println(exception);
            }
        }
    }

}