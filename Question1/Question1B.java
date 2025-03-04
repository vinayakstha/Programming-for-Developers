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

public class Question1B {
    private int[] nums1; // First array
    private int[] nums2; // Second array

    // Returns the kth smallest product from nums1 and nums2.
    public long kthSmallestProduct(int[] nums1, int[] nums2, long k) {
        this.nums1 = nums1; // Save first array
        this.nums2 = nums2; // Save second array

        int m = nums1.length; // Length of nums1
        int n = nums2.length; // Length of nums2

        // Get maximum absolute values from the arrays.
        int a = Math.max(Math.abs(nums1[0]), Math.abs(nums1[m - 1]));
        int b = Math.max(Math.abs(nums2[0]), Math.abs(nums2[n - 1]));

        long r = (long) a * b; // Upper bound for product
        long l = (long) -a * b; // Lower bound for product

        // Binary search to find the kth smallest product.
        while (l < r) {
            long mid = (l + r) >> 1; // Find middle value
            if (count(mid) >= k) { // If there are k or more products <= mid
                r = mid; // Search left half
            } else {
                l = mid + 1; // Search right half
            }
        }
        return l; // l is the kth smallest product
    }

    // Counts how many products are <= p.
    private long count(long p) {
        long cnt = 0; // Counter for valid products
        int n = nums2.length; // Length of nums2

        // Loop over every number in nums1.
        for (int x : nums1) {
            if (x > 0) { // If x is positive
                int l = 0, r = n;
                while (l < r) {
                    int mid = (l + r) >> 1;
                    if ((long) x * nums2[mid] > p) {
                        r = mid;
                    } else {
                        l = mid + 1;
                    }
                }
                cnt += l; // Add valid count for positive x
            } else if (x < 0) { // If x is negative
                int l = 0, r = n;
                while (l < r) {
                    int mid = (l + r) >> 1;
                    if ((long) x * nums2[mid] <= p) {
                        r = mid;
                    } else {
                        l = mid + 1;
                    }
                }
                cnt += n - l; // Add valid count for negative x
            } else { // If x is zero
                if (p >= 0) {
                    cnt += n; // All products are 0 if p is non-negative
                }
            }
        }
        return cnt; // Return the total count
    }

    // Main method for testing.
    public static void main(String[] args) {
        Question1B qno1B = new Question1B(); // Create an instance

        // Test 1: kth smallest product from {2, 5} and {3, 4} where k=2 should be 8.
        System.out.println(qno1B.kthSmallestProduct(new int[] { 2, 5 }, new int[] { 3, 4 }, 2)); // Expected: 8

        // Test 2: kth smallest product from {-4, -2, 0, 3} and {2, 4} where k=6 should
        // be 0.
        System.out.println(qno1B.kthSmallestProduct(new int[] { -4, -2, 0, 3 }, new int[] { 2, 4 }, 6)); // Expected: 0
    }
}

// Output:
// 8
// 0
