import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Environment {
    private final int[][] track_;
    private State state_;
    private final List<State> start_ = new ArrayList<>();
    private final Random rand_ = new Random();

    Environment(int[][] track) {
        track_ = track;
        findStart();
        reset();
    }

    private void findStart() {
        for (int i = 0; i < track_.length; ++i)
            for (int j = 0; j < track_[i].length; ++j)
                if (track_[i][j] == Constants.START)
                    start_.add(new State(i, j, 0, 0));
    }

    void reset() {
        state_ = start_.get(rand_.nextInt(start_.size()));
    }

    private boolean isFinished(int row, int column) {
        for (int i = 0; i < track_.length; ++i)
            if (track_[i][track_[i].length - 1] == Constants.FINISH)
                if (row == i && column >= track_[i].length - 1)
                    return true;
        return false;
    }

    private boolean isOutOfBounds(int row, int column) {
        if (row >= track_.length || row < 0 || column >= track_[0].length || column < 0)
            return true;
        if (track_[row][state_.pos_column_] == Constants.BORDER)
            return true;
        if (track_[state_.pos_row_][column] == Constants.BORDER)
            return true;
        return track_[row][column] == Constants.BORDER;
    }

    private void velocityBound(int velocity_row, int velocity_column) {
        if (velocity_row > Constants.MAX_VELOCITY)
            state_.velocity_row_ = Constants.MAX_VELOCITY;
        if (velocity_row < -Constants.MAX_VELOCITY)
            state_.velocity_row_ = -Constants.MAX_VELOCITY;
        if (velocity_column > Constants.MAX_VELOCITY)
            state_.velocity_column_ = Constants.MAX_VELOCITY;
        if (velocity_column < -Constants.MAX_VELOCITY)
            state_.velocity_column_ = -Constants.MAX_VELOCITY;

        if (velocity_row == Constants.MIN_VELOCITY && velocity_column == Constants.MIN_VELOCITY && !start_.contains(state_))
            state_.velocity_row_--;
    }

    StepResult step(int action) {
        int new_velocity_row = state_.velocity_row_ + Constants.ACTION[action][0];
        int new_velocity_column = state_.velocity_column_ + Constants.ACTION[action][1];
        velocityBound(new_velocity_row, new_velocity_column);

        int new_row = state_.pos_row_ + new_velocity_row;
        int new_column = state_.pos_column_ + new_velocity_column;

        boolean done = false;

        if (isFinished(new_row, new_column)) {
            done = true;
            //state_ = new State(new_row, track_[new_row].length - 1, new_velocity_row, new_velocity_column);
        }
        else if (isOutOfBounds(new_row, new_column)) {
            reset();
        }
        else {
            state_ = new State(new_row, new_column, new_velocity_row, new_velocity_column);
        }
        return new StepResult(state_, done);
    }

    State getState() {
        return state_;
    }

    int[][] getTrack() {
        return track_;
    }

    List<State> getStartLine() {
        return start_;
    }

    void setStartPos(State state) {
        state_ = state;
    }
}