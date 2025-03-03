package Question6;

import javax.swing.*; // Import Swing components for GUI
import java.awt.*; // Import AWT classes for graphics
import java.awt.event.*; // Import event classes for handling user input
import java.io.BufferedReader; // Import BufferedReader for reading from streams
import java.io.InputStreamReader; // Import InputStreamReader for reading bytes and decoding them into characters
import java.net.URL; // Import URL for representing and accessing a URL
import java.util.*; // Import utility classes like List, Map, etc.
import java.util.concurrent.*; // Import concurrent classes for multithreading
import java.util.concurrent.atomic.AtomicInteger; // Import AtomicInteger for thread-safe counting

/*
 * Working Mechanism:
 * This program implements a simple web crawler with a GUI using Java Swing.
 * The GUI allows the user to enter a seed URL, then starts crawling web pages.
 * The crawler uses a thread pool to fetch web pages concurrently.
 * It maintains a thread-safe queue for URLs to crawl, a set for visited URLs,
 * and a map to store a snippet of crawled data for each URL.
 * Crawling stops after a maximum number of URLs have been processed.
 * The GUI displays real-time logs of the crawl progress and shows the data fetched.
 */

public class Question6B extends JFrame { // Define class WebCrawlerGUI extending JFrame (the main window)
    
    // GUI components declaration
    private JTextField urlField;                    // Text field for seed URL input
    private JButton startButton;                    // Button to start crawling
    private JTextArea logArea;                      // Area to display crawl progress logs
    private JTextArea dataArea;                     // Area to display crawled data snippets
    private JLabel statusLabel;                     // Label to show current crawler status

    // Data structures for crawling (all are thread-safe)
    private Queue<String> urlQueue = new LinkedBlockingQueue<>(); // Thread-safe queue for URLs to crawl
    private Set<String> visitedUrls = Collections.synchronizedSet(new HashSet<>()); // Thread-safe set to store visited URLs
    private Map<String, String> crawledData = Collections.synchronizedMap(new HashMap<>()); // Thread-safe map to store crawled data
    private ExecutorService executorService;        // Thread pool for executing crawling tasks
    private AtomicInteger activeTasks = new AtomicInteger(0); // Atomic counter for active crawling tasks
    private final int MAX_URLS = 10;                // Maximum number of URLs to crawl
    private final int THREAD_POOL_SIZE = 4;         // Number of threads in the thread pool
    private volatile boolean isCrawling = false;    // Flag to track if crawling is currently in progress

    // Constructor to set up the GUI and initialize components
    public WebCrawlerGUI() {
        setTitle("Web Crawler GUI");                // Set the window title
        setSize(800, 600);                          // Set the window size (width x height)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure application exits when window is closed
        setLayout(new BorderLayout());              // Use BorderLayout for arranging components

        // Create a control panel for URL input and the start button
        JPanel controlPanel = new JPanel();         // Initialize a new JPanel for controls
        urlField = new JTextField("https://example.com", 20); // Create a text field with a default seed URL and a column width of 20
        startButton = new JButton("Start Crawling");  // Create a button to start the crawling process
        statusLabel = new JLabel("Status: Idle");     // Create a label to show the current status ("Idle" initially)
        controlPanel.add(new JLabel("Seed URL:"));    // Add a label to the control panel indicating the URL field
        controlPanel.add(urlField);                   // Add the URL input field to the control panel
        controlPanel.add(startButton);                // Add the start button to the control panel
        controlPanel.add(statusLabel);                // Add the status label to the control panel
        add(controlPanel, BorderLayout.NORTH);        // Add the control panel to the top (NORTH) of the main window

        // Create a split pane to show both the log area and the data area side by side
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); // Create a horizontal split pane
        logArea = new JTextArea(20, 30);              // Initialize the log area with 20 rows and 30 columns
        logArea.setEditable(false);                   // Make the log area read-only
        dataArea = new JTextArea(20, 30);             // Initialize the data area with 20 rows and 30 columns
        dataArea.setEditable(false);                  // Make the data area read-only
        splitPane.setLeftComponent(new JScrollPane(logArea));  // Add the log area inside a scroll pane to the left side
        splitPane.setRightComponent(new JScrollPane(dataArea)); // Add the data area inside a scroll pane to the right side
        add(splitPane, BorderLayout.CENTER);          // Add the split pane to the center of the main window

