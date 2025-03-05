package Question5;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
/*
 * Network Optimizer Program:
 * --------------------------
 * This GUI-based application allows users to build and visualize a network graph.
 * - Users add nodes (representing servers/clients) and edges (connections with cost and bandwidth).
 * - The graph is drawn on a custom panel.
 * - Users can "optimize" the network by computing a Minimum Spanning Tree (MST) (placeholder implementation)
 *   and can also find the shortest path based on inverse bandwidth (again, a placeholder).
 * - Results such as total cost and latency are displayed on the GUI.
 */

class Question5 extends JFrame {
    private Graph graph;
    private JTextArea outputArea;
    private JTextField nodeField1, nodeField2, costField, bandwidthField;
    private GraphPanel graphPanel; // Panel to display the graph

    public Question5() {
        setTitle("Network Optimization"); // Set the title of the JFrame
        setSize(800, 600); // Set the size of the JFrame window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the default close operation
        setLayout(new BorderLayout()); // Set the layout of the JFrame

        graph = new Graph(); // Initialize a new Graph object
        outputArea = new JTextArea(10, 50); // Create a JTextArea for displaying output
        outputArea.setEditable(false); // Make the JTextArea non-editable
        JScrollPane scrollPane = new JScrollPane(outputArea); // Wrap the JTextArea in a JScrollPane

        JPanel controlPanel = new JPanel(); // Create a panel for controls (input fields and buttons)
        nodeField1 = new JTextField(5); // Create a text field for the first node
        nodeField2 = new JTextField(5); // Create a text field for the second node
        costField = new JTextField(5); // Create a text field for the cost of the edge
        bandwidthField = new JTextField(5); // Create a text field for the bandwidth of the edge
        JButton addEdgeButton = new JButton("Add Connection"); // Create a button to add an edge
        JButton optimizeButton = new JButton("Optimize Network"); // Create a button to optimize the network
        JButton shortestPathButton = new JButton("Find Shortest Path"); // Create a button to find the shortest path

        // Add labels and fields for node1, node2, cost, and bandwidth to the control
        // panel
        controlPanel.add(new JLabel("Node 1:"));
        controlPanel.add(nodeField1);
        controlPanel.add(new JLabel("Node 2:"));
        controlPanel.add(nodeField2);
        controlPanel.add(new JLabel("Cost:"));
        controlPanel.add(costField);
        controlPanel.add(new JLabel("Bandwidth:"));
        controlPanel.add(bandwidthField);
        controlPanel.add(addEdgeButton); // Add the "Add Connection" button
        controlPanel.add(optimizeButton); // Add the "Optimize Network" button
        controlPanel.add(shortestPathButton); // Add the "Find Shortest Path" button

        // Add the graph panel (where the graph will be drawn) to the JFrame
        graphPanel = new GraphPanel(); // Create the graph panel
        add(graphPanel, BorderLayout.CENTER); // Add it to the center of the layout
        add(controlPanel, BorderLayout.NORTH); // Add control panel to the top of the layout
        add(scrollPane, BorderLayout.SOUTH); // Add the scrollable output area to the bottom of the layout

        // Define action listeners for each button
        addEdgeButton.addActionListener(e -> addEdge()); // Action for adding an edge
        optimizeButton.addActionListener(e -> optimizeNetwork()); // Action for optimizing the network
        shortestPathButton.addActionListener(e -> findShortestPath()); // Action for finding the shortest path
    }

    private void addEdge() {
        String node1 = nodeField1.getText(); // Get the first node name from the text field
        String node2 = nodeField2.getText(); // Get the second node name from the text field
        int cost = Integer.parseInt(costField.getText()); // Parse the cost from the text field
        int bandwidth = Integer.parseInt(bandwidthField.getText()); // Parse the bandwidth from the text field
        graph.addEdge(node1, node2, cost, bandwidth); // Add the edge to the graph
        outputArea.append(
                "Added connection: " + node1 + " - " + node2 + " (Cost: " + cost + ", Bandwidth: " + bandwidth + ")\n"); // Append
                                                                                                                         // the
                                                                                                                         // result
                                                                                                                         // to
                                                                                                                         // the
                                                                                                                         // output
                                                                                                                         // area

        // Repaint the graph after adding an edge
        graphPanel.repaint(); // Repaint the graph panel to reflect the added edge
    }

