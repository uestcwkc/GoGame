package chess.ui;

import chess.model.Board;
import chess.model.Stone;

/**
 * 棋盘区组件
 * 使用字符画渲染棋盘：列标 A-T（跳过 I），行标 1-19。
 * 黑棋 ●，白棋 ○，空位 ·，边框用 + - | 绘制。
 */
public class BoardAreaComponent implements UIComponent {

    // 列标跳过 I，避免与数字 1 混淆
    private static final char[] COL_LABELS =
        "ABCDEFGHJKLMNOPQRST".toCharArray();

    private final Board board;

    public BoardAreaComponent(Board board) { this.board = board; }

    @Override
    public void render() {
        int size = board.getSize();
        // 列标题行
        System.out.print("     ");
        for (int c = 0; c < size; c++) {
            System.out.printf("%-3c", COL_LABELS[c]);
        }
        System.out.println();

        System.out.print("   +");
        System.out.println("-".repeat(size * 3 - 1) + "+");

        for (int r = 0; r < size; r++) {
            System.out.printf("%2d |", r + 1);
            for (int c = 0; c < size; c++) {
                Stone s = board.get(r, c);
                System.out.print(" " + s.getSymbol());
                if (c < size - 1) System.out.print(" ");
            }
            System.out.printf("| %-2d%n", r + 1);
        }

        System.out.print("   +");
        System.out.println("-".repeat(size * 3 - 1) + "+");

        System.out.print("     ");
        for (int c = 0; c < size; c++) {
            System.out.printf("%-3c", COL_LABELS[c]);
        }
        System.out.println();
    }

    /**
     * 将列字母转换为 col 索引（0-based），跳过 I
     */
    public static int colLabelToIndex(char label) {
        label = Character.toUpperCase(label);
        if (label >= 'A' && label <= 'H') return label - 'A';
        if (label >= 'J' && label <= 'T') return label - 'A' - 1;
        return -1;
    }

    public static char colIndexToLabel(int col) {
        return COL_LABELS[col];
    }
}
