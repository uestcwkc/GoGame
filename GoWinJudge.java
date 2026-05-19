package chess.game;

import chess.model.*;

import java.util.*;

/**
 * 围棋胜负判断（数目法）
 * extraData = { 黑提子数, 白提子数, 连续虚着次数 }
 * 当连续虚着 >= 2 时触发终局计算。
 * 黑方贴目 6.5（标准日本规则）。
 */
public class GoWinJudge implements WinJudge {

    private static final double KOMI = 6.5;

    @Override
    public GameResult judge(Board board, Position lastMove, Stone lastColor, int[] extraData) {
        int blackCaptures = extraData[0];
        int whiteCaptures = extraData[1];
        int consecutivePasses = extraData[2];

        // 只有双方均虚着时才终局
        if (consecutivePasses < 2) return GameResult.ongoing();

        return calculate(board, blackCaptures, whiteCaptures);
    }

    /** 强制终局计算（投子认负时由 AbstractGame 调用） */
    public GameResult calculate(Board board, int blackCaptures, int whiteCaptures) {
        int blackTerritory = 0, whiteTerritory = 0;
        boolean[][] visited = new boolean[board.getSize()][board.getSize()];

        for (int r = 0; r < board.getSize(); r++) {
            for (int c = 0; c < board.getSize(); c++) {
                if (!visited[r][c] && board.isEmpty(r, c)) {
                    // BFS 找空地连通块，判断归属
                    List<int[]> region = new ArrayList<>();
                    Set<Stone> borders = new HashSet<>();
                    Deque<int[]> queue = new ArrayDeque<>();
                    queue.add(new int[]{r, c});
                    visited[r][c] = true;
                    int[] dr = {-1,1,0,0}, dc = {0,0,-1,1};
                    while (!queue.isEmpty()) {
                        int[] cur = queue.poll();
                        region.add(cur);
                        for (int d = 0; d < 4; d++) {
                            int nr = cur[0]+dr[d], nc = cur[1]+dc[d];
                            if (!board.inBounds(nr, nc)) continue;
                            if (!visited[nr][nc]) {
                                if (board.isEmpty(nr, nc)) {
                                    visited[nr][nc] = true;
                                    queue.add(new int[]{nr, nc});
                                } else {
                                    borders.add(board.get(nr, nc));
                                }
                            } else if (!board.isEmpty(nr, nc)) {
                                borders.add(board.get(nr, nc));
                            }
                        }
                    }
                    if (borders.size() == 1) {
                        if (borders.contains(Stone.BLACK)) blackTerritory += region.size();
                        else                               whiteTerritory += region.size();
                    }
                }
            }
        }

        double blackScore = blackTerritory + blackCaptures;
        double whiteScore = whiteTerritory + whiteCaptures + KOMI;

        String detail = String.format(
            "黑方：地盘%d + 提子%d = %.0f；白方：地盘%d + 提子%d + 贴目%.1f = %.1f",
            blackTerritory, blackCaptures, blackScore,
            whiteTerritory, whiteCaptures, KOMI, whiteScore);

        if (blackScore > whiteScore)
            return new GameResult(GameResult.Status.BLACK_WIN, detail);
        else
            return new GameResult(GameResult.Status.WHITE_WIN, detail);
    }
}
