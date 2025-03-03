import java.util.*; // Import necessary Java utilities, including collections like List and Map.

/*
 * Working Mechanism:
 * This program processes a list of tweets (each represented as a map containing user_id, tweet_id, tweet, and tweet_date).
 * It counts the number of times each hashtag is mentioned across all tweets.
 * Hashtags are identified as words starting with '#' in the tweet text.
 * After counting, the program sorts the hashtags in descending order by count, and if counts are equal,
 * in ascending lexicographical order.
 * Finally, it outputs the top three hashtags in a formatted table.
 */

public class Question4A { // Define the class Qno4A
    public static void main(String[] args) { // Main method: program entry point

        // Create a list to store tweet data as maps.
        List<Map<String, String>> tweets = new ArrayList<>(); // Initialize an ArrayList to hold tweets (each as a map)

        // Add sample tweets to the list using the createTweet helper method.
        tweets.add(
                createTweet("135", "13", "Enjoying a great start to the day. #HappyDay #MorningVibes", "2024-02-01")); // Add
                                                                                                                       // first
                                                                                                                       // tweet
        tweets.add(createTweet("136", "14", "Another #HappyDay with good vibes! #FeelGood", "2024-02-03")); // Add
                                                                                                            // second
                                                                                                            // tweet
        tweets.add(createTweet("137", "15", "Productivity peaks! #WorkLife #ProductiveDay", "2024-02-04")); // Add third
                                                                                                            // tweet
        tweets.add(createTweet("138", "16", "Exploring new tech frontiers. #TechLife #Innovation", "2024-02-04")); // Add
                                                                                                                   // fourth
                                                                                                                   // tweet
        tweets.add(createTweet("139", "17", "Gratitude for today's moments. #HappyDay #Thankful", "2024-02-05")); // Add
                                                                                                                  // fifth
                                                                                                                  // tweet
        tweets.add(createTweet("140", "18", "Innovation drives us. #TechLife #FutureTech", "2024-02-07")); // Add sixth
                                                                                                           // tweet
        tweets.add(createTweet("141", "19", "Connecting with nature's serenity. #Nature #Peaceful", "2024-02-09")); // Add
                                                                                                                    // seventh
                                                                                                                    // tweet

        // Create a map to store hashtags and their counts.
        Map<String, Integer> hashtagCounts = new HashMap<>(); // Initialize a HashMap for hashtag counts

        // Iterate through each tweet in the list.
        for (Map<String, String> tweet : tweets) { // Loop over every tweet in the tweets list
            String tweetText = tweet.get("tweet"); // Retrieve the tweet text from the current tweet
            String[] words = tweetText.split(" "); // Split the tweet text into individual words by spaces

            // Iterate through each word in the tweet.
            for (String word : words) { // Loop over each word in the tweet text
                if (word.startsWith("#")) { // Check if the word starts with '#' indicating a hashtag
                    // Update the count for the hashtag in the map.
                    hashtagCounts.put(word, hashtagCounts.getOrDefault(word, 0) + 1); // Increment count for the hashtag
                }
            }
        }

        // Convert the map entries to a list for sorting.
        List<Map.Entry<String, Integer>> sortedHashtags = new ArrayList<>(hashtagCounts.entrySet()); // Create a list
                                                                                                     // from the
                                                                                                     // hashtagCounts
                                                                                                     // map entries

        // Sort the list of hashtags by count in descending order.
        // If counts are equal, sort alphabetically by hashtag name.
        sortedHashtags.sort((a, b) -> { // Sort the entries using a lambda comparator
            int countCompare = b.getValue().compareTo(a.getValue()); // Compare counts in descending order
            if (countCompare != 0)
                return countCompare; // If counts differ, return the comparison result
            return a.getKey().compareTo(b.getKey()); // If counts are equal, compare hashtag names lexicographically
        });

        // Output the top 3 hashtags in the redesigned table format.
        // Print the table header.
        System.out.println("+-------------+---------+"); // Print top border of the table
        System.out.println("|   HASHTAG   |  COUNT  |"); // Print table header row with column names
        System.out.println("+-------------+---------+"); // Print separator line

        // Iterate through the top 3 hashtags (or fewer if there are less than 3).
        for (int i = 0; i < Math.min(3, sortedHashtags.size()); i++) { // Loop for the top 3 entries or less if not
                                                                       // enough hashtags
            Map.Entry<String, Integer> entry = sortedHashtags.get(i); // Get the current hashtag entry
            // Print the hashtag and its count in a formatted table row.
            System.out.printf("| %-11s | %-7d |%n", entry.getKey(), entry.getValue()); // Print the formatted row with
                                                                                       // hashtag and count
        }

        // Print the table footer.
        System.out.println("+-------------+---------+"); // Print bottom border of the table
    }

    // Helper method to create a tweet map.
    // This method creates and returns a map representing a single tweet.
    private static Map<String, String> createTweet(String userId, String tweetId, String tweet, String tweetDate) {
        Map<String, String> tweetMap = new HashMap<>(); // Initialize a new HashMap to hold tweet data
        tweetMap.put("user_id", userId); // Add the user_id to the map
        tweetMap.put("tweet_id", tweetId); // Add the tweet_id to the map
        tweetMap.put("tweet", tweet); // Add the tweet text to the map
        tweetMap.put("tweet_date", tweetDate); // Add the tweet_date to the map
        return tweetMap; // Return the created tweet map
    }
}

// +-------------+---------+
// | HASHTAG | COUNT |
// +-------------+---------+
// | #HappyDay | 3 |
// | #TechLife | 2 |
// | #Innovation | 1 |
// +-------------+---------+
