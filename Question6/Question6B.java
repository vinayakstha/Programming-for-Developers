/*
 * Working Mechanism:
 * 1. User enters a seed URL and clicks the "Start Crawling" button.
 * 2. The application uses a thread pool to concurrently crawl web pages.
 * 3. For each page, it fetches the content, logs basic info, and extracts new URLs.
 * 4. Crawled data (first 100 characters) and progress logs are displayed in separate tabs.
 * 5. The process stops when a maximum number of URLs is reached or no new URLs are found.
 */

package Question6;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Question6B extends JFrame {
    // GUI components
    private JTextField urlField; // Input field for seed URL
    private JButton startButton; // Button to start crawling
    private JTextArea logArea; // Text area to display crawl progress
    private JTextArea dataArea; // Text area to display crawled data
    private JLabel statusLabel; // Label to show crawler status

    // Data structures for crawling
    private Queue<String> urlQueue = new LinkedBlockingQueue<>(); // Thread-safe URL queue
    private Set<String> visitedUrls = Collections.synchronizedSet(new HashSet<>()); // Thread-safe visited URLs
    private Map<String, String> crawledData = Collections.synchronizedMap(new HashMap<>()); // Thread-safe crawled data
                                                                                            // map
    private ExecutorService executorService; // Thread pool for crawling tasks
    private AtomicInteger activeTasks = new AtomicInteger(0); // Counter for active tasks
    private final int MAX_URLS = 10; // Maximum number of URLs to crawl
    private final int THREAD_POOL_SIZE = 4; // Thread pool size
    private volatile boolean isCrawling = false; // Flag to track crawling state

    // Constructor to set up the GUI with a new UI design
    public Question6B() {
        setTitle("Web Crawler GUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // Add some gap between components

        // Top panel with input controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        urlField = new JTextField("https://example.com", 25);
        startButton = new JButton("Start Crawling");
        statusLabel = new JLabel("Status: Idle");
        topPanel.add(new JLabel("Seed URL:"));
        topPanel.add(urlField);
        topPanel.add(startButton);
        topPanel.add(statusLabel);
        add(topPanel, BorderLayout.NORTH);

        // Tabbed pane for displaying logs and data separately
        JTabbedPane tabbedPane = new JTabbedPane();
        logArea = new JTextArea(20, 40);
        logArea.setEditable(false);
        dataArea = new JTextArea(20, 40);
        dataArea.setEditable(false);
        tabbedPane.addTab("Crawl Log", new JScrollPane(logArea));
        tabbedPane.addTab("Crawled Data", new JScrollPane(dataArea));
        add(tabbedPane, BorderLayout.CENTER);

        // Action listener for start button
        startButton.addActionListener(e -> startCrawling());

        // Set a modern look and feel if available
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            // Use default look and feel if Nimbus is not available
        }

        setVisible(true);
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    // Method to start crawling based on user input
    private void startCrawling() {
        if (isCrawling) {
            JOptionPane.showMessageDialog(this, "Crawling is already in progress.");
            return;
        }
        String seedUrl = urlField.getText().trim();
        if (seedUrl.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid URL.");
            return;
        }
        urlQueue.clear();
        visitedUrls.clear();
        crawledData.clear();
        logArea.setText("");
        dataArea.setText("");
        urlQueue.add(seedUrl);
        isCrawling = true;
        statusLabel.setText("Status: Crawling");
        startButton.setEnabled(false);
        crawl();
    }

    // Main crawling logic
    private void crawl() {
        while (!urlQueue.isEmpty() && visitedUrls.size() < MAX_URLS && !executorService.isShutdown()) {
            String url = urlQueue.poll();
            if (url != null && !visitedUrls.contains(url)) {
                visitedUrls.add(url);
                activeTasks.incrementAndGet();
                executorService.submit(new CrawlTask(url));
            }
        }
        // Monitor tasks and shutdown when done
        new Thread(() -> {
            while (activeTasks.get() > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            shutdown();
        }).start();
    }

    // Method to shut down the thread pool
    private void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                logArea.append("Forced shutdown due to timeout.\n");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        isCrawling = false;
        statusLabel.setText("Status: Idle");
        startButton.setEnabled(true);
        logArea.append("Crawling completed. Total URLs crawled: " + visitedUrls.size() + "\n");
    }

    // Inner class for crawl task
    class CrawlTask implements Runnable {
        private String url; // URL to crawl

        CrawlTask(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                String content = fetchPage(url);
                processContent(url, content);
                extractUrls(content);
            } catch (Exception e) {
                SwingUtilities
                        .invokeLater(() -> logArea.append("Error crawling " + url + ": " + e.getMessage() + "\n"));
            } finally {
                activeTasks.decrementAndGet();
            }
        }

        // Method to fetch web page content
        private String fetchPage(String urlString) throws Exception {
            URL url = new URL(urlString);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            return content.toString();
        }

        // Method to process and store content
        private void processContent(String url, String content) {
            String snippet = content.substring(0, Math.min(content.length(), 100));
            crawledData.put(url, snippet);
            SwingUtilities.invokeLater(() -> {
                logArea.append("Crawled: " + url + " (Length: " + content.length() + ")\n");
                dataArea.append("URL: " + url + "\nContent: " + snippet + "\n\n");
            });
        }

        // Method to extract new URLs (simplified)
        private void extractUrls(String content) {
            String[] words = content.split("\\s+");
            for (String word : words) {
                if (word.startsWith("http://") || word.startsWith("https://")) {
                    if (visitedUrls.size() < MAX_URLS && !visitedUrls.contains(word)) {
                        urlQueue.add(word);
                    }
                }
            }
        }
    }

    // Main method to launch the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Question6B());
    }
}
