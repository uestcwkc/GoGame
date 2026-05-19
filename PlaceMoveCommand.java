package chess.command;

import chess.model.Board;
import chess.model.Position;
import chess.model.Stone;

import java.util.List;

/**
 * 落子命令（ConcreteCommand）
 * 记录落子位置、颜色、被提走的棋子，支持撤销。
 */
public class PlaceMoveCommand implements MoveCommand {
    private static final long serialVersionUID = 1L;

    private final Board          board;
    private final Position       pos;
    private final Stone          color;
    private final List<Position> captured;
    private final int            prevBlackCaptures;
    private final int            prevWhiteCaptures;
    private final int            prevBoardHash;
    private final int            prevPrevHash;

    public PlaceMoveCommand(Board board, Position pos, Stone color,
                            List<Position> captured,
                            int prevBlackCaptures, int prevWhiteCaptures,
                            int prevBoardHash, int prevPrevHash) {
        this.board             = board;
        this.pos               = pos;
        this.color             = color;
        this.captured          = captured;
        this.prevBlackCaptures = prevBlackCaptures;
        this.prevWhiteCaptures = prevWhiteCaptures;
        this.prevBoardHash     = prevBoardHash;
        this.prevPrevHash      = prevPrevHash;
    }

    @Override public void execute() {}

    @Override
    public void undo() {
        board.set(pos, Stone.EMPTY);
        Stone opp = color.opposite();
        for (Position p : captured) board.set(p, opp);
    }

    @Override
    public String describe() {
        return color.getSymbol() + " 落子于 " + pos
                + (captured.isEmpty() ? "" : "，提子 " + captured.size() + " 枚");
    }

    public Stone  getColor()             { return color; }
    public int    getPrevBlackCaptures() { return prevBlackCaptures; }
    public int    getPrevWhiteCaptures() { return prevWhiteCaptures; }
    public int    getPrevBoardHash()     { return prevBoardHash; }
    public int    getPrevPrevHash()      { return prevPrevHash; }
}
