package Question2;
/*
 * Working Mechanism:
 * This algorithm finds the pair of points with the smallest Manhattan distance.
 * Manhattan distance between two points is computed as the absolute difference in x coordinates
 * plus the absolute difference in y coordinates.
 * The algorithm considers every unique pair of points (i, j) with i < j.
 * If multiple pairs have the same Manhattan distance, the lexicographically smallest pair (by indices) is chosen.
 */

import java.util.Arrays; // Import Arrays class for printing array as a string

public class Question2B { // Define a class named QNo2B

    // Method to find the closest lexicographical pair of points based on Manhattan
    // distance
    public static int[] closestLexicographicalPair(int[] x_coords, int[] y_coords) { // Define a static method taking
                                                                                     // two coordinate arrays
        int n = x_coords.length; // Get the number of points from the x_coords array
        int minDistance = Integer.MAX_VALUE; // Initialize minDistance to the maximum integer value
        int[] result = new int[2]; // Create an array to store the indices of the closest pair

        // Iterate through all pairs of points (i, j) where i < j
        for (int i = 0; i < n; i++) { // Loop over points with index i from 0 to n-1
            for (int j = i + 1; j < n; j++) { // Loop over points with index j from i+1 to n-1
                // Calculate the Manhattan distance between points i and j
                int distance = Math.abs(x_coords[i] - x_coords[j]) + Math.abs(y_coords[i] - y_coords[j]); // Compute
                                                                                                          // Manhattan
                                                                                                          // distance

                // If a smaller distance is found, update minDistance and store the indices in
                // result
                if (distance < minDistance) { // Check if current distance is less than minDistance
                    minDistance = distance; // Update minDistance with the current distance
                    result[0] = i; // Set the first index of the result to i
                    result[1] = j; // Set the second index of the result to j
                }
                // If the same distance is found, check lexicographical order of indices
                else if (distance == minDistance) { // Check if current distance equals minDistance
                    // If current pair (i, j) is lexicographically smaller than the stored pair,
                    // update result
                    if (i < result[0] || (i == result[0] && j < result[1])) { // Compare indices lexicographically
                        result[0] = i; // Update first index in result to i
                        result[1] = j; // Update second index in result to j
                    }
                }
            }
        }
        return result; // Return the indices of the closest lexicographical pair
    }

    public static void main(String[] args) { // Main method to test the functionality
        int[] x_coords = { 1, 2, 3, 2, 4 }; // Define the x-coordinates of the points
        int[] y_coords = { 2, 3, 1, 2, 3 }; // Define the y-coordinates of the points

        int[] result = closestLexicographicalPair(x_coords, y_coords); // Call the method to find the closest pair
        System.out.println(Arrays.toString(result)); // Print the result array; Expected output: [0, 3]
    }
}

/*
 * Input/Output:
 * Input:
 * x_coords = [1, 2, 3, 2, 4]
 * y_coords = [2, 3, 1, 2, 3]
 * 
 * Output:
 * [0, 3]
 */
