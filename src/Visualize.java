import javax.swing.*;
import java.awt.*;

public class Visualize extends JPanel {
    private final int[][] track;
    private State currentState;
    private static final int CELL_SIZE = 20;

    public Visualize(int[][] track) {
        this.track = track;
        setPreferredSize(new Dimension(track[0].length * CELL_SIZE, track.length * CELL_SIZE));
    }

    public void setState(State state) {
        this.currentState = state;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < track.length; i++) {
            for (int j = 0; j < track[i].length; j++) {
                Color color = getColor(track[i][j]);
                g.setColor(color);
                g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                g.setColor(Color.DARK_GRAY);
                g.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        if (currentState != null) {
            g.setColor(Color.BLUE);
            g.fillRect(currentState.pos_column_ * CELL_SIZE,
                    currentState.pos_row_ * CELL_SIZE,
                    CELL_SIZE, CELL_SIZE);
        }
    }

    private Color getColor(int cellType) {
        switch (cellType) {
            case Constants.ROAD:
                return Color.WHITE;
            case Constants.BORDER:
                return Color.GRAY;
            case Constants.START:
                return Color.RED;
            case Constants.FINISH:
                return Color.GREEN;
            default:
                return Color.WHITE;
        }
    }

    public static void demonstrateWithVisualization(Environment env, Agent agent, int episodes) {
        JFrame frame = new JFrame("Race Track");
        Visualize visualize = new Visualize(env.getTrack());
        frame.add(visualize);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        while (true) {
            int i = 0;
            for (; i < env.getStartLine().size(); ++i) {
                env.reset();
                env.setStartPos(env.getStartLine().get(i));
                State state = env.getState();
                visualize.setState(state);

                State startState = env.getStartLine().get(i);
                System.out.printf("Стартовая позиция=(%d, %d)%n", startState.pos_row_, startState.pos_column_);

                boolean done = false;
                int steps = 0;

                while (!done) {
                    int action = agent.getAction(state);
                    StepResult result = env.step(action);
                    done = result.done_;
                    state = result.state_;
                    visualize.setState(state);
                    steps++;

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                if (done) {
                    System.out.printf("Заезд за %d шагов, Финиш=(%d, %d)%n%n",
                            steps, state.pos_row_, state.pos_column_);
                }
            }
        }
    }
}