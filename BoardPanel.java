package chess.gui;

import chess.facade.GameFacade;
import chess.model.Board;
import chess.model.Position;
import chess.model.Stone;
import chess.ui.BoardAreaComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

/**
 * 棋盘绘制组件（核心 UI）
 *
 * 负责：
 *   - 绘制木色棋盘、网格、星位、坐标标签
 *   - 绘制黑白棋子（带光晕/阴影效果）
 *   - 标记最后一步落子位置
 *   - 鼠标悬停预览落点
 *   - 鼠标点击触发落子，回调 GameFacade
 */
public class BoardPanel extends JPanel {

    private static final int PADDING    = 40;   // 棋盘边距（留给坐标标签）
    private static final int MIN_CELL   = 28;   // 最小格子像素
    private static final int PREF_CELL  = 44;   // 偏好格子像素

    private final GameFacade facade;
    private final GamePanel  gamePanel;

    private Board   board;
    private int     boardSize;
    private int     cellSize;

    // 鼠标悬停的落点（棋盘坐标）
    private int hoverRow = -1, hoverCol = -1;
    // 最后落子坐标（用于标记）
    private int lastRow  = -1, lastCol  = -1;
    // 当前轮到的颜色（用于悬停预览）
    private Stone currentTurn = Stone.BLACK;
    private boolean gameOver  = false;