        // Add an action listener to the start button to trigger crawling when clicked
        startButton.addActionListener(e -> startCrawling()); // When button is clicked, call startCrawling()

        setVisible(true);                             // Make the window visible
        // Initialize the executor service with a fixed thread pool
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    // Method to start the crawling process when the user clicks the start button
    private void startCrawling() {
        if (isCrawling) {                             // Check if crawling is already in progress
            JOptionPane.showMessageDialog(this, "Crawling is already in progress."); // Inform the user if already crawling
            return;                                   // Exit the method if already crawling
        }
        String seedUrl = urlField.getText().trim();   // Get the seed URL from the text field, trimming extra spaces
        if (seedUrl.isEmpty()) {                      // Validate the seed URL input
            JOptionPane.showMessageDialog(this, "Please enter a valid URL."); // Show error message if URL is empty
            return;                                   // Exit the method if URL is invalid
        }
        urlQueue.clear();                             // Clear any previous URLs from the queue
        visitedUrls.clear();                          // Clear the set of visited URLs
        crawledData.clear();                          // Clear the map of crawled data
        logArea.setText("");                          // Clear the log area in the GUI
        dataArea.setText("");                         // Clear the data display area in the GUI
        urlQueue.add(seedUrl);                        // Add the seed URL to the URL queue
        isCrawling = true;                            // Set the crawling flag to true
        statusLabel.setText("Status: Crawling");        // Update the status label to "Crawling"
        startButton.setEnabled(false);                // Disable the start button during crawling
        crawl();                                      // Begin the crawling process
    }

    // Main crawling logic: this method processes URLs from the queue and submits crawl tasks
    private void crawl() {
        // Continue processing as long as there are URLs and we haven't exceeded the maximum count
        while (!urlQueue.isEmpty() && visitedUrls.size() < MAX_URLS && !executorService.isShutdown()) {
            String url = urlQueue.poll();           // Retrieve and remove the next URL from the queue
            if (url != null && !visitedUrls.contains(url)) { // Check if the URL is not null and hasn't been visited
                visitedUrls.add(url);                 // Mark the URL as visited
                activeTasks.incrementAndGet();        // Increment the count of active tasks
                executorService.submit(new CrawlTask(url)); // Submit a new crawl task for the URL to the thread pool
            }
        }
        // Start a new thread to monitor active tasks and shutdown the executor when done
        new Thread(() -> {
            while (activeTasks.get() > 0) {           // Wait until all tasks have completed
                try {
                    Thread.sleep(1000);               // Sleep for a second to avoid busy-waiting
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupt status if interrupted
                }
            }
            shutdown();                             // Once all tasks are complete, shut down the executor
        }).start();                                 // Start the monitoring thread
    }

    // Method to shutdown the executor service and update the GUI when crawling is complete
    private void shutdown() {
        executorService.shutdown();                 // Stop accepting new tasks
        try {
            // Wait up to 60 seconds for active tasks to complete
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();      // Force shutdown if tasks do not finish in time
                logArea.append("Forced shutdown due to timeout.\n"); // Log forced shutdown
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();          // Force shutdown if interrupted
            Thread.currentThread().interrupt();     // Restore the interrupt status
        }
        isCrawling = false;                         // Reset the crawling flag to false
        statusLabel.setText("Status: Idle");        // Update the status label to "Idle"
        startButton.setEnabled(true);               // Re-enable the start button
        // Log completion with total URLs crawled
        logArea.append("Crawling completed. Total URLs crawled: " + visitedUrls.size() + "\n");
    }

