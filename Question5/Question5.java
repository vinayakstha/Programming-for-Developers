package Question5;

import javax.swing.*; // Import Swing components for GUI creation
import java.awt.*; // Import AWT classes for graphics rendering
import java.awt.event.*; // Import AWT event classes for handling user actions
import java.util.*; // Import utility classes for data structures (e.g., List, Map, Queue)
import java.util.List; // Import the List interface

/*
 * Working Mechanism:
 * This program (Question4B) is a GUI-based network optimizer built using Java Swing.
 * It allows users to add nodes and edges to form a network graph.
 * Nodes represent servers/clients and edges represent network connections with a specified cost and bandwidth.
 * The network graph is displayed in a custom drawing panel (GraphPanel).
 * Users can optimize the network using a Minimum Spanning Tree algorithm (placeholder implementation)
 * and find the shortest path between nodes based on inverse bandwidth (again, a placeholder implementation).
 * The results (total cost and latency) are shown on the GUI via labels.
 */

public class Question5 extends JFrame { // Class declaration extending JFrame for GUI support

    // GUI components for visualization and interaction
    private JPanel graphPanel; // Panel where the network graph is drawn
    private JButton addNodeButton, addEdgeButton, // Buttons for adding nodes and edges
            optimizeButton, findPathButton; // Buttons for optimizing the network and finding paths
    private JTextField costField, bandwidthField; // Text fields to input edge cost and bandwidth
    private JLabel totalCostLabel, latencyLabel; // Labels to display total cost and latency results

    // Data structures to store network information
    private List<Node> nodes = new ArrayList<>(); // List to hold network nodes
    private List<Edge> edges = new ArrayList<>(); // List to hold network edges
    private Graph graph; // Graph object used for optimization algorithms

    // Constructor: sets up the GUI components and layout
    public Question5() {
        setTitle("Network Optimizer"); // Set the window title
        setSize(800, 600); // Set the window size to 800x600 pixels
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit the application when the window is closed
        setLayout(new BorderLayout()); // Use BorderLayout to arrange components

        graphPanel = new GraphPanel(); // Create the custom graph drawing panel
        add(graphPanel, BorderLayout.CENTER); // Add the graph panel to the center of the window

        // Create a control panel for user inputs and actions
        JPanel controlPanel = new JPanel(); // Initialize a new JPanel for controls
        addNodeButton = new JButton("Add Node"); // Button to add a new node
        addEdgeButton = new JButton("Add Edge"); // Button to add a new edge
        optimizeButton = new JButton("Optimize Network"); // Button to optimize the network (compute MST)
        findPathButton = new JButton("Find Shortest Path"); // Button to find the shortest path in the network
        costField = new JTextField(5); // Text field for entering edge cost (5 characters wide)
        bandwidthField = new JTextField(5); // Text field for entering edge bandwidth (5 characters wide)
        totalCostLabel = new JLabel("Total Cost: 0"); // Label to display the total cost (initially 0)
        latencyLabel = new JLabel("Latency: N/A"); // Label to display latency (initially N/A)

        // Add components to the control panel in order
        controlPanel.add(addNodeButton); // Add "Add Node" button
        controlPanel.add(addEdgeButton); // Add "Add Edge" button
        controlPanel.add(new JLabel("Cost:")); // Add label for cost input
        controlPanel.add(costField); // Add cost input field
        controlPanel.add(new JLabel("Bandwidth:")); // Add label for bandwidth input
        controlPanel.add(bandwidthField); // Add bandwidth input field
        controlPanel.add(optimizeButton); // Add "Optimize Network" button
        controlPanel.add(findPathButton); // Add "Find Shortest Path" button
        controlPanel.add(totalCostLabel); // Add label to display total cost
        controlPanel.add(latencyLabel); // Add label to display latency
        add(controlPanel, BorderLayout.SOUTH); // Add the control panel to the bottom of the window

        // Attach action listeners to buttons to handle user interactions
        addNodeButton.addActionListener(e -> addNode()); // Call addNode() when "Add Node" button is clicked
        addEdgeButton.addActionListener(e -> addEdge()); // Call addEdge() when "Add Edge" button is clicked
        optimizeButton.addActionListener(e -> optimizeNetwork()); // Call optimizeNetwork() when button is clicked
        findPathButton.addActionListener(e -> findShortestPath()); // Call findShortestPath() when button is clicked

        setVisible(true); // Make the window visible to the user
    }

