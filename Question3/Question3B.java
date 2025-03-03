package Question3;
/*
 * Working Mechanism:
 * This program implements a simple Tetris-like game using Java Swing.
 * It creates a game board with falling Tetris blocks that the user can control using the keyboard.
 * The game updates periodically via a Timer and handles user input (left, right, down, up) to move or rotate the current block.
 * When a block can no longer move down, it is placed on the board.
 * Completed rows are cleared, and a new block is generated.
 * The game ends when a new block cannot move down, displaying a "Game Over" message.
 */

import javax.swing.*; // Import Swing components for GUI
import java.awt.*; // Import AWT classes for graphics
import java.awt.event.*; // Import event classes for handling user input
import java.util.LinkedList; // Import LinkedList for queue implementation
import java.util.Queue; // Import Queue interface
import java.util.Random; // Import Random for random number generation

public class Question3B extends JPanel implements ActionListener { // Define class Question3B extending JPanel and
                                                                   // implementing ActionListener

    private static final int BOARD_WIDTH = 10; // Width of the game board in blocks
    private static final int BOARD_HEIGHT = 20; // Height of the game board in blocks
    private static final int BLOCK_SIZE = 30; // Size of each block in pixels
    // Using brighter colors for the blocks:
    private static final Color[] COLORS = { Color.YELLOW, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK };

    private Timer timer; // Timer to control game update speed
    private boolean isGameOver; // Flag to indicate if the game is over
    private int[][] board; // 2D array representing the game board grid
    private Block currentBlock; // The current falling block
    private Queue<Block> blockQueue; // Queue to hold upcoming blocks

    // Constructor to initialize the game
    public Question3B() {
        setPreferredSize(new Dimension(BOARD_WIDTH * BLOCK_SIZE, BOARD_HEIGHT * BLOCK_SIZE)); // Set panel size based on
                                                                                              // board dimensions and
                                                                                              // block size
        setBackground(Color.WHITE); // Set background color of the game panel to white
        setFocusable(true); // Allow panel to receive keyboard focus and key events
        addKeyListener(new KeyAdapter() { // Add key listener to handle key events
            @Override
            public void keyPressed(KeyEvent e) { // Override method to handle key press events
                if (!isGameOver) { // Only process key events if the game is not over
                    switch (e.getKeyCode()) { // Switch on the key code pressed
                        case KeyEvent.VK_LEFT: // If left arrow is pressed
                            moveBlockLeft(); // Move the current block left
                            break;
                        case KeyEvent.VK_RIGHT: // If right arrow is pressed
                            moveBlockRight(); // Move the current block right
                            break;
                        case KeyEvent.VK_DOWN: // If down arrow is pressed
                            moveBlockDown(); // Move the current block down faster
                            break;
                        case KeyEvent.VK_UP: // If up arrow is pressed
                            rotateBlock(); // Rotate the current block
                            break;
                    }
                    repaint(); // Repaint the panel to update the display
                }
            }
        });
        board = new int[BOARD_HEIGHT][BOARD_WIDTH]; // Initialize the game board as a 2D array
        blockQueue = new LinkedList<>(); // Initialize the queue for upcoming blocks
        timer = new Timer(500, this); // Create a timer that fires every 500 milliseconds, calling actionPerformed
        timer.start(); // Start the timer to begin the game loop
        generateNewBlock(); // Generate the first block for the game
    }

    // Method to generate a new block and set it as the current block
    private void generateNewBlock() {
        Random random = new Random(); // Create a new Random object for generating random numbers
        int shapeIndex = random.nextInt(COLORS.length); // Randomly select an index for the color from the COLORS array
        int[][] shape = getRandomShape(); // Get a random shape for the block
        Block block = new Block(shape, COLORS[shapeIndex]); // Create a new Block with the selected shape and color
        blockQueue.add(block); // Add the new block to the block queue
        currentBlock = blockQueue.poll(); // Set the current block to the first block in the queue
    }

    // Method to return a random shape for a block
    private int[][] getRandomShape() {
        int[][][] shapes = { // Define an array of possible block shapes
                { { 1, 1, 1, 1 } }, // I-shape
                { { 1, 1, 0 }, { 0, 1, 1 } }, // Z-shape
                { { 0, 1, 1 }, { 1, 1, 0 } }, // S-shape
                { { 1, 1, 1 }, { 0, 1, 0 } }, // T-shape
                { { 1, 1 }, { 1, 1 } } // O-shape
        };
        Random random = new Random(); // Create a new Random object
        return shapes[random.nextInt(shapes.length)]; // Return a random shape from the shapes array
    }

