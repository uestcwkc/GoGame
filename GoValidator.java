package chess.game;

import chess.model.Board;
import chess.model.Position;
import chess.model.Stone;

import java.util.*;

/**
 * 围棋落子合法性校验：
 *   1. 位置在棋盘内且为空
 *   2. 落子后己方连通块有气（否则为禁着），除非能提取对方棋子
 *   3. 禁止打劫（落子后棋盘 hash 等于上上步 hash）
 */
public class GoValidator implements MoveValidator {

    @Override
    public ValidationResult validate(Board board, Position pos, Stone color, int prevHash) {
        if (!board.inBounds(pos))
            return ValidationResult.fail("落子位置超出棋盘范围。");
        if (!board.isEmpty(pos))
            return ValidationResult.fail("该位置已有棋子，请选择其他位置。");

        // 模拟落子，检查合法性
        Board sim = board.snapshot();
        sim.set(pos, color);

        // 先提掉对方无气的棋子
        Stone opp = color.opposite();
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        for (int d = 0; d < 4; d++) {
            int nr = pos.row + dr[d], nc = pos.col + dc[d];
            if (sim.inBounds(nr, nc) && sim.get(nr, nc) == opp) {
                Set<Position> group = findGroup(sim, new Position(nr, nc));
                if (countLiberties(sim, group) == 0) {
                    for (Position p : group) sim.set(p, Stone.EMPTY);
                }
            }
        }

        // 检查己方是否有气（落子后）
        Set<Position> myGroup = findGroup(sim, pos);
        if (countLiberties(sim, myGroup) == 0)
            return ValidationResult.fail("禁着点：落子后该棋子无气。");

        // 打劫检测
        if (sim.boardHash() == prevHash)
            return ValidationResult.fail("禁止打劫：该落子将重复上一局面。");

        return ValidationResult.ok();
    }

    /** BFS 找连通块 */
    public static Set<Position> findGroup(Board board, Position start) {
        Set<Position> group = new HashSet<>();
        if (board.get(start) == Stone.EMPTY) return group;
        Stone color = board.get(start);
        Deque<Position> queue = new ArrayDeque<>();
        queue.add(start);
        group.add(start);
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        while (!queue.isEmpty()) {
            Position cur = queue.poll();
            for (int d = 0; d < 4; d++) {
                int nr = cur.row + dr[d], nc = cur.col + dc[d];
                Position nb = new Position(nr, nc);
                if (board.inBounds(nr, nc) && !group.contains(nb)
                        && board.get(nr, nc) == color) {
                    group.add(nb);
                    queue.add(nb);
                }
            }
        }
        return group;
    }

    /** 计算连通块的气数 */
    public static int countLiberties(Board board, Set<Position> group) {
        Set<Position> liberties = new HashSet<>();
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        for (Position p : group) {
            for (int d = 0; d < 4; d++) {
                int nr = p.row + dr[d], nc = p.col + dc[d];
                if (board.inBounds(nr, nc) && board.isEmpty(nr, nc))
                    liberties.add(new Position(nr, nc));
            }
        }
        return liberties.size();
    }
}