    // Inner class representing a network node (server or client)
    static class Node {
        int id; // Unique identifier for the node
        int x, y; // X and Y coordinates for drawing the node

        Node(int id, int x, int y) {
            this.id = id; // Set node ID
            this.x = x; // Set x-coordinate
            this.y = y; // Set y-coordinate
        }
    }

    // Inner class representing an edge (network connection) between two nodes
    static class Edge {
        Node src, dest; // Source and destination nodes for the edge
        int cost; // Cost associated with the edge
        int bandwidth; // Bandwidth of the connection

        Edge(Node src, Node dest, int cost, int bandwidth) {
            this.src = src; // Set source node
            this.dest = dest; // Set destination node
            this.cost = cost; // Set cost value
            this.bandwidth = bandwidth; // Set bandwidth value
        }
    }

    // Custom JPanel to draw the network graph
    class GraphPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Clear the panel and paint the background
            // Draw all edges from the edges list
            for (Edge edge : edges) {
                g.setColor(Color.BLACK); // Set color for drawing edges
                // Draw a line representing the edge between source and destination nodes
                g.drawLine(edge.src.x, edge.src.y, edge.dest.x, edge.dest.y);
                // Calculate the midpoint of the edge for displaying its label
                int midX = (edge.src.x + edge.dest.x) / 2; // Midpoint X coordinate
                int midY = (edge.src.y + edge.dest.y) / 2; // Midpoint Y coordinate
                // Draw the edge label showing cost and bandwidth at the midpoint
                g.drawString("C:" + edge.cost + " B:" + edge.bandwidth, midX, midY);
            }
            // Draw all nodes from the nodes list
            for (Node node : nodes) {
                g.setColor(Color.BLUE); // Set color for drawing nodes
                // Draw node as a filled circle (oval) centered at (node.x, node.y)
                g.fillOval(node.x - 10, node.y - 10, 20, 20);
                g.setColor(Color.BLACK); // Set color for node label text
                // Draw the node label with its ID near the node
                g.drawString("Node " + node.id, node.x - 15, node.y - 15);
            }
        }
    }

    // Method to add a new node to the network
    private void addNode() {
        int id = nodes.size(); // Determine node ID based on current number of nodes
        int x = 50 + (int) (Math.random() * 700); // Generate a random x-coordinate within bounds
        int y = 50 + (int) (Math.random() * 500); // Generate a random y-coordinate within bounds
        nodes.add(new Node(id, x, y)); // Create and add the new node to the nodes list
        graphPanel.repaint(); // Repaint the graph panel to reflect the new node
    }

    // Method to add a new edge between the last two added nodes
    private void addEdge() {
        if (nodes.size() < 2) { // Ensure there are at least two nodes to create an edge
            JOptionPane.showMessageDialog(this, "Need at least two nodes to add an edge."); // Show error message
            return; // Exit the method if insufficient nodes exist
        }
        String costStr = costField.getText(); // Get the cost input from the text field
        String bandwidthStr = bandwidthField.getText(); // Get the bandwidth input from the text field
        try {
            int cost = Integer.parseInt(costStr); // Parse the cost input to an integer
            int bandwidth = Integer.parseInt(bandwidthStr); // Parse the bandwidth input to an integer
            // For simplicity, connect the last two nodes added to the network
            Node src = nodes.get(nodes.size() - 2); // Second-to-last node is the source
            Node dest = nodes.get(nodes.size() - 1); // Last node is the destination
            edges.add(new Edge(src, dest, cost, bandwidth)); // Create and add the new edge to the edges list
            graphPanel.repaint(); // Repaint the graph panel to display the new edge
        } catch (NumberFormatException e) { // Catch exceptions if input parsing fails
            JOptionPane.showMessageDialog(this, "Invalid cost or bandwidth."); // Show error message if input is invalid
        }
    }

    // Method to optimize the network by computing a Minimum Spanning Tree (MST) and
    // updating the total cost label
    private void optimizeNetwork() {
        if (nodes.isEmpty())
            return; // Exit if there are no nodes in the network
        graph = new Graph(nodes.size()); // Create a new Graph instance with the number of nodes
        for (Edge edge : edges) { // For each edge in the network,
            graph.addEdge(edge.src.id, edge.dest.id, edge.cost); // Add the edge to the graph with cost as the weight
        }
        int totalCost = graph.kruskalMST(); // Compute the MST cost using Kruskal's algorithm (placeholder
                                            // implementation)
        totalCostLabel.setText("Total Cost: " + totalCost); // Update the total cost label with the computed cost
        latencyLabel.setText("Latency: Optimized"); // Update the latency label to indicate optimization
    }

    // Method to find the shortest path (minimize latency) based on inverse
    // bandwidth
    private void findShortestPath() {
        if (nodes.size() < 2) { // Ensure there are at least two nodes to compute a path
            JOptionPane.showMessageDialog(this, "Need at least two nodes to find a path."); // Show error message
            return; // Exit the method if insufficient nodes exist
        }
        int srcId = nodes.get(0).id; // Set the source node (first node in the list)
        int destId = nodes.get(nodes.size() - 1).id; // Set the destination node (last node in the list)
        graph = new Graph(nodes.size()); // Create a new Graph instance with the number of nodes
        for (Edge edge : edges) { // For each edge in the network,
            // Use the inverse of the bandwidth as the weight (higher bandwidth results in
            // lower latency)
            graph.addEdge(edge.src.id, edge.dest.id, 1.0 / edge.bandwidth);
        }
        double latency = graph.dijkstra(srcId, destId); // Compute the shortest path latency using Dijkstra's algorithm
                                                        // (placeholder)
        latencyLabel.setText("Latency: " + latency); // Update the latency label with the computed latency
    }

    // Graph class to support optimization algorithms (MST and shortest path)
    static class Graph {
        private int V; // Number of vertices in the graph
        private List<Edge> edges; // List to store all edges in the graph

        Graph(int V) {
            this.V = V; // Set the number of vertices
            edges = new ArrayList<>(); // Initialize the edge list
        }

        // Method to add an edge to the graph with a given weight
        void addEdge(int src, int dest, double weight) {
            // Create and add a new edge to the graph; dummy nodes are used as only the node
            // IDs are needed
            edges.add(new Edge(new Node(src, 0, 0), new Node(dest, 0, 0), (int) weight, 0));
        }

        // Placeholder method for Kruskal's algorithm to compute the Minimum Spanning
        // Tree (MST) cost
        int kruskalMST() {
            // Actual implementation of Kruskal's algorithm should be here.
            // This placeholder simply returns 0.
            return 0;
        }

        // Placeholder method for Dijkstra's algorithm to compute the shortest path
        // latency between two nodes
        double dijkstra(int src, int dest) {
            // Actual implementation of Dijkstra's algorithm should be here.
            // This placeholder simply returns 0.0.
            return 0.0;
        }
    }

    // Main method to launch the GUI application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Question5::new); // Schedule the creation of the GUI on the Event Dispatch Thread
    }
}

/*
 * Input/Output:
 * Input:
 * - The user interacts with the GUI by:
 * • Clicking "Add Node" to add nodes at random positions.
 * • Clicking "Add Edge" after entering values in the "Cost" and "Bandwidth"
 * text fields to create an edge between the last two added nodes.
 * • Clicking "Optimize Network" to compute the network's minimum spanning tree
 * (MST) cost (placeholder output).
 * • Clicking "Find Shortest Path" to compute the shortest path latency between
 * the first and last node (placeholder output).
 * 
 * Output:
 * - A graphical window is displayed showing the network graph.
 * - Nodes appear as blue circles with labels (e.g., "Node 0").
 * - Edges are drawn as black lines with labels indicating their cost and
 * bandwidth (e.g., "C:10 B:100").
 * - The "Total Cost" label and "Latency" label are updated based on the
 * optimization and path-finding results.
 */
