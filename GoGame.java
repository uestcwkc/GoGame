package chess.game;

import chess.model.*;
import java.util.*;

/**
 * 围棋具体游戏（模板方法 ConcreteClass）
 * afterPlace() 负责提子逻辑：检查四邻对方棋子的连通块，无气则提走。
 */
public class GoGame extends AbstractGame {

    private static final long serialVersionUID = 1L;

    // 最近一次落子被提走的棋子坐标列表（供 GameFacade 构造命令使用）
    private transient List<Position> lastCaptured = new ArrayList<>();

    public GoGame(int boardSize) {
        super(boardSize, GameType.GO);
    }

    @Override
    protected MoveValidator createValidator() { return new GoValidator(); }

    @Override
    protected WinJudge createWinJudge() { return new GoWinJudge(); }

    @Override
    protected int[] getExtraData() {
        return new int[]{ blackCaptures, whiteCaptures, consecutivePasses };
    }

    /**
     * 落子后提走对方无气的棋子，返回被提走的坐标列表（用于命令撤销）。
     */
    @Override
    protected List<Position> afterPlace(Position pos, Stone color) {
        Stone opp = color.opposite();
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        lastCaptured = new ArrayList<>();

        for (int d = 0; d < 4; d++) {
            int nr = pos.row + dr[d], nc = pos.col + dc[d];
            if (!board.inBounds(nr, nc) || board.get(nr, nc) != opp) continue;
            Set<Position> group = GoValidator.findGroup(board, new Position(nr, nc));
            if (GoValidator.countLiberties(board, group) == 0) {
                for (Position p : group) {
                    board.set(p, Stone.EMPTY);
                    lastCaptured.add(p);
                }
                if (color == Stone.BLACK) blackCaptures += group.size();
                else                      whiteCaptures += group.size();
            }
        }
        return lastCaptured;
    }

    /** 获取最近一次落子被提走的棋子列表（Facade 构造命令用） */
    public List<Position> getLastCaptured() {
        return lastCaptured == null ? Collections.emptyList() : lastCaptured;
    }

    /** 围棋强制结算（投子认负时使用） */
    public GameResult forceCalculate() {
        return ((GoWinJudge) winJudge).calculate(board, blackCaptures, whiteCaptures);
    }
}
