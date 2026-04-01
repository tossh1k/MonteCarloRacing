import java.util.Objects;

public class State {
    int pos_row_;
    int pos_column_;
    int velocity_row_;
    int velocity_column_;

    public State(int row, int column, int velocity_row, int velocity_column) {
        pos_row_ = row;
        pos_column_ = column;
        velocity_row_ = velocity_row;
        velocity_column_ = velocity_column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State)o;
        return pos_row_ == state.pos_row_ && pos_column_ == state.pos_column_ &&
                velocity_row_ == state.velocity_row_ && velocity_column_ == state.velocity_column_;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos_row_, pos_column_, velocity_row_, velocity_column_);
    }
}
