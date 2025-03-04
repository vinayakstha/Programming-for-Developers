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

public class Question1A { // Start of class

    // Function to calculate the minimum number of measurements needed
    public static int minMeasurements(int k, int n) { // Start of minMeasurements method
        int[][] dp = new int[k + 1][n + 1]; // Create a DP table of size (k+1) x (n+1)
        int s = 0; // Start with 0 measurements

        while (dp[k][s] < n) { // Loop until we cover n levels
            s++; // Increase measurement count by 1

            for (int samples = 1; samples <= k; samples++) { // For each material from 1 to k
                // Update DP: 1 current measurement + if it breaks + if it survives
                dp[samples][s] = 1 + dp[samples - 1][s - 1] + dp[samples][s - 1];
            }
        }

        return s; // Return the minimum measurements needed
    }

    public static void main(String[] args) { // Start of main method
        System.out.println("k = 1, n = 2: " + minMeasurements(1, 2)); // Test case: 1 material, 2 levels
        System.out.println("k = 2, n = 6: " + minMeasurements(2, 6)); // Test case: 2 materials, 6 levels
        System.out.println("k = 3, n = 14: " + minMeasurements(3, 14)); // Test case: 3 materials, 14 levels
    }
}

/*
 * Input/Output:
 * k = 1, n = 2: 2
 * k = 2, n = 6: 3
 * k = 3, n = 14: 4
 */
