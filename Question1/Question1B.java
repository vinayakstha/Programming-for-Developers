package Question1;
/*
 * Working Mechanism:
 * This algorithm finds the kth smallest product by selecting one element from nums1 and one element from nums2.
 * It uses binary search over the possible range of products.
 * The range is determined using the maximum absolute values from nums1 and nums2.
 * For each candidate product value (mid), the count() method calculates the number of products less than or equal to mid.
 * The count() method uses binary search for positive and negative numbers in nums1 to efficiently count valid products from nums2.
 * The binary search in kthSmallestProduct narrows the range until it finds the smallest product value for which the count is at least k.
 */

public class Question1B { // Define a class named Qno1B
    private int[] nums1; // Declare an array to hold the first set of numbers (nums1)
    private int[] nums2; // Declare an array to hold the second set of numbers (nums2)

    // Method to find the kth smallest product from nums1 and nums2
    public long kthSmallestProduct(int[] nums1, int[] nums2, long k) { // Define method kthSmallestProduct with inputs
                                                                       // nums1, nums2, and kth value k
        this.nums1 = nums1; // Assign the input array nums1 to the class-level variable nums1
        this.nums2 = nums2; // Assign the input array nums2 to the class-level variable nums2

        int m = nums1.length; // Get the length of nums1 and store it in m
        int n = nums2.length; // Get the length of nums2 and store it in n

        // Determine the maximum absolute value in nums1 by comparing the first and last
        // elements
        int a = Math.max(Math.abs(nums1[0]), Math.abs(nums1[m - 1]));
        // Determine the maximum absolute value in nums2 by comparing the first and last
        // elements
        int b = Math.max(Math.abs(nums2[0]), Math.abs(nums2[n - 1]));

        // Set the initial binary search boundaries for the product range
        long r = (long) a * b; // Set the upper bound r as the product of a and b
        long l = (long) -a * b; // Set the lower bound l as the negative product of a and b

        // Perform binary search to find the kth smallest product
        while (l < r) { // Continue searching while the lower bound is less than the upper bound
            long mid = (l + r) >> 1; // Compute the middle value (mid) of the current range using bitwise shift
                                     // (dividing by 2)
            if (count(mid) >= k) { // If the number of products <= mid is at least k
                r = mid; // Narrow the search range by setting the upper bound r to mid
            } else { // Otherwise, if the count is less than k
                l = mid + 1; // Narrow the search range by setting the lower bound l to mid + 1
            }
        }

        return l; // Return l, which is the kth smallest product once the search completes
    }

    // Helper method to count the number of products less than or equal to a given
    // value p
    private long count(long p) { // Define the count method with parameter p
        long cnt = 0; // Initialize the counter to store the number of valid products
        int n = nums2.length; // Get the length of nums2 and store it in n

        // Loop through each element x in nums1
        for (int x : nums1) { // For each element x in nums1
            if (x > 0) { // If x is positive
                int l = 0, r = n; // Initialize binary search boundaries for nums2 (from 0 to n)
                while (l < r) { // Perform binary search while the range is valid
                    int mid = (l + r) >> 1; // Compute the middle index of nums2 using bitwise shift
                    if ((long) x * nums2[mid] > p) { // If the product of x and nums2[mid] is greater than p
                        r = mid; // Narrow the search range to the left by setting r to mid
                    } else { // Otherwise, if the product is less than or equal to p
                        l = mid + 1; // Move the lower boundary to mid + 1 to search the right half
                    }
                }
                cnt += l; // Add the number of valid products for positive x (l products) to cnt
            } else if (x < 0) { // If x is negative
                int l = 0, r = n; // Initialize binary search boundaries for nums2
                while (l < r) { // Perform binary search while the range is valid
                    int mid = (l + r) >> 1; // Compute the middle index of nums2 using bitwise shift
                    if ((long) x * nums2[mid] <= p) { // If the product of x and nums2[mid] is less than or equal to p
                        r = mid; // Narrow the search range to the left by setting r to mid
                    } else { // Otherwise, if the product is greater than p
                        l = mid + 1; // Move the lower boundary to mid + 1 to search the right half
                    }
                }
                cnt += n - l; // Add the number of valid products for negative x (n - l products) to cnt
            } else if (x == 0) { // If x is zero
                if (p >= 0) { // Check if p is non-negative (since 0 is <= any non-negative p)
                    cnt += n; // All products will be 0; add n valid products to cnt
                }
                // If p is negative, do not add any count because 0 > p
            }
        }

        return cnt; // Return the total count of products that are less than or equal to p
    }

    // Main method to test the kthSmallestProduct method
    public static void main(String[] args) { // Define the main method
        Question1B qno1B = new Question1B(); // Create an instance of Qno1B

        // Test Case 1: Find the 2nd smallest product from arrays {2, 5} and {3, 4}
        System.out.println(qno1B.kthSmallestProduct(new int[] { 2, 5 }, new int[] { 3, 4 }, 2)); // Expected output: 8

        // Test Case 2: Find the 6th smallest product from arrays {-4, -2, 0, 3} and {2,
        // 4}
        System.out.println(qno1B.kthSmallestProduct(new int[] { -4, -2, 0, 3 }, new int[] { 2, 4 }, 6)); // Expected
                                                                                                         // output: 0
    }
}

// Output:
// 8
// 0
