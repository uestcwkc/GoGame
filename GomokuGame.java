package chess.game;

import chess.model.*;
import java.util.*;

/** 五子棋具体游戏（模板方法 ConcreteClass） */
public class GomokuGame extends AbstractGame {

    private static final long serialVersionUID = 1L;

    public GomokuGame(int boardSize) {
        super(boardSize, GameType.GOMOKU);
    }

    @Override
    protected MoveValidator createValidator() { return new GomokuValidator(); }

    @Override
    protected WinJudge createWinJudge() { return new GomokuWinJudge(); }

    @Override
    protected int[] getExtraData() { return null; }

    @Override
    protected List<Position> afterPlace(Position pos, Stone color) {
        // 五子棋落子后无需额外处理
        return Collections.emptyList();
    }
}
