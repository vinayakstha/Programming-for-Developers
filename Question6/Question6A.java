package Question6;

/*
 * Working Mechanism:
 * This program prints a sequence "0102030405" (for n = 5) by coordinating three threads:
 * - ZeroThread prints "0".
 * - OddThread prints odd numbers.
 * - EvenThread prints even numbers.
 * Semaphores are used to ensure that the threads print in the correct order.
 *
 * For n = 5, the output will be: 0,1,0,2,0,3,0,4,0,5 (without commas: "0102030405").
 */

import java.util.concurrent.Semaphore; // Import Semaphore for thread synchronization

public class Question6A {
    private Semaphore zeroSem = new Semaphore(1); // Allow ZeroThread to start immediately
    private Semaphore evenSem = new Semaphore(0); // EvenThread waits initially
    private Semaphore oddSem = new Semaphore(0); // OddThread waits initially

    private int n; // Upper limit for printing numbers
    private NumberPrinter printer; // NumberPrinter instance to print numbers

    // Constructor: sets n and the NumberPrinter instance
    public Question6A(int n, NumberPrinter printer) {
        this.n = n; // Set the maximum number to print
        this.printer = printer; // Set the printer
    }

    // ZeroThread method: prints "0" before each number
    public void printZero() throws InterruptedException {
        for (int i = 0; i <= n; i++) { // Loop to print n+1 zeros (one before each number)
            zeroSem.acquire(); // Wait for permission to print 0
            printer.printZero(); // Print 0
            if (i < n) { // If more numbers to print
                if (i % 2 == 0) { // Even iteration: next number is odd
                    oddSem.release(); // Allow OddThread to print
                } else { // Odd iteration: next number is even
                    evenSem.release(); // Allow EvenThread to print
                }
            }
        }
    }

    // EvenThread method: prints even numbers
    public void printEven() throws InterruptedException {
        for (int i = 2; i <= n; i += 2) { // Loop through even numbers from 2 to n
            evenSem.acquire(); // Wait for permission to print an even number
            printer.printEven(i); // Print the even number
            zeroSem.release(); // Signal ZeroThread for the next zero
        }
    }

    // OddThread method: prints odd numbers
    public void printOdd() throws InterruptedException {
        for (int i = 1; i <= n; i += 2) { // Loop through odd numbers from 1 to n
            oddSem.acquire(); // Wait for permission to print an odd number
            printer.printOdd(i); // Print the odd number
            zeroSem.release(); // Signal ZeroThread for the next zero
        }
    }

    // Main method to start threads and test the printing sequence
    public static void main(String[] args) {
        int n = 5; // Set the maximum number (example: 5)
        NumberPrinter printer = new NumberPrinter(); // Create a NumberPrinter instance
        Question6A controller = new Question6A(n, printer); // Create ThreadController

        // Create and start ZeroThread
        Thread zeroThread = new Thread(() -> {
            try {
                controller.printZero();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Create and start EvenThread
        Thread evenThread = new Thread(() -> {
            try {
                controller.printEven();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Create and start OddThread
        Thread oddThread = new Thread(() -> {
            try {
                controller.printOdd();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Start all threads
        zeroThread.start();
        evenThread.start();
        oddThread.start();

        // Wait for all threads to finish
        try {
            zeroThread.join();
            evenThread.join();
            oddThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// Provided NumberPrinter class; do not modify.
class NumberPrinter {
    public void printZero() {
        System.out.print("0"); // Print 0 without newline
    }

    public void printEven(int num) {
        System.out.print(num); // Print the even number without newline
    }

    public void printOdd(int num) {
        System.out.print(num); // Print the odd number without newline
    }
}

/*
 * Input:
 * n = 5 (set in the main method)
 * 
 * Output:
 * 0102030405
 * 
 * Explanation:
 * - ZeroThread prints "0" before each number.
 * - OddThread prints odd numbers (1, 3, 5) when allowed.
 * - EvenThread prints even numbers (2, 4) when allowed.
 * - The semaphores ensure the output order is "0 1 0 2 0 3 0 4 0 5" (printed
 * without spaces).
 */