    // Method called by the timer to update game state (game loop)
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) { // Only update the game if it is not over
            if (canMoveDown(currentBlock)) { // Check if the current block can move down
                currentBlock.row++; // Move the current block one row down
            } else { // If the block cannot move down
                placeBlock(currentBlock); // Place the block onto the board
                checkCompletedRows(); // Check and clear any completed rows on the board
                generateNewBlock(); // Generate a new block to fall
                if (!canMoveDown(currentBlock)) { // If the new block cannot move down
                    isGameOver = true; // Set game over flag to true
                }
            }
            repaint(); // Repaint the game panel to reflect changes
        }
    }

    // Method to check if the current block can move down without collision
    private boolean canMoveDown(Block block) {
        for (int row = 0; row < block.shape.length; row++) { // Loop through each row of the block's shape
            for (int col = 0; col < block.shape[row].length; col++) { // Loop through each column of the block's shape
                if (block.shape[row][col] != 0) { // If the current cell in the block is occupied
                    int newRow = block.row + row + 1; // Calculate the new row position after moving down
                    int newCol = block.col + col; // Calculate the corresponding column position
                    // If newRow is out of board bounds or the board cell is already occupied
                    if (newRow >= BOARD_HEIGHT || (newRow >= 0 && board[newRow][newCol] != 0)) {
                        return false; // The block cannot move down
                    }
                }
            }
        }
        return true; // The block can move down
    }

    // Method to place the current block onto the board when it can no longer move
    // down
    private void placeBlock(Block block) {
        for (int row = 0; row < block.shape.length; row++) { // Loop through each row of the block's shape
            for (int col = 0; col < block.shape[row].length; col++) { // Loop through each column of the block's shape
                if (block.shape[row][col] != 0) { // If the cell in the block is occupied
                    // Place the block on the board using its color index (offset by 1)
                    board[block.row + row][block.col + col] = block.colorIndex + 1;
                }
            }
        }
    }

    // Method to check for completed rows and clear them from the board
    private void checkCompletedRows() {
        for (int row = 0; row < BOARD_HEIGHT; row++) { // Loop through each row of the board
            boolean isComplete = true; // Assume the current row is complete
            for (int col = 0; col < BOARD_WIDTH; col++) { // Loop through each column in the current row
                if (board[row][col] == 0) { // If any cell in the row is empty
                    isComplete = false; // The row is not complete
                    break; // Break out of the loop
                }
            }
            if (isComplete) { // If the row is complete
                for (int r = row; r > 0; r--) { // Shift all rows above down by one row
                    board[r] = board[r - 1];
                }
                board[0] = new int[BOARD_WIDTH]; // Clear the top row after shifting
            }
        }
    }

    // Method to move the current block left if possible
    private void moveBlockLeft() {
        if (canMoveLeft(currentBlock)) { // Check if moving left is allowed
            currentBlock.col--; // Decrement the column position to move left
        }
    }

    // Method to check if the block can move left without collision
    private boolean canMoveLeft(Block block) {
        for (int row = 0; row < block.shape.length; row++) { // Loop through each row of the block's shape
            for (int col = 0; col < block.shape[row].length; col++) { // Loop through each column of the block's shape
                if (block.shape[row][col] != 0) { // If the current cell is occupied
                    int newRow = block.row + row; // Calculate the new row position (unchanged)
                    int newCol = block.col + col - 1; // Calculate the new column position after moving left
                    // If newCol is out of bounds or the board cell is occupied
                    if (newCol < 0 || (newRow >= 0 && board[newRow][newCol] != 0)) {
                        return false; // The block cannot move left
                    }
                }
            }
        }
        return true; // The block can move left
    }

    // Method to move the current block right if possible
    private void moveBlockRight() {
        if (canMoveRight(currentBlock)) { // Check if moving right is allowed
            currentBlock.col++; // Increment the column position to move right
        }
    }

    // Method to check if the block can move right without collision
    private boolean canMoveRight(Block block) {
        for (int row = 0; row < block.shape.length; row++) { // Loop through each row of the block's shape
            for (int col = 0; col < block.shape[row].length; col++) { // Loop through each column of the block's shape
                if (block.shape[row][col] != 0) { // If the cell in the block is occupied
                    int newRow = block.row + row; // Calculate the new row position (unchanged)
                    int newCol = block.col + col + 1; // Calculate the new column position after moving right
                    // If newCol is out of board bounds or the board cell is occupied
                    if (newCol >= BOARD_WIDTH || (newRow >= 0 && board[newRow][newCol] != 0)) {
                        return false; // The block cannot move right
                    }
                }
            }
        }
        return true; // The block can move right
    }

    // Method to move the current block down faster when the user presses the down
    // arrow key
    private void moveBlockDown() {
        if (canMoveDown(currentBlock)) { // Check if the block can move down
            currentBlock.row++; // Move the block one row down
        }
    }

    // Method to rotate the current block
    private void rotateBlock() {
        currentBlock.rotate(); // Rotate the block 90 degrees clockwise
        if (!canMoveDown(currentBlock)) { // If the block collides after rotation
            // Rotate it back by rotating three more times (360 degrees total)
            currentBlock.rotate();
            currentBlock.rotate();
            currentBlock.rotate();
        }
    }

    // Override the paintComponent method to draw the game elements on the panel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call the superclass method to clear the panel and paint the background
        // Draw the blocks placed on the board
        for (int row = 0; row < BOARD_HEIGHT; row++) { // Loop through each row of the board
            for (int col = 0; col < BOARD_WIDTH; col++) { // Loop through each column of the board
                if (board[row][col] != 0) { // If the board cell is occupied by a block
                    g.setColor(COLORS[board[row][col] - 1]); // Set the drawing color based on the block's color index
                    // Draw the block as a filled rectangle at the calculated position
                    g.fillRect(col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
        // Draw the current falling block
        if (currentBlock != null) { // If there is a current block falling
            g.setColor(currentBlock.color); // Set the drawing color to the current block's color
            for (int row = 0; row < currentBlock.shape.length; row++) { // Loop through each row of the block's shape
                for (int col = 0; col < currentBlock.shape[row].length; col++) { // Loop through each column of the
                                                                                 // block's shape
                    if (currentBlock.shape[row][col] != 0) { // If the cell in the block is occupied
                        // Draw the block's cell at the appropriate position on the board
                        g.fillRect((currentBlock.col + col) * BLOCK_SIZE, (currentBlock.row + row) * BLOCK_SIZE,
                                BLOCK_SIZE, BLOCK_SIZE);
                    }
                }
            }
        }
        // Display the "Game Over" message if the game has ended
        if (isGameOver) { // If the game is over
            g.setColor(Color.BLACK); // Set the text color to black for contrast on a white background
            g.setFont(new Font("Arial", Font.BOLD, 36)); // Set the font for the game over message
            g.drawString("Game Over", 50, 300); // Draw the "Game Over" message on the panel
        }
    }

    // Main method to start the Tetris game
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris Game"); // Create a new JFrame with the title "Tetris Game"
        Question3B game = new Question3B(); // Create an instance of the Question3B game panel
        frame.add(game); // Add the game panel to the JFrame
        frame.pack(); // Pack the frame to fit the preferred size of its components
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the default close operation to exit the application
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true); // Make the frame visible
    }

    // Inner class to represent a Tetris block
    static class Block {
        int[][] shape; // 2D array representing the block's shape
        int row, col; // Current position of the block on the board
        Color color; // Color of the block
        int colorIndex; // Index of the block's color in the COLORS array

        // Constructor to initialize a Block with a given shape and color
        public Block(int[][] shape, Color color) {
            this.shape = shape; // Set the block's shape
            this.row = 0; // Initialize the block's starting row position to 0
            // Center the block horizontally on the board based on its shape's width
            this.col = BOARD_WIDTH / 2 - shape[0].length / 2;
            this.color = color; // Set the block's color
            this.colorIndex = getColorIndex(color); // Determine the color index from the COLORS array
        }

        // Method to get the index of the specified color from the COLORS array
        private int getColorIndex(Color color) {
            for (int i = 0; i < COLORS.length; i++) { // Loop through the COLORS array
                if (COLORS[i].equals(color)) { // If the current color matches the given color
                    return i; // Return the index of the matching color
                }
            }
            return -1; // Return -1 if the color is not found (should not occur)
        }

        // Method to rotate the block 90 degrees clockwise
        public void rotate() {
            int rows = shape.length; // Get the number of rows in the current shape
            int cols = shape[0].length; // Get the number of columns in the current shape
            int[][] rotated = new int[cols][rows]; // Create a new array for the rotated shape with swapped dimensions
            for (int i = 0; i < rows; i++) { // Loop through each row of the current shape
                for (int j = 0; j < cols; j++) { // Loop through each column of the current shape
                    // Rotate the shape by mapping the cell to its new position
                    rotated[j][rows - i - 1] = shape[i][j];
                }
            }
            shape = rotated; // Update the block's shape with the rotated version
        }
    }
}

/*
 * Input/Output:
 * Input:
 * - User interacts with the game using the keyboard:
 * • Left Arrow: Move block left
 * • Right Arrow: Move block right
 * • Down Arrow: Move block down faster
 * • Up Arrow: Rotate the block
 * 
 * Output:
 * - A graphical Tetris game window is displayed with a white background and
 * bright block colors.
 * - Blocks fall from the top and can be controlled by the user.
 * - Completed rows are cleared.
 * - When no new block can move down, the game ends and "Game Over" is
 * displayed.
 */
