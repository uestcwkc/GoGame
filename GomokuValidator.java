package chess.game;

import chess.model.Board;
import chess.model.Position;
import chess.model.Stone;

/** 五子棋落子合法性校验：位置必须在棋盘内且为空。 */
public class GomokuValidator implements MoveValidator {
    @Override
    public ValidationResult validate(Board board, Position pos, Stone color, int prevHash) {
        if (!board.inBounds(pos))
            return ValidationResult.fail("落子位置超出棋盘范围。");
        if (!board.isEmpty(pos))
            return ValidationResult.fail("该位置已有棋子，请选择其他位置。");
        return ValidationResult.ok();
    }
}
