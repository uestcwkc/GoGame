package chess.game;

import chess.model.*;

/** 五子棋胜负判断：检查最后落子位置的八个方向是否有五连珠；棋盘满则平局。 */
public class GomokuWinJudge implements WinJudge {

    private static final int[][] DIRS = {{0,1},{1,0},{1,1},{1,-1}};

    @Override
    public GameResult judge(Board board, Position last, Stone lastColor, int[] extra) {
        if (last == null) return GameResult.ongoing();

        for (int[] d : DIRS) {
            int count = 1;
            count += countDir(board, last, lastColor,  d[0],  d[1]);
            count += countDir(board, last, lastColor, -d[0], -d[1]);
            if (count >= 5) {
                GameResult.Status s = (lastColor == Stone.BLACK)
                        ? GameResult.Status.BLACK_WIN : GameResult.Status.WHITE_WIN;
                return new GameResult(s, lastColor.getSymbol() + " 五子连珠");
            }
        }

        if (board.isFull())
            return new GameResult(GameResult.Status.DRAW, "棋盘已满");

        return GameResult.ongoing();
    }

    private int countDir(Board b, Position pos, Stone color, int dr, int dc) {
        int count = 0;
        int r = pos.row + dr, c = pos.col + dc;
        while (b.inBounds(r, c) && b.get(r, c) == color) {
            count++; r += dr; c += dc;
        }
        return count;
    }
}
