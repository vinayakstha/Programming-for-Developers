package Question3;

/*
 * Working Mechanism:
 * This algorithm calculates the minimum total cost to connect all devices by building a Minimum Spanning Tree (MST)
 * using Kruskal's algorithm and then adds the cost of the cheapest module.
 * 
 * It first converts each connection into an Edge object, sorts these edges by cost, and uses a Union-Find data structure
 * to efficiently connect devices while avoiding cycles. After constructing the MST, it adds the minimum module cost to
 * the total cost, ensuring the optimal solution.
 */

import java.util.*; // Import all necessary classes from the Java utility package

public class Question3A { // Define the main class Qno3A

    // Define the Edge class to represent an edge between two devices with an
    // associated cost
    static class Edge { // Begin Edge class definition
        int device1; // Declare variable for the first device in the edge
        int device2; // Declare variable for the second device in the edge
        int cost; // Declare variable for the cost of connecting the two devices

        // Constructor to initialize an Edge with given devices and cost
        public Edge(int device1, int device2, int cost) { // Edge class constructor
            this.device1 = device1; // Set the first device for this edge
            this.device2 = device2; // Set the second device for this edge
            this.cost = cost; // Set the cost associated with this edge
        }
    } // End of Edge class

    // Define the UnionFind class for the Union-Find data structure (also known as
    // Disjoint Set Union, DSU)
    static class UnionFind { // Begin UnionFind class definition
        int[] parent; // Array to keep track of the parent (representative) of each device
        int[] rank; // Array to keep track of the rank (depth) of each device's tree in the
                    // Union-Find structure

        // Constructor to initialize the UnionFind structure for n elements
        public UnionFind(int n) { // UnionFind constructor with n elements
            parent = new int[n]; // Initialize the parent array with size n
            rank = new int[n]; // Initialize the rank array with size n
            for (int i = 0; i < n; i++) { // Loop over each device from 0 to n-1
                parent[i] = i; // Initially, each device is its own parent (each is in its own set)
                rank[i] = 0; // Initially, set the rank of each device to 0
            }
        }

        // Find method to locate the representative (root) of the set containing device
        // u with path compression
        public int find(int u) { // Begin find method for device u
            if (parent[u] != u) { // If u is not its own parent, it is not the root
                parent[u] = find(parent[u]); // Recursively find the root and apply path compression
            }
            return parent[u]; // Return the root of u
        }

        // Union method to merge the sets containing devices u and v
        public boolean union(int u, int v) { // Begin union method for devices u and v
            int rootU = find(u); // Find the root of device u
            int rootV = find(v); // Find the root of device v
            if (rootU != rootV) { // If the roots are different, u and v are in different sets
                // Perform union by rank to keep tree shallow
                if (rank[rootU] > rank[rootV]) { // If the tree of u has higher rank than that of v
                    parent[rootV] = rootU; // Attach v's tree under u's tree
                } else if (rank[rootU] < rank[rootV]) { // If the tree of v has higher rank than that of u
                    parent[rootU] = rootV; // Attach u's tree under v's tree
                } else { // If both trees have the same rank
                    parent[rootV] = rootU; // Attach v's tree under u's tree
                    rank[rootU]++; // Increment the rank of u's tree since its height increases
                }
                return true; // The union was successful
            }
            return false; // Devices u and v are already in the same set; union not performed
        }
    } // End of UnionFind class

    // Method to calculate the minimum total cost to connect all devices
    public static int minTotalCost(int n, int[] modules, int[][] connections) { // Define the minTotalCost method with
                                                                                // parameters n, modules, and
                                                                                // connections
        List<Edge> edges = new ArrayList<>(); // Create a list to store Edge objects representing each connection

        // Convert each connection from the input into an Edge object and add it to the
        // list
        for (int[] connection : connections) { // Loop over each connection in the connections array
            edges.add(new Edge(connection[0] - 1, connection[1] - 1, connection[2])); // Create an Edge object (convert
                                                                                      // to 0-indexed) and add it to the
                                                                                      // list
        }

        // Sort the list of edges in increasing order based on their cost (for use in
        // Kruskal's algorithm)
        edges.sort(Comparator.comparingInt(edge -> edge.cost)); // Sort edges by their cost using a lambda comparator

        UnionFind uf = new UnionFind(n); // Initialize the UnionFind structure for n devices
        int totalCost = 0; // Initialize the total cost of the Minimum Spanning Tree (MST) to 0

        // Process each edge in the sorted order to build the MST using Kruskal's
        // algorithm
        for (Edge edge : edges) { // Loop over each edge in the sorted list
            if (uf.union(edge.device1, edge.device2)) { // If the edge connects two separate components (union is
                                                        // successful)
                totalCost += edge.cost; // Add the cost of the edge to the total cost
            }
        }

        // Add the cost of the cheapest module to the total cost
        int modulesCost = Arrays.stream(modules).min().getAsInt(); // Find the minimum module cost using Java streams
        totalCost += modulesCost; // Add the cheapest module cost to the total cost

        return totalCost; // Return the computed total minimum cost to connect all devices
    }

    // Main method to run the program and test the minTotalCost function
    public static void main(String[] args) { // Define the main method
        int n = 3; // Number of devices
        int[] modules = { 1, 2, 2 }; // Array representing the cost of modules for each device
        int[][] connections = { { 1, 2, 1 }, { 2, 3, 1 } }; // 2D array representing the connections (device1, device2,
                                                            // cost)

        // Call the minTotalCost method to compute the minimum total cost to connect all
        // devices
        int result = minTotalCost(n, modules, connections); // Store the result from minTotalCost

        // Print the result to the console
        System.out.println("Minimum total cost to connect all devices: " + result); // Expected output: 4
    }
}

/*
 * Input/Output:
 * Input:
 * Number of devices, n = 3
 * Modules cost array: {1, 2, 2}
 * Connections array: {{1, 2, 1}, {2, 3, 1}}
 * 
 * Output:
 * Minimum total cost to connect all devices: 4
 */