    public BoardPanel(GameFacade facade, GamePanel gamePanel) {
        this.facade    = facade;
        this.gamePanel = gamePanel;
        setBackground(Theme.BG_DARK);
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
            @Override public void mouseExited(MouseEvent e) {
                hoverRow = hoverCol = -1;
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateHover(e.getX(), e.getY());
            }
        });
    }

    /** 刷新棋盘数据（每次状态变化后调用） */
    public void refresh(Board board, Stone turn, boolean over,
                        int lastR, int lastC) {
        this.board       = board;
        this.boardSize   = board.getSize();
        this.currentTurn = turn;
        this.gameOver    = over;
        this.lastRow     = lastR;
        this.lastCol     = lastC;
        repaint();
    }

    /** 重置悬停标记 */
    public void clearLastMove() { lastRow = lastCol = -1; }

    @Override
    public Dimension getPreferredSize() {
        int sz = boardSize > 0 ? boardSize : 15;
        int total = PADDING * 2 + PREF_CELL * (sz - 1);
        return new Dimension(total, total);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,       RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        cellSize = calcCellSize();
        int boardPx = cellSize * (boardSize - 1);
        int ox = (getWidth()  - boardPx) / 2;
        int oy = (getHeight() - boardPx) / 2;

        drawBoardBackground(g2, ox, oy, boardPx);
        drawGrid(g2, ox, oy);
        drawStarPoints(g2, ox, oy);
        drawCoordinates(g2, ox, oy);
        drawHover(g2, ox, oy);
        drawStones(g2, ox, oy);
        drawLastMoveMark(g2, ox, oy);

        g2.dispose();
    }

    // ── 绘制方法 ──────────────────────────────────────────────────

    private void drawBoardBackground(Graphics2D g2, int ox, int oy, int boardPx) {
        int pad = cellSize;
        g2.setColor(Theme.BOARD_LIGHT);
        g2.fillRoundRect(ox - pad, oy - pad, boardPx + pad * 2, boardPx + pad * 2, 12, 12);
        // 木纹条纹
        g2.setColor(new Color(0, 0, 0, 10));
        for (int i = 0; i < boardPx + pad * 2; i += 4) {
            g2.drawLine(ox - pad + i, oy - pad, ox - pad + i, oy - pad + boardPx + pad * 2);
        }
    }

    private void drawGrid(Graphics2D g2, int ox, int oy) {
        g2.setColor(Theme.GRID_LINE);
        g2.setStroke(new BasicStroke(1.0f));
        for (int i = 0; i < boardSize; i++) {
            int x = ox + i * cellSize;
            int y = oy + i * cellSize;
            g2.drawLine(ox, y, ox + (boardSize - 1) * cellSize, y);
            g2.drawLine(x, oy, x, oy + (boardSize - 1) * cellSize);
        }
        // 外框加粗
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRect(ox, oy, (boardSize - 1) * cellSize, (boardSize - 1) * cellSize);
        g2.setStroke(new BasicStroke(1.0f));
    }

    private void drawStarPoints(Graphics2D g2, int ox, int oy) {
        int[] stars = getStarPoints();
        int r = Math.max(3, cellSize / 10);
        g2.setColor(Theme.GRID_LINE);
        for (int idx = 0; idx < stars.length; idx += 2) {
            int px = ox + stars[idx]     * cellSize;
            int py = oy + stars[idx + 1] * cellSize;
            g2.fillOval(px - r, py - r, r * 2, r * 2);
        }
    }

    private int[] getStarPoints() {
        if (boardSize == 19) return new int[]{
            3,3, 9,3, 15,3, 3,9, 9,9, 15,9, 3,15, 9,15, 15,15};
        if (boardSize == 13) return new int[]{3,3, 9,3, 6,6, 3,9, 9,9};
        if (boardSize ==  9) return new int[]{2,2, 6,2, 4,4, 2,6, 6,6};
        return new int[]{};
    }

    private void drawCoordinates(Graphics2D g2, int ox, int oy) {
        g2.setFont(Theme.FONT_COORD);
        g2.setColor(new Color(80, 50, 20));
        FontMetrics fm = g2.getFontMetrics();
        for (int i = 0; i < boardSize; i++) {
            char col = BoardAreaComponent.colIndexToLabel(i);
            String row = String.valueOf(i + 1);
            // 列标（上下）
            int cx = ox + i * cellSize;
            g2.drawString(String.valueOf(col), cx - fm.charWidth(col) / 2,
                    oy - cellSize / 2 + fm.getAscent() / 2);
            g2.drawString(String.valueOf(col), cx - fm.charWidth(col) / 2,
                    oy + (boardSize - 1) * cellSize + cellSize / 2 + fm.getAscent() / 2);
            // 行标（左右）
            int cy = oy + i * cellSize;
            g2.drawString(row, ox - cellSize / 2 - fm.stringWidth(row) / 2,
                    cy + fm.getAscent() / 2);
            g2.drawString(row, ox + (boardSize - 1) * cellSize + cellSize / 2 - fm.stringWidth(row) / 2,
                    cy + fm.getAscent() / 2);
        }
    }

    private void drawHover(Graphics2D g2, int ox, int oy) {
        if (gameOver || hoverRow < 0 || hoverCol < 0) return;
        if (!board.isEmpty(hoverRow, hoverCol)) return;
        int px = ox + hoverCol * cellSize;
        int py = oy + hoverRow * cellSize;
        int r  = cellSize / 2 - 3;
        g2.setColor(currentTurn == Stone.BLACK
                ? new Color(20, 20, 20, 100)
                : new Color(245, 245, 245, 120));
        g2.fill(new Ellipse2D.Double(px - r, py - r, r * 2, r * 2));
    }

    private void drawStones(Graphics2D g2, int ox, int oy) {
        int r = cellSize / 2 - 2;
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Stone s = board.get(row, col);
                if (s == Stone.EMPTY) continue;
                int px = ox + col * cellSize;
                int py = oy + row * cellSize;
                drawStone(g2, px, py, r, s == Stone.BLACK);
            }
        }
    }

    private void drawStone(Graphics2D g2, int cx, int cy, int r, boolean black) {
        // 阴影
        g2.setColor(Theme.STONE_SHADOW);
        g2.fill(new Ellipse2D.Double(cx - r + 2, cy - r + 3, r * 2, r * 2));

        // 主体渐变
        Color c1 = black ? new Color(50, 50, 50) : new Color(255, 255, 255);
        Color c2 = black ? new Color(10, 10, 10)  : new Color(200, 200, 200);
        GradientPaint gp = new GradientPaint(
                cx - r / 2f, cy - r / 2f, c1,
                cx + r / 2f, cy + r / 2f, c2);
        g2.setPaint(gp);
        g2.fill(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));

        // 高光
        Color hilight = black ? new Color(255, 255, 255, 60) : new Color(255, 255, 255, 160);
        g2.setColor(hilight);
        int hr = r / 3;
        g2.fill(new Ellipse2D.Double(cx - r * 0.45, cy - r * 0.45, hr * 2, hr * 2));
    }

    private void drawLastMoveMark(Graphics2D g2, int ox, int oy) {
        if (lastRow < 0 || lastCol < 0) return;
        int px = ox + lastCol * cellSize;
        int py = oy + lastRow * cellSize;
        int r  = cellSize / 6;
        g2.setColor(Theme.LAST_MARK);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(px - r, py - r, r * 2, r * 2);
        g2.setStroke(new BasicStroke(1f));
    }

    // ── 交互处理 ──────────────────────────────────────────────────

    private void handleClick(int mx, int my) {
        if (gameOver || board == null) return;
        int[] rc = pixelToGrid(mx, my);
        if (rc == null) return;
        int row = rc[0], col = rc[1];
        if (!board.inBounds(row, col)) return;

        char colLabel = BoardAreaComponent.colIndexToLabel(col);
        String msg = facade.placeStone(colLabel, row + 1);
        // 只有落子成功才更新最后落子标记
        if (!msg.startsWith("非法") && !msg.startsWith("错误")) {
            lastRow = row;
            lastCol = col;
            gamePanel.setLastMove(row, col);
        }
        gamePanel.onActionResult(msg);
    }

    private void updateHover(int mx, int my) {
        int[] rc = pixelToGrid(mx, my);
        if (rc == null) {
            hoverRow = hoverCol = -1;
        } else {
            hoverRow = rc[0];
            hoverCol = rc[1];
        }
        repaint();
    }

    /** 将像素坐标转换为棋盘格坐标，如果超出范围返回 null */
    private int[] pixelToGrid(int mx, int my) {
        if (board == null) return null;
        int boardPx = cellSize * (boardSize - 1);
        int ox = (getWidth()  - boardPx) / 2;
        int oy = (getHeight() - boardPx) / 2;

        int col = Math.round((float)(mx - ox) / cellSize);
        int row = Math.round((float)(my - oy) / cellSize);

        if (col < 0 || col >= boardSize || row < 0 || row >= boardSize) return null;
        // 只有在格子附近（半格以内）才响应
        int px = ox + col * cellSize;
        int py = oy + row * cellSize;
        double dist = Math.sqrt((mx - px) * (mx - px) + (my - py) * (my - py));
        if (dist > cellSize * 0.45) return null;
        return new int[]{row, col};
    }

    private int calcCellSize() {
        if (boardSize <= 0) return PREF_CELL;
        int available = Math.min(getWidth(), getHeight()) - PADDING * 2;
        int cs = available / (boardSize - 1);
        return Math.max(cs, MIN_CELL);
    }
}
