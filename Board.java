package chess.model;

import java.io.Serializable;

/**
 * 棋盘数据模型（8~19 路棋盘）
 * 二维 Stone 数组，提供基础读写与工具方法，实现 Serializable 支持存档。
 */
public class Board implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int size;
    private final Stone[][] grid;

    public Board(int size) {
        if (size < 8 || size > 19)
            throw new IllegalArgumentException("棋盘大小必须在 8~19 之间，当前：" + size);
        this.size = size;
        this.grid = new Stone[size][size];
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                grid[r][c] = Stone.EMPTY;
    }

    /** 深拷贝构造 */
    public Board(Board other) {
        this.size = other.size;
        this.grid = new Stone[size][size];
        for (int r = 0; r < size; r++)
            System.arraycopy(other.grid[r], 0, this.grid[r], 0, size);
    }

    public int   getSize()               { return size; }
    public Stone get(int row, int col)   { return grid[row][col]; }
    public Stone get(Position pos)       { return grid[pos.row][pos.col]; }
    public void  set(int r, int c, Stone s) { grid[r][c] = s; }
    public void  set(Position pos, Stone s) { grid[pos.row][pos.col] = s; }
    public boolean isEmpty(int r, int c)   { return grid[r][c] == Stone.EMPTY; }
    public boolean isEmpty(Position pos)   { return grid[pos.row][pos.col] == Stone.EMPTY; }
    public boolean inBounds(int r, int c)  { return r >= 0 && r < size && c >= 0 && c < size; }
    public boolean inBounds(Position pos)  { return inBounds(pos.row, pos.col); }

    public boolean isFull() {
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                if (grid[r][c] == Stone.EMPTY) return false;
        return true;
    }

    /** 棋盘状态哈希，用于围棋打劫检测 */
    public int boardHash() {
        int h = size;
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                h = h * 31 + grid[r][c].ordinal();
        return h;
    }

    public Board snapshot() { return new Board(this); }
}