    private void optimizeNetwork() {
        List<Edge> mst = graph.findMinimumSpanningTree(); // Get the minimum spanning tree (MST)
        outputArea.append("\nOptimized Network (Minimum Cost Spanning Tree):\n"); // Print optimization heading
        for (Edge edge : mst) {
            outputArea.append(edge.node1 + " - " + edge.node2 + " (Cost: " + edge.cost + ")\n"); // Print each edge in
                                                                                                 // the MST
        }

        // Repaint the graph after optimizing the network
        graphPanel.repaint(); // Repaint the graph panel to show the optimized network
    }

    private void findShortestPath() {
        String start = nodeField1.getText(); // Get the starting node from the text field
        String end = nodeField2.getText(); // Get the ending node from the text field
        int distance = graph.findShortestPath(start, end); // Find the shortest path between the nodes
        outputArea.append("\nShortest path from " + start + " to " + end + " is " + distance + " units.\n"); // Print
                                                                                                             // the
                                                                                                             // result
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Question5().setVisible(true)); // Launch the GUI
    }

    // Custom JPanel for drawing the graph
    class GraphPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Call the superclass's paint method to ensure proper rendering

            // Set graphics properties
            g.setColor(Color.BLACK); // Set the drawing color to black
            // Call the graph's drawing method to visualize nodes and edges
            graph.drawGraph(g); // Draw the graph on the panel
        }
    }
}

class Graph {
    private Map<String, List<Edge>> adjList = new HashMap<>(); // Adjacency list for storing the graph

    public void addEdge(String node1, String node2, int cost, int bandwidth) {
        adjList.putIfAbsent(node1, new ArrayList<>()); // Add the first node if it does not exist
        adjList.putIfAbsent(node2, new ArrayList<>()); // Add the second node if it does not exist
        adjList.get(node1).add(new Edge(node1, node2, cost, bandwidth)); // Add the edge to the adjacency list for node1
        adjList.get(node2).add(new Edge(node2, node1, cost, bandwidth)); // Add the edge to the adjacency list for node2
    }

    public List<Edge> findMinimumSpanningTree() {
        List<Edge> edges = new ArrayList<>(); // List to store all edges
        for (List<Edge> list : adjList.values()) {
            edges.addAll(list); // Add all edges from the adjacency list to the edges list
        }
        edges.sort(Comparator.comparingInt(e -> e.cost)); // Sort the edges by cost in ascending order

        List<Edge> mst = new ArrayList<>(); // List to store the minimum spanning tree
        UnionFind uf = new UnionFind(adjList.keySet()); // Create a UnionFind structure for disjoint sets
        for (Edge edge : edges) {
            if (uf.union(edge.node1, edge.node2)) { // If the nodes of the edge are not in the same set, add the edge to
                                                    // the MST
                mst.add(edge); // Add the edge to the MST
            }
        }
        return mst; // Return the minimum spanning tree
    }