    // Inner class representing a crawl task that implements Runnable
    class CrawlTask implements Runnable {
        private String url;                         // The URL to crawl

        // Constructor that initializes the task with the given URL
        CrawlTask(String url) {
            this.url = url;                         // Set the URL to be crawled
        }

        // The run method defines the task to be executed in a separate thread
        @Override
        public void run() {
            try {
                String content = fetchPage(url);    // Fetch the web page content from the URL
                processContent(url, content);         // Process the fetched content and store a snippet
                extractUrls(content);                 // Extract new URLs from the content and add them to the queue
            } catch (Exception e) {
                // Log any errors encountered during crawling on the Event Dispatch Thread
                SwingUtilities.invokeLater(() ->
                    logArea.append("Error crawling " + url + ": " + e.getMessage() + "\n"));
            } finally {
                activeTasks.decrementAndGet();      // Decrement the active task counter regardless of success/failure
            }
        }

        // Method to fetch the content of a web page given its URL
        private String fetchPage(String urlString) throws Exception {
            URL url = new URL(urlString);           // Create a URL object from the string
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(url.openStream())); // Open a stream and wrap it in a BufferedReader
            StringBuilder content = new StringBuilder(); // StringBuilder to accumulate page content
            String line;
            while ((line = reader.readLine()) != null) { // Read each line of the page content
                content.append(line).append("\n");  // Append the line and a newline to the content
            }
            reader.close();                         // Close the reader to free resources
            return content.toString();              // Return the complete page content as a string
        }

        // Method to process the fetched content and store a snippet in the crawledData map
        private void processContent(String url, String content) {
            // Get the first 100 characters of the content or the full content if shorter
            String snippet = content.substring(0, Math.min(content.length(), 100));
            crawledData.put(url, snippet);          // Store the snippet in the crawledData map with the URL as key
            // Update the GUI with the crawl information on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                logArea.append("Crawled: " + url + " (Length: " + content.length() + ")\n"); // Append crawl log
                dataArea.append("URL: " + url + "\nContent: " + snippet + "\n\n"); // Append data snippet
            });
        }

        // Simplified method to extract URLs from the fetched content
        private void extractUrls(String content) {
            // Split the content by whitespace into words
            String[] words = content.split("\\s+");
            // Iterate through each word to find potential URLs
            for (String word : words) {
                if (word.startsWith("http://") || word.startsWith("https://")) { // Identify valid URL strings
                    // If maximum URL limit not reached and the URL hasn't been visited yet, add it to the queue
                    if (visitedUrls.size() < MAX_URLS && !visitedUrls.contains(word)) {
                        urlQueue.add(word);         // Add the URL to the queue for later crawling
                    }
                }
            }
        }
    }

    // Main method to launch the GUI on the Event Dispatch Thread
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WebCrawlerGUI()); // Create and show the WebCrawlerGUI instance
    }
}

/*
Input/Output:

Input:
- User provides a seed URL in the text field (default is "https://example.com").
- User clicks the "Start Crawling" button.
- The crawler begins fetching pages, processing content, and extracting new URLs.
- The maximum number of URLs to crawl is set to 10.

Output:
- The GUI window titled "Web Crawler GUI" opens with a white background.
- The top control panel displays the seed URL input, the "Start Crawling" button, and a status label.
- As crawling proceeds, the left text area ("logArea") displays messages such as:
    "Crawled: [URL] (Length: [number])"
    "Error crawling [URL]: [error message]" (if an error occurs)
- The right text area ("dataArea") displays the URL and a snippet (first 100 characters) of its content.
- Once crawling is complete, a message is appended to the log area:
    "Crawling completed. Total URLs crawled: [number]"
- The status label is updated to "Status: Idle" and the start button is re-enabled.
*/
