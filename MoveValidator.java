package chess.game;

import chess.model.Board;
import chess.model.Position;
import chess.model.Stone;

/**
 * 落子合法性校验策略接口（Strategy）
 * 五子棋和围棋各自实现，客户端面向接口调用。
 */
public interface MoveValidator {
    /**
     * @param board       当前棋盘
     * @param pos         落子坐标
     * @param color       落子颜色
     * @param prevHash    上一步的棋盘 hash（围棋打劫检测用）
     * @return 合法性结果
     */
    ValidationResult validate(Board board, Position pos, Stone color, int prevHash);
}
