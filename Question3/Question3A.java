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

public class Question3A { // Main class

    static class Edge { // Class for an edge between devices
        int device1; // First device number
        int device2; // Second device number
        int cost; // Cost of the connection

        public Edge(int device1, int device2, int cost) { // Constructor for Edge
            this.device1 = device1; // Set first device
            this.device2 = device2; // Set second device
            this.cost = cost; // Set cost
        }
    }

    static class UnionFind { // Class for the Union-Find structure
        int[] parent; // Array for each device's parent
        int[] rank; // Array for each device's rank

        public UnionFind(int n) { // Constructor for UnionFind
            parent = new int[n]; // Create parent array
            rank = new int[n]; // Create rank array
            for (int i = 0; i < n; i++) { // For each device
                parent[i] = i; // Set itself as parent
                rank[i] = 0; // Set initial rank to 0
            }
        }

        public int find(int u) { // Find root of device u
            if (parent[u] != u) { // If u is not its own parent
                parent[u] = find(parent[u]); // Recursively find and update parent
            }
            return parent[u]; // Return root of u
        }

        public boolean union(int u, int v) { // Merge sets of u and v
            int rootU = find(u); // Find u's root
            int rootV = find(v); // Find v's root
            if (rootU != rootV) { // If u and v are in different sets
                if (rank[rootU] > rank[rootV]) { // If u's tree is taller
                    parent[rootV] = rootU; // Attach v's tree under u's root
                } else if (rank[rootU] < rank[rootV]) { // If v's tree is taller
                    parent[rootU] = rootV; // Attach u's tree under v's root
                } else { // If both trees have equal height
                    parent[rootV] = rootU; // Attach v's tree under u's root
                    rank[rootU]++; // Increase u's tree rank
                }
                return true; // Union was successful
            }
            return false; // No union needed; already connected
        }
    }

    public static int minTotalCost(int n, int[] modules, int[][] connections) { // Method to compute minimum cost
        List<Edge> edges = new ArrayList<>(); // List to store all edges

        for (int[] connection : connections) { // For each connection
            edges.add(new Edge(connection[0] - 1, connection[1] - 1, connection[2])); // Create edge (convert to
                                                                                      // 0-index) and add it
        }

        edges.sort(Comparator.comparingInt(edge -> edge.cost)); // Sort edges by cost

        UnionFind uf = new UnionFind(n); // Initialize Union-Find for n devices
        int totalCost = 0; // Start total cost at 0

        for (Edge edge : edges) { // For each edge in sorted order
            if (uf.union(edge.device1, edge.device2)) { // If the devices can be connected
                totalCost += edge.cost; // Add the edge cost to total cost
            }
        }

        int modulesCost = Arrays.stream(modules).min().getAsInt(); // Get the smallest module cost
        totalCost += modulesCost; // Add module cost to the total cost

        return totalCost; // Return the minimum total cost
    }

    public static void main(String[] args) { // Main method
        int n = 3; // Total number of devices
        int[] modules = { 1, 2, 2 }; // Cost for each device's module
        int[][] connections = { { 1, 2, 1 }, { 2, 3, 1 } }; // Connections: [device1, device2, cost]

        int result = minTotalCost(n, modules, connections); // Calculate the total cost
        System.out.println("Minimum total cost to connect all devices: " + result); // Print the result
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
