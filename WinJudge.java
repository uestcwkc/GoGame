package chess.game;

import chess.model.Board;
import chess.model.GameResult;
import chess.model.Position;
import chess.model.Stone;

/**
 * 胜负判断策略接口（Strategy）
 * 五子棋和围棋各自实现。
 */
public interface WinJudge {
    /**
     * @param board      当前棋盘
     * @param lastMove   最后一次落子位置（null 表示虚着）
     * @param lastColor  最后落子的颜色
     * @param extraData  附加数据（围棋：int[]{黑提子数, 白提子数, 连续虚着次数}）
     */
    GameResult judge(Board board, Position lastMove, Stone lastColor, int[] extraData);
}
