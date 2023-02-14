import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SnakeGame extends JPanel implements KeyListener {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int UNIT_SIZE = 10;
    private static final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 100;
    private static final int MINIMUM_APPLE_DISTANCE = 20;

    private LinkedList<Point> snake;
    private Point apple;
    private int score;
    private char direction;
    private boolean running;
    private boolean paused;

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        initGame();
    }

    private void initGame() {
        snake = new LinkedList<>();
        snake.add(new Point(WIDTH / 2, HEIGHT / 2));
        direction = 'R';
        generateApple();
        score = 0;
        running = true;
        paused = false;

        Timer timer = new Timer();
        timer.schedule(new GameLoop(), DELAY, DELAY);
    }

    private void generateApple() {
        Random rand = new Random();
        int appleX = rand.nextInt((int) (WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        int appleY = rand.nextInt((int) (HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        apple = new Point(appleX, appleY);
        if (isAppleTooCloseToSnake()) {
            generateApple();
        }
    }

    private boolean isAppleTooCloseToSnake() {
        for (Point p : snake) {
            if (p.distance(apple) < MINIMUM_APPLE_DISTANCE) {
                return true;
            }
        }
        return false;
    }

    private void paintSnake(Graphics g) {
        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillOval(p.x, p.y, UNIT_SIZE, UNIT_SIZE);
        }
    }

    private void paintApple(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(apple.x, apple.y, UNIT_SIZE, UNIT_SIZE);
    }

    private void paintScore(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, 10, 20);
    }

    private void paintLength(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString("Length: " + snake.size(), 10, 40);
    }

    private void paintGameOver(Graphics g2d) {
        g2d.setColor(Color.BLACK);
        g2d.drawString("GAME OVER", WIDTH / 2 - 40, HEIGHT / 2);
        g2d.drawString("Press SPACE to restart", WIDTH / 2 - 70, HEIGHT / 2 + 20);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (running) {
            paintSnake(g);
            paintApple(g);
            paintScore(g);
            paintLength(g);
        } else {
            paintGameOver(g);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direction != 'R') {
                    direction = 'L';
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') {
                    direction = 'R';
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') {
                    direction = 'D';
                }
                break;
            case KeyEvent.VK_UP:
                if (direction != 'D') {
                    direction = 'U';
                }
                break;
            case KeyEvent.VK_SPACE:
                if (!running) {
                    initGame();
                } else {
                    paused = !paused;
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void move() {
        Point head = new Point(snake.getFirst());
        switch (direction) {
            case 'L':
                head.x -= UNIT_SIZE;
                break;
            case 'R':
                head.x += UNIT_SIZE;
                break;
            case 'U':
                head.y -= UNIT_SIZE;
                break;
            case 'D':
                head.y += UNIT_SIZE;
                break;
        }
        if (head.equals(apple)) {
            snake.addFirst(head);
            score++;
            generateApple();
        } else {
            snake.removeLast();
            if (isSnakeCollidingWithItself(head)) {
                running = false;
            } else if (isSnakeCollidingWithWall(head)) {
                running = false;
            } else {
                snake.addFirst(head);
            }
        }
    }

    private boolean isSnakeCollidingWithItself(Point head) {
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean isSnakeCollidingWithWall(Point head) {
        return head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT;
    }

    private class GameLoop extends TimerTask {
        @Override
        public void run() {
            if (!paused) {
                move();
                repaint();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new SnakeGameFrame();
        frame.setVisible(true);
    }
}

class SnakeGameFrame extends JFrame {

    public SnakeGameFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Snake Game");
        setResizable(false);
        add(new SnakeGame(), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }
}


