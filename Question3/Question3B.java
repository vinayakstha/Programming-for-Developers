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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Question3B extends JPanel implements ActionListener {

    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int BLOCK_SIZE = 30;
    private static final Color[] COLORS = { Color.YELLOW, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK };

    private Timer timer;
    private boolean isGameOver;
    private int[][] board;
    private Block currentBlock;
    private Queue<Block> blockQueue;

    // Initialize game board, key listener, timer and first block
    public Question3B() {
        setPreferredSize(new Dimension(BOARD_WIDTH * BLOCK_SIZE, BOARD_HEIGHT * BLOCK_SIZE));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isGameOver) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            moveBlockLeft();
                            break;
                        case KeyEvent.VK_RIGHT:
                            moveBlockRight();
                            break;
                        case KeyEvent.VK_DOWN:
                            moveBlockDown();
                            break;
                        case KeyEvent.VK_UP:
                            rotateBlock();
                            break;
                    }
                    repaint();
                }
            }
        });
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        blockQueue = new LinkedList<>();
        timer = new Timer(500, this);
        timer.start();
        generateNewBlock();
    }

    // Generate and enqueue a new block, then set the current block
    private void generateNewBlock() {
        Random random = new Random();
        int shapeIndex = random.nextInt(COLORS.length);
        int[][] shape = getRandomShape();
        Block block = new Block(shape, COLORS[shapeIndex]);
        blockQueue.add(block);
        currentBlock = blockQueue.poll();
    }

    // Return a random Tetris shape
    private int[][] getRandomShape() {
        int[][][] shapes = {
                { { 1, 1, 1, 1 } }, // I-shape
                { { 1, 1, 0 }, { 0, 1, 1 } }, // Z-shape
                { { 0, 1, 1 }, { 1, 1, 0 } }, // S-shape
                { { 1, 1, 1 }, { 0, 1, 0 } }, // T-shape
                { { 1, 1 }, { 1, 1 } } // O-shape
        };
        Random random = new Random();
        return shapes[random.nextInt(shapes.length)];
    }

    // Main game loop
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            if (canMoveDown(currentBlock)) {
                currentBlock.row++;
            } else {
                placeBlock(currentBlock);
                checkCompletedRows();
                generateNewBlock();
                if (!canMoveDown(currentBlock)) {
                    isGameOver = true;
                }
            }
            repaint();
        }
    }

    // Check if block can move down without collision
    private boolean canMoveDown(Block block) {
        for (int row = 0; row < block.shape.length; row++) {
            for (int col = 0; col < block.shape[row].length; col++) {
                if (block.shape[row][col] != 0) {
                    int newRow = block.row + row + 1;
                    int newCol = block.col + col;
                    if (newRow >= BOARD_HEIGHT || (newRow >= 0 && board[newRow][newCol] != 0)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Place block on board when it can no longer move down
    private void placeBlock(Block block) {
        for (int row = 0; row < block.shape.length; row++) {
            for (int col = 0; col < block.shape[row].length; col++) {
                if (block.shape[row][col] != 0) {
                    board[block.row + row][block.col + col] = block.colorIndex + 1;
                }
            }
        }
    }

    // Clear full rows and shift down the remaining rows
    private void checkCompletedRows() {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            boolean isComplete = true;
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] == 0) {
                    isComplete = false;
                    break;
                }
            }
            if (isComplete) {
                for (int r = row; r > 0; r--) {
                    board[r] = board[r - 1];
                }
                board[0] = new int[BOARD_WIDTH];
            }
        }
    }

    // Move block left if possible
    private void moveBlockLeft() {
        if (canMoveLeft(currentBlock)) {
            currentBlock.col--;
        }
    }

    private boolean canMoveLeft(Block block) {
        for (int row = 0; row < block.shape.length; row++) {
            for (int col = 0; col < block.shape[row].length; col++) {
                if (block.shape[row][col] != 0) {
                    int newRow = block.row + row;
                    int newCol = block.col + col - 1;
                    if (newCol < 0 || (newRow >= 0 && board[newRow][newCol] != 0)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Move block right if possible
    private void moveBlockRight() {
        if (canMoveRight(currentBlock)) {
            currentBlock.col++;
        }
    }

    private boolean canMoveRight(Block block) {
        for (int row = 0; row < block.shape.length; row++) {
            for (int col = 0; col < block.shape[row].length; col++) {
                if (block.shape[row][col] != 0) {
                    int newRow = block.row + row;
                    int newCol = block.col + col + 1;
                    if (newCol >= BOARD_WIDTH || (newRow >= 0 && board[newRow][newCol] != 0)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Move block down faster on key press
    private void moveBlockDown() {
        if (canMoveDown(currentBlock)) {
            currentBlock.row++;
        }
    }

    // Rotate block; if collision occurs after rotation, rotate back
    private void rotateBlock() {
        currentBlock.rotate();
        if (!canMoveDown(currentBlock)) {
            currentBlock.rotate();
            currentBlock.rotate();
            currentBlock.rotate();
        }
    }

    // Render board, current block, and game over message
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] != 0) {
                    g.setColor(COLORS[board[row][col] - 1]);
                    g.fillRect(col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
        if (currentBlock != null) {
            g.setColor(currentBlock.color);
            for (int row = 0; row < currentBlock.shape.length; row++) {
                for (int col = 0; col < currentBlock.shape[row].length; col++) {
                    if (currentBlock.shape[row][col] != 0) {
                        g.fillRect((currentBlock.col + col) * BLOCK_SIZE, (currentBlock.row + row) * BLOCK_SIZE,
                                BLOCK_SIZE, BLOCK_SIZE);
                    }
                }
            }
        }
        if (isGameOver) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Game Over", 50, 300);
        }
    }

    // Start the game window
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris Game");
        Question3B game = new Question3B();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Represents a Tetris block
    static class Block {
        int[][] shape;
        int row, col;
        Color color;
        int colorIndex;

        public Block(int[][] shape, Color color) {
            this.shape = shape;
            this.row = 0;
            this.col = BOARD_WIDTH / 2 - shape[0].length / 2;
            this.color = color;
            this.colorIndex = getColorIndex(color);
        }

        private int getColorIndex(Color color) {
            for (int i = 0; i < COLORS.length; i++) {
                if (COLORS[i].equals(color)) {
                    return i;
                }
            }
            return -1;
        }

        // Rotate the block 90 degrees clockwise
        public void rotate() {
            int rows = shape.length;
            int cols = shape[0].length;
            int[][] rotated = new int[cols][rows];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    rotated[j][rows - i - 1] = shape[i][j];
                }
            }
            shape = rotated;
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
