
import java.util.*; // Import necessary Java utilities (collections, queues, maps, etc.)

/*
 * Working Mechanism:
 * This program calculates the minimum number of roads (or steps) needed to collect all packages.
 * Each package is located at a node (indicated by a 1 in the packages array) in an undirected graph.
 * The graph is represented using an adjacency list built from the provided roads.
 * 
 * Steps:
 * 1. Build an adjacency list to represent the graph from the roads.
 * 2. Identify nodes that contain packages.
 * 3. For each package node, precompute its "coverage area" – all nodes reachable within 2 steps (using BFS).
 * 4. For every possible starting node, perform a BFS to determine the minimal distance to reach any node in each package's coverage area.
 * 5. For each starting node, determine the maximum distance among all package nodes (the "bottleneck").
 * 6. Compute the cost for a starting node as 2 * (that maximum distance) and keep track of the minimum cost.
 * 7. Return the minimum cost if found; otherwise, return -1.
 */

public class Question4B { // Changed class name to Question4B

    // Method to calculate the minimum roads needed to collect all packages.
    public static int minRoads(int[] packages, int[][] roads) {
        int n = packages.length; // Total number of nodes in the graph

        // Build the adjacency list for the graph.
        List<List<Integer>> adj = new ArrayList<>(); // Initialize the adjacency list.
        for (int i = 0; i < n; i++) { // For each node from 0 to n-1,
            adj.add(new ArrayList<>()); // add an empty list to hold its neighbors.
        }
        for (int[] road : roads) { // For each road (edge) in the roads array,
            int u = road[0]; // First endpoint of the road.
            int v = road[1]; // Second endpoint of the road.
            adj.get(u).add(v); // Add v as a neighbor of u.
            adj.get(v).add(u); // Add u as a neighbor of v (since the graph is undirected).
        }

        // Identify all package nodes (nodes where packages[i] == 1).
        List<Integer> packageNodes = new ArrayList<>();
        for (int i = 0; i < n; i++) { // Iterate over all nodes.
            if (packages[i] == 1) { // If a node contains a package,
                packageNodes.add(i); // add its index to packageNodes.
            }
        }
        if (packageNodes.isEmpty()) { // If there are no package nodes,
            return 0; // then no roads are needed.
        }

        // Precompute coverage areas for each package node: nodes reachable within 2
        // steps.
        Map<Integer, Set<Integer>> coverageMap = new HashMap<>();
        for (int p : packageNodes) { // For each package node p,
            Set<Integer> coverage = new HashSet<>(); // Create a set to store nodes within 2 steps.
            Queue<Integer> queue = new LinkedList<>(); // Use a queue for BFS.
            queue.offer(p); // Start BFS from node p.
            queue.offer(-1); // Marker (-1) to indicate the end of the current BFS level.
            int level = 0; // Initialize level counter.
            while (!queue.isEmpty() && level <= 2) { // Process until queue is empty or level exceeds 2.
                int node = queue.poll(); // Dequeue a node.
                if (node == -1) { // If the marker is reached,
                    level++; // increment the BFS level.
                    if (!queue.isEmpty()) { // If there are still nodes to process,
                        queue.offer(-1); // add another marker.
                    }
                    continue; // Skip further processing for the marker.
                }
                coverage.add(node); // Add the node to the coverage set.
                for (int neighbor : adj.get(node)) { // For each neighbor of the current node,
                    if (!coverage.contains(neighbor)) { // if it hasn't been added yet,
                        queue.offer(neighbor); // enqueue the neighbor.
                    }
                }
            }
            coverageMap.put(p, coverage); // Map the package node p to its computed coverage area.
        }

        int minCost = Integer.MAX_VALUE; // Initialize the minimum cost with a large value.

        // Evaluate each possible starting node (s) in the graph.
        for (int s = 0; s < n; s++) { // For every node s as a potential starting point,
            int maxDist = 0; // To store the maximum distance from s to any package's coverage area.
            boolean valid = true; // Flag to determine if s can reach all package coverage areas.
            for (int p : packageNodes) { // For each package node,
                Set<Integer> coverage = coverageMap.get(p); // Retrieve its coverage area.

                // Perform BFS from starting node s to find the minimum distance to any node in
                // the coverage.
                Queue<Integer> queue = new LinkedList<>();
                boolean[] visited = new boolean[n]; // Array to track visited nodes.
                queue.offer(s); // Enqueue the starting node s.
                visited[s] = true; // Mark s as visited.
                int dist = 0; // Initialize the distance counter.
                boolean found = false; // Flag to indicate if a node in the coverage is reached.
                while (!queue.isEmpty() && !found) { // Continue BFS until a node in coverage is found or queue empties.
                    int size = queue.size(); // Number of nodes at the current level.
                    for (int i = 0; i < size; i++) { // Process all nodes at this level.
                        int node = queue.poll(); // Dequeue the next node.
                        if (coverage.contains(node)) { // Check if the node is in the coverage area.
                            found = true; // Mark as found.
                            break; // Break out of the loop.
                        }
                        // Enqueue all unvisited neighbors.
                        for (int neighbor : adj.get(node)) {
                            if (!visited[neighbor]) {
                                visited[neighbor] = true; // Mark neighbor as visited.
                                queue.offer(neighbor); // Enqueue the neighbor.
                            }
                        }
                    }
                    if (!found) { // If no node was found at the current level,
                        dist++; // increment the distance.
                    }
                }
                if (!found) { // If no node in the coverage was reachable from s,
                    valid = false; // mark s as invalid.
                    break; // No need to check further package nodes.
                }
                maxDist = Math.max(maxDist, dist); // Update the maximum distance for this starting node.
            }
            if (valid) { // If s can reach all package coverage areas,
                minCost = Math.min(minCost, 2 * maxDist); // Calculate the cost (2 * maxDist) and update the minimum
                                                          // cost.
            }
        }
        // If no valid starting node was found, return -1; otherwise, return the minimum
        // cost.
        return minCost == Integer.MAX_VALUE ? -1 : minCost;
    }

    public static void main(String[] args) { // Main method: program entry point.
        // First example:
        int[] packages1 = { 1, 0, 0, 0, 0, 1 }; // packages array: nodes with a value 1 have a package.
        int[][] roads1 = { { 1, 0 }, { 1, 1 }, { 2, 1 }, { 2, 3 }, { 3, 4 }, { 4, 5 } }; // Roads represented as pairs
                                                                                         // (u, v).
        System.out.println(minRoads(packages1, roads1)); // Expected output: 2

        // Second example:
        int[] packages2 = { 0, 0, 0, 1, 1, 0, 0, 1 }; // packages array for 8 nodes.
        int[][] roads2 = { { 0, 1 }, { 0, 2 }, { 1, 3 }, { 1, 4 }, { 2, 5 }, { 5, 6 }, { 5, 7 } }; // Roads represented
                                                                                                   // as pairs (u, v).
        System.out.println(minRoads(packages2, roads2)); // Expected output: 2
    }
}

/*
 * Input/Output:
 * Example 1:
 * Input:
 * packages1 = {1, 0, 0, 0, 0, 1}
 * roads1 = {{1,0}, {1,1}, {2,1}, {2,3}, {3,4}, {4,5}}
 * Output:
 * 2
 * 
 * Example 2:
 * Input:
 * packages2 = {0, 0, 0, 1, 1, 0, 0, 1}
 * roads2 = {{0,1}, {0,2}, {1,3}, {1,4}, {2,5}, {5,6}, {5,7}}
 * Output:
 * 2
 */
