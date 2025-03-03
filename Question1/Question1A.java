package Question1;
/*
 * Working Mechanism:
 * This algorithm calculates the minimum number of measurements needed to determine the highest safe temperature level
 * using a given number of available materials (k) over n temperature levels. 
 *
 * The DP table, dp[i][j], represents the maximum number of temperature levels that can be tested with i materials and j measurements.
 * The recurrence relation used is:
 *
 * dp[samples][s] = 1 + dp[samples - 1][s - 1] + dp[samples][s - 1]
 *
 * This equation covers two scenarios:
 * - When the material breaks: dp[samples - 1][s - 1] is added (one less material and one less measurement).
 * - When the material survives: dp[samples][s - 1] is added (same number of materials but one less measurement).
 * The "+1" accounts for the current measurement.
 *
 * The algorithm increments the measurement count (s) until dp[k][s] is at least n, ensuring that all temperature levels are covered.
 */

public class Question1A { // Define the public class Question1A

    // Function to calculate minimum measurements required
    /*
     * Parameters:
     * k = number of available materials
     * n = number of temperature levels to cover
     */
    public static int minMeasurements(int k, int n) { // Define a static method minMeasurements that returns an int
        int[][] dp = new int[k + 1][n + 1]; // Create a 2D array (DP table) with dimensions (k+1) x (n+1)
        int s = 0; // Initialize the number of measurements to 0

        // Loop until the DP table entry for k materials and s measurements covers all
        // temperature levels (dp[k][s] >= n)
        while (dp[k][s] < n) { // Continue iterating as long as dp[k][s] is less than n
            s++; // Increment the measurement count by 1

            // For each number of available materials from 1 to k, update the DP table
            for (int samples = 1; samples <= k; samples++) { // Loop through each sample count from 1 to k
                // Update the DP table using the recurrence relation:
                // dp[samples][s] = 1 (current measurement) + dp[samples - 1][s - 1] (if the
                // material breaks)
                // + dp[samples][s - 1] (if the material survives)
                dp[samples][s] = 1 + dp[samples - 1][s - 1] + dp[samples][s - 1]; // Compute the value for
                                                                                  // dp[samples][s]
            }
        }

        return s; // Return the minimum number of measurements required to cover n temperature
                  // levels with k materials
    }

    public static void main(String[] args) { // Main method: the entry point of the program
        // Test case 1: With 1 material and 2 temperature levels, output the result of
        // minMeasurements
        System.out.println("k = 1, n = 2: " + minMeasurements(1, 2)); // Expected output: 2

        // Test case 2: With 2 materials and 6 temperature levels, output the result of
        // minMeasurements
        System.out.println("k = 2, n = 6: " + minMeasurements(2, 6)); // Expected output: 3

        // Test case 3: With 3 materials and 14 temperature levels, output the result of
        // minMeasurements
        System.out.println("k = 3, n = 14: " + minMeasurements(3, 14)); // Expected output: 4
    }
}

/*
 * Input/Output:
 * k = 1, n = 2: 2
 * k = 2, n = 6: 3
 * k = 3, n = 14: 4
 */
