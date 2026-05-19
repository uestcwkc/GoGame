package chess.model;

import java.io.Serializable;
import java.util.Objects;

/** 棋盘坐标值对象（不可变），row/col 均从 0 开始。 */
public final class Position implements Serializable {
    private static final long serialVersionUID = 1L;
    public final int row;
    public final int col;

    public Position(int row, int col) { this.row = row; this.col = col; }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return row == p.row && col == p.col;
    }
    @Override public int hashCode() { return Objects.hash(row, col); }
    @Override public String toString() { return "(" + row + "," + col + ")"; }
}