    public int findShortestPath(String start, String end) {
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.cost)); // Priority queue for nodes,
                                                                                            // sorted by cost
        Map<String, Integer> distances = new HashMap<>(); // Map to store the shortest distance to each node
        Set<String> visited = new HashSet<>(); // Set to keep track of visited nodes

        distances.put(start, 0); // Set the distance to the start node to 0
        pq.add(new Node(start, 0)); // Add the start node to the priority queue

        while (!pq.isEmpty()) {
            Node current = pq.poll(); // Get the node with the smallest distance from the queue
            if (!visited.add(current.name)) // Skip if the node is already visited
                continue;
            if (current.name.equals(end)) // If the current node is the destination, return the distance
                return current.cost;

            for (Edge neighbor : adjList.getOrDefault(current.name, Collections.emptyList())) { // For each neighbor of
                                                                                                // the current node
                int newDist = current.cost + neighbor.cost; // Calculate the new distance to the neighbor
                if (newDist < distances.getOrDefault(neighbor.node2, Integer.MAX_VALUE)) { // If the new distance is
                                                                                           // shorter, update it
                    distances.put(neighbor.node2, newDist); // Update the distance for the neighbor
                    pq.add(new Node(neighbor.node2, newDist)); // Add the neighbor to the priority queue
                }
            }
        }
        return -1; // Return -1 if no path is found
    }

    // Method to draw the graph (nodes and edges) on the panel
    public void drawGraph(Graphics g) {
        int nodeRadius = 20; // Radius for drawing the nodes
        Map<String, Point> nodePositions = new HashMap<>(); // Map to store the positions of the nodes
        int x = 100, y = 100; // Starting position for drawing the nodes

        // Example: Draw nodes and edges
        for (String node : adjList.keySet()) {
            nodePositions.put(node, new Point(x, y)); // Store the position of each node
            g.setColor(Color.BLUE); // Set the color for nodes
            g.fillOval(x - nodeRadius, y - nodeRadius, nodeRadius * 2, nodeRadius * 2); // Draw the node as a circle
            g.setColor(Color.BLACK); // Set the color for the text
            g.drawString(node, x - nodeRadius / 2, y - nodeRadius); // Draw the node's name

            x += 150; // Move to the next x position for the next node
            if (x > 500) { // If x position exceeds the width, move to the next row
                x = 100;
                y += 150; // Move down to the next row
            }
        }

        // Draw edges (connections between nodes)
        for (String node : adjList.keySet()) {
            Point node1Pos = nodePositions.get(node); // Get the position of the first node
            for (Edge edge : adjList.get(node)) {
                Point node2Pos = nodePositions.get(edge.node2); // Get the position of the second node
                g.setColor(Color.RED); // Set the color for edges
                g.drawLine(node1Pos.x, node1Pos.y, node2Pos.x, node2Pos.y); // Draw the edge between the nodes
                g.setColor(Color.BLACK); // Set the color for the text (cost)
                g.drawString(edge.cost + "", (node1Pos.x + node2Pos.x) / 2, (node1Pos.y + node2Pos.y) / 2); // Draw the
                                                                                                            // cost
                                                                                                            // label on
                                                                                                            // the edge
            }
        }
    }
}

class Edge {
    String node1, node2;
    int cost, bandwidth;

    public Edge(String node1, String node2, int cost, int bandwidth) {
        this.node1 = node1; // Initialize the first node
        this.node2 = node2; // Initialize the second node
        this.cost = cost; // Initialize the cost
        this.bandwidth = bandwidth; // Initialize the bandwidth
    }
}

class Node {
    String name;
    int cost;

    public Node(String name, int cost) {
        this.name = name; // Initialize the node's name
        this.cost = cost; // Initialize the node's cost
    }
}

class UnionFind {
    private Map<String, String> parent = new HashMap<>(); // Map to store the parent of each node

    public UnionFind(Set<String> nodes) {
        for (String node : nodes)
            parent.put(node, node); // Initialize each node's parent to be itself
    }

    public String find(String node) {
        if (!parent.get(node).equals(node)) { // If the node's parent is not itself
            parent.put(node, find(parent.get(node))); // Recursively find the root parent
        }
        return parent.get(node); // Return the root parent of the node
    }

    public boolean union(String node1, String node2) {
        String root1 = find(node1); // Find the root of the first node
        String root2 = find(node2); // Find the root of the second node
        if (!root1.equals(root2)) { // If the nodes are not connected
            parent.put(root1, root2); // Connect the two nodes by making one root the parent of the other
            return true; // Return true to indicate that the union was successful
        }
        return false; // Return false if the nodes are already connected
    }
}