package Question5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

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

public class Question5 extends JFrame { // Extending JFrame to create a GUI window

    // GUI components for visualization and user interaction
    private JPanel graphPanel;
    private JButton addNodeButton, addEdgeButton, optimizeButton, findPathButton;
    private JTextField costField, bandwidthField;
    private JLabel totalCostLabel, latencyLabel;

    // Data structures to store network information
    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private Graph graph;

    // Constructor: Set up the GUI components and layout
    public Question5() {
        setTitle("Network Optimizer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize the custom panel for drawing the network graph
        graphPanel = new GraphPanel();
        add(graphPanel, BorderLayout.CENTER);

        // Create a control panel to hold buttons and input fields
        JPanel controlPanel = new JPanel();
        addNodeButton = new JButton("Add Node");
        addEdgeButton = new JButton("Add Edge");
        optimizeButton = new JButton("Optimize Network");
        findPathButton = new JButton("Find Shortest Path");
        costField = new JTextField(5);
        bandwidthField = new JTextField(5);
        totalCostLabel = new JLabel("Total Cost: 0");
        latencyLabel = new JLabel("Latency: N/A");

        // Add components to the control panel in order
        controlPanel.add(addNodeButton);
        controlPanel.add(addEdgeButton);
        controlPanel.add(new JLabel("Cost:"));
        controlPanel.add(costField);
        controlPanel.add(new JLabel("Bandwidth:"));
        controlPanel.add(bandwidthField);
        controlPanel.add(optimizeButton);
        controlPanel.add(findPathButton);
        controlPanel.add(totalCostLabel);
        controlPanel.add(latencyLabel);
        add(controlPanel, BorderLayout.SOUTH);

        // Attach action listeners to the buttons to handle user interactions
        addNodeButton.addActionListener(e -> addNode());
        addEdgeButton.addActionListener(e -> addEdge());
        optimizeButton.addActionListener(e -> optimizeNetwork());
        findPathButton.addActionListener(e -> findShortestPath());

        setVisible(true); // Make the window visible to the user
    }

    // Inner class representing a network node (server or client)
    static class Node {
        int id;
        int x, y;

        Node(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

    // Inner class representing an edge (connection) between two nodes
    static class Edge {
        Node src, dest;
        int cost;
        int bandwidth;

        Edge(Node src, Node dest, int cost, int bandwidth) {
            this.src = src;
            this.dest = dest;
            this.cost = cost;
            this.bandwidth = bandwidth;
        }
    }

    // Custom JPanel for drawing the network graph
    class GraphPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Clear previous drawings and paint the background

            // Draw each edge in the network
            for (Edge edge : edges) {
                g.setColor(Color.BLACK); // Set color for edges
                g.drawLine(edge.src.x, edge.src.y, edge.dest.x, edge.dest.y); // Draw a line between source and
                                                                              // destination nodes

                // Calculate the midpoint of the edge to place the label
                int midX = (edge.src.x + edge.dest.x) / 2;
                int midY = (edge.src.y + edge.dest.y) / 2;
                // Draw label showing cost and bandwidth on the edge
                g.drawString("C:" + edge.cost + " B:" + edge.bandwidth, midX, midY);
            }

            // Draw each node in the network
            for (Node node : nodes) {
                g.setColor(Color.BLUE); // Set color for nodes
                // Draw the node as a filled circle (centered at node.x, node.y)
                g.fillOval(node.x - 10, node.y - 10, 20, 20);
                g.setColor(Color.BLACK); // Set color for node label text
                // Draw the node's ID near the node
                g.drawString("Node " + node.id, node.x - 15, node.y - 15);
            }
        }
    }

    // Method to add a new node at a random position
    private void addNode() {
        int id = nodes.size(); // Set node ID based on the number of existing nodes
        int x = 50 + (int) (Math.random() * 700); // Random x-coordinate (within defined bounds)
        int y = 50 + (int) (Math.random() * 500); // Random y-coordinate (within defined bounds)
        nodes.add(new Node(id, x, y)); // Create and add the new node to the nodes list
        graphPanel.repaint(); // Refresh the graph panel to display the new node
    }

    // Method to add an edge between the last two added nodes
    private void addEdge() {
        if (nodes.size() < 2) { // Check if at least two nodes exist
            JOptionPane.showMessageDialog(this, "Need at least two nodes to add an edge.");
            return; // Exit if there are not enough nodes
        }
        String costStr = costField.getText(); // Retrieve cost input as a string
        String bandwidthStr = bandwidthField.getText(); // Retrieve bandwidth input as a string
        try {
            int cost = Integer.parseInt(costStr); // Convert cost to an integer
            int bandwidth = Integer.parseInt(bandwidthStr); // Convert bandwidth to an integer
            // Connect the last two nodes: the second-to-last is the source, and the last is
            // the destination
            Node src = nodes.get(nodes.size() - 2);
            Node dest = nodes.get(nodes.size() - 1);
            edges.add(new Edge(src, dest, cost, bandwidth)); // Create and add the new edge to the edges list
            graphPanel.repaint(); // Refresh the graph panel to display the new edge
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid cost or bandwidth."); // Display error if input is invalid
        }
    }

    // Method to optimize the network by computing a Minimum Spanning Tree (MST)
    private void optimizeNetwork() {
        if (nodes.isEmpty()) { // Check if there are any nodes in the network
            return; // Exit if the network is empty
        }
        // Create a new Graph instance with the current number of nodes
        graph = new Graph(nodes.size());
        // Add each edge from the network to the graph using cost as the weight
        for (Edge edge : edges) {
            graph.addEdge(edge.src.id, edge.dest.id, edge.cost);
        }
        // Compute the MST cost using Kruskal's algorithm (placeholder implementation)
        int totalCost = graph.kruskalMST();
        totalCostLabel.setText("Total Cost: " + totalCost); // Update the total cost label with the MST cost
        latencyLabel.setText("Latency: Optimized"); // Update the latency label to indicate network optimization
    }

    // Method to find the shortest path based on inverse bandwidth (to simulate
    // latency)
    private void findShortestPath() {
        if (nodes.size() < 2) { // Check if there are at least two nodes
            JOptionPane.showMessageDialog(this, "Need at least two nodes to find a path.");
            return; // Exit if not enough nodes exist
        }
        // Define source as the first node and destination as the last node in the list
        int srcId = nodes.get(0).id;
        int destId = nodes.get(nodes.size() - 1).id;
        // Create a new Graph instance for the path finding process
        graph = new Graph(nodes.size());
        // Add each edge to the graph using the inverse of the bandwidth as the weight
        // (this means higher bandwidth results in lower latency)
        for (Edge edge : edges) {
            graph.addEdge(edge.src.id, edge.dest.id, 1.0 / edge.bandwidth);
        }
        // Compute the shortest path latency using Dijkstra's algorithm (placeholder
        // implementation)
        double latency = graph.dijkstra(srcId, destId);
        latencyLabel.setText("Latency: " + latency); // Update the latency label with the computed value
    }

    // Graph class to support network optimization algorithms (MST and shortest
    // path)
    static class Graph {
        private int V; // Number of vertices in the graph
        private List<Edge> edges; // List of all edges in the graph

        // Constructor: Initialize the graph with a given number of vertices
        Graph(int V) {
            this.V = V;
            edges = new ArrayList<>();
        }

        // Method to add an edge to the graph with a specified weight
        void addEdge(int src, int dest, double weight) {
            edges.add(new Edge(new Node(src, 0, 0), new Node(dest, 0, 0), (int) weight, 0));
        }

        // Placeholder method for Kruskal's algorithm to compute the MST cost
        int kruskalMST() {
            // Actual implementation of Kruskal's algorithm should be provided here.
            // Currently, this method returns 0 as a placeholder.
            return 0;
        }

        double dijkstra(int src, int dest) {

            return 0.0;
        }
    }

    // Main method to launch the GUI application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Question5::new);
    }
}

/*
 * Input/Output:
 * -------------
 * Input:
 * - The user interacts with the GUI by:
 * • Clicking "Add Node" to add nodes at random positions.
 * • Entering values in "Cost" and "Bandwidth" then clicking "Add Edge" to
 * connect nodes.
 * • Clicking "Optimize Network" to compute the network's MST cost (placeholder
 * output).
 * • Clicking "Find Shortest Path" to compute the shortest path latency
 * (placeholder output).
 *
 * Output:
 * - A graphical window displays the network graph.
 * - Nodes appear as blue circles labeled with their node IDs (e.g., "Node 0").
 * - Edges are drawn as black lines with labels indicating cost and bandwidth
 * (e.g., "C:10 B:100").
 * - The "Total Cost" and "Latency" labels are updated based on the computed
 * results.
 */
