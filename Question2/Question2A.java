package Question2;
/*
 * Working Mechanism:
 * This algorithm calculates the minimum number of rewards needed for employees based on their ratings.
 * Each employee must receive at least one reward. Additionally, if an employee's rating is higher than
 * that of an adjacent employee, they must receive more rewards than that neighbor.
 *
 * The algorithm works in three main steps:
 * 1. Initialize: Assign each employee 1 reward.
 * 2. Left-to-Right Pass: For each employee (starting from the second), if the rating is higher than the previous,
 *    update the reward to be one more than the previous employee's reward.
 * 3. Right-to-Left Pass: For each employee (starting from the second last), if the rating is higher than the next,
 *    update the reward to be the maximum of its current reward and one more than the next employee's reward.
 *
 * Finally, the algorithm sums up the rewards and returns the total.
 */

public class Question2A { // Define the class Question2A

    // Function to calculate the minimum number of rewards based on employee ratings
    public static int minRewards(int[] ratings) { // Define static method minRewards with input array ratings
        int n = ratings.length; // Store the number of employees in variable n
        int[] rewards = new int[n]; // Create an array 'rewards' of size n to hold the reward for each employee

        // Step 1: Initialize each employee's reward to 1
        for (int i = 0; i < n; i++) { // Loop through all employees from index 0 to n-1
            rewards[i] = 1; // Assign 1 reward to the current employee
        }

        // Step 2: Left-to-Right Pass to adjust rewards
        for (int i = 1; i < n; i++) { // Start from the second employee (index 1) and go to the end
            if (ratings[i] > ratings[i - 1]) { // If the current employee's rating is higher than the previous
                                               // employee's rating
                rewards[i] = rewards[i - 1] + 1; // Set current employee's reward to one more than the previous
                                                 // employee's reward
            }
        }

        // Step 3: Right-to-Left Pass to adjust rewards further
        for (int i = n - 2; i >= 0; i--) { // Start from the second last employee and move backwards to the first
            if (ratings[i] > ratings[i + 1]) { // If the current employee's rating is higher than the next employee's
                                               // rating
                // Update reward: choose the maximum between the current reward and one more
                // than the next employee's reward
                rewards[i] = Math.max(rewards[i], rewards[i + 1] + 1);
            }
        }

        int totalRewards = 0; // Initialize a variable to hold the total number of rewards
        for (int reward : rewards) { // Loop through each reward in the rewards array
            totalRewards += reward; // Add the current reward to totalRewards
        }

        return totalRewards; // Return the computed total number of rewards
    }

    // Main method to test the minRewards function
    public static void main(String[] args) { // Define the main method
        int[] ratings1 = { 1, 0, 2 }; // Define the first test case input array with ratings
        // Print the minimum rewards needed for the first test case
        System.out.println("Minimum rewards for ratings [1, 0, 2] is " + minRewards(ratings1));

        int[] ratings2 = { 1, 2, 2 }; // Define the second test case input array with ratings
        // Print the minimum rewards needed for the second test case
        System.out.println("Minimum rewards for ratings [1, 2, 2] is " + minRewards(ratings2));
    }
}

/*
 * Input/Output:
 * Input:
 * For the first test case, ratings1 = [1, 0, 2].
 * For the second test case, ratings2 = [1, 2, 2].
 * 
 * Output:
 * Minimum rewards for ratings [1, 0, 2] is 5
 * Minimum rewards for ratings [1, 2, 2] is 4
 */
