import java.util.*;

public class Agent {
    private final double[][][][][] Q_;
    private final double[][][][][] C_;
    private final int[][][][] pi_;
    private final Random rand_ = new Random();

    private final int rows_;
    private final int cols_;

    public Agent(int rows, int cols) {
        this.rows_ = rows;
        this.cols_ = cols;

        Q_ = new double[rows][cols][9][9][9];
        C_ = new double[rows][cols][9][9][9];
        pi_ = new int[rows][cols][9][9];

        initializeArrays();
    }

    private void initializeArrays() {
        for (int r = 0; r < rows_; r++) {
            for (int c = 0; c < cols_; c++) {
                for (int vr = 0; vr < 9; vr++) {
                    for (int vc = 0; vc < 9; vc++) {
                        Arrays.fill(Q_[r][c][vr][vc], -500.0);
                        Arrays.fill(C_[r][c][vr][vc], 0.0);
                        pi_[r][c][vr][vc] = 4;
                    }
                }
            }
        }
    }

    private int getVelocityIndex(int velocity) {
        return velocity + 4;
    }

    private ActionProbability b(State state) {
        int row = Math.max(0, Math.min(rows_ - 1, state.pos_row_));
        int col = Math.max(0, Math.min(cols_ - 1, state.pos_column_));
        int velRowIdx = Math.max(0, Math.min(8, getVelocityIndex(state.velocity_row_)));
        int velColIdx = Math.max(0, Math.min(8, getVelocityIndex(state.velocity_column_)));

        int greedy_action = pi_[row][col][velRowIdx][velColIdx];

        if (rand_.nextDouble() >= Constants.EPSILON) {
            double probability = 1.0 - Constants.EPSILON + Constants.EPSILON / Constants.ACTION_NUM;
            return new ActionProbability(greedy_action, probability);
        }
        else {
            int action = rand_.nextInt(Constants.ACTION_NUM);
            while (action == greedy_action)
                action = rand_.nextInt(Constants.ACTION_NUM);
            double probability = Constants.EPSILON / (Constants.ACTION_NUM - 1);
            return new ActionProbability(action, probability);
        }
    }

    private void updateValues(List<Experience> trajectory) {
        double G = 0.0;
        double W = 1.0;

        for (int i = trajectory.size() - 1; i >= 0; --i) {
            Experience exp = trajectory.get(i);
            G = 0.9 * G + Constants.REWARD;

            State state = exp.state_;
            int action = exp.action_;

            int row = Math.max(0, Math.min(rows_ - 1, state.pos_row_));
            int col = Math.max(0, Math.min(cols_ - 1, state.pos_column_));
            int velRowIdx = Math.max(0, Math.min(8, getVelocityIndex(state.velocity_row_)));
            int velColIdx = Math.max(0, Math.min(8, getVelocityIndex(state.velocity_column_)));

            C_[row][col][velRowIdx][velColIdx][action] += W;
            Q_[row][col][velRowIdx][velColIdx][action] +=
                    (W / C_[row][col][velRowIdx][velColIdx][action]) *
                            (G - Q_[row][col][velRowIdx][velColIdx][action]);

            int best_action = 0;
            double best_val = Double.NEGATIVE_INFINITY;
            double[] stateQ = Q_[row][col][velRowIdx][velColIdx];

            for (int a = 0; a < stateQ.length; ++a) {
                if (stateQ[a] > best_val) {
                    best_val = stateQ[a];
                    best_action = a;
                }
            }
            pi_[row][col][velRowIdx][velColIdx] = best_action;

            if (action != best_action)
                break;

            W *= (1.0 / exp.probability_);
        }
    }

    void MonteCarlo(Environment environment, int episodes) {
        Runtime runtime = Runtime.getRuntime();

        for (int i = 0; i < episodes; ++i) {
            List<Experience> trajectory = new ArrayList<>();
            environment.reset();
            State state = environment.getState();
            ActionProbability act_prob = b(state);
            boolean done = false;
            int steps = 0;

            while (!done) {
                int action = act_prob.action_;
                if (rand_.nextDouble() < Constants.NOISE)
                    action = 4;

                StepResult step = environment.step(action);
                done = step.done_;

                trajectory.add(new Experience(state, action, act_prob.probability_));
                state = step.state_;
                act_prob = b(state);
                steps++;
            }

            updateValues(trajectory);

            if (i % 1000 == 0) {
                long usedMB = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
                System.out.printf("Episode #%d, Steps = %d, Memory = %d MB\n", i, steps, usedMB);
                System.gc();
            }
        }
    }

    public int getAction(State state) {
        int row = Math.max(0, Math.min(rows_ - 1, state.pos_row_));
        int col = Math.max(0, Math.min(cols_ - 1, state.pos_column_));
        int velRowIdx = Math.max(0, Math.min(8, getVelocityIndex(state.velocity_row_)));
        int velColIdx = Math.max(0, Math.min(8, getVelocityIndex(state.velocity_column_)));

        return pi_[row][col][velRowIdx][velColIdx];
    }

    private static class Experience {
        final State state_;
        final int action_;
        final double probability_;

        Experience(State state, int action, double probability) {
            state_ = state;
            action_ = action;
            probability_ = probability;
        }
    }

    private static class ActionProbability {
        final int action_;
        final double probability_;

        ActionProbability(int action, double probability) {
            action_ = action;
            probability_ = probability;
        }
    }
}