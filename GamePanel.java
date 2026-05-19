package chess.gui;

import chess.facade.GameFacade;
import chess.game.AbstractGame;
import chess.model.Board;
import chess.model.Stone;

import javax.swing.*;
import java.awt.*;

/**
 * 对局面板：左侧棋盘 + 右侧信息面板
 * 作为 BoardPanel 和 SidePanel 的协调者，
 * 所有操作结果汇聚到 onActionResult() 统一处理。
 */
public class GamePanel extends JPanel {

    private final GameFacade  facade;
    private final MainFrame   owner;
    private final BoardPanel  boardPanel;
    private final SidePanel   sidePanel;

    // 记录最后落子坐标（传给 BoardPanel 高亮）
    private int lastRow = -1, lastCol = -1;

    public GamePanel(MainFrame owner, GameFacade facade) {
        this.owner     = owner;
        this.facade    = facade;
        this.boardPanel = new BoardPanel(facade, this);
        this.sidePanel  = new SidePanel(facade, this);

        setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout(0, 0));
        add(boardPanel, BorderLayout.CENTER);
        add(sidePanel,  BorderLayout.EAST);
    }

    /** 游戏开始或切换后初始化界面 */
    public void init() {
        lastRow = lastCol = -1;
        sidePanel.clearLog();
        refreshAll();
    }

    /**
     * 所有操作（落子、虚着、悔棋等）完成后统一调用
     * @param msg Facade 返回的结果描述
     */
    public void onActionResult(String msg) {
        if (msg == null || msg.isEmpty()) return;
        sidePanel.appendLog(msg);

        // 解析 lastRow/lastCol（从落子反馈中提取，简单处理）
        // BoardPanel 在 handleClick 中已设置 lastRow/lastCol，
        // 这里只负责刷新状态
        refreshAll();

        // 游戏结束时弹出提示
        if (msg.contains("获胜") || msg.contains("平局") || msg.contains("认负")) {
            String result = msg.contains(">>>") ? msg.split(">>>")[1].trim() : msg;
            sidePanel.setResult(result);
            JOptionPane.showMessageDialog(this,
                    result, "游戏结束", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** 刷新棋盘和侧边栏（每次状态变化后调用） */
    public void refreshAll() {
        AbstractGame game = facade.getCurrentGame();
        if (game == null) return;

        Board board = game.getBoard();
        Stone turn  = game.getCurrentTurn();
        boolean over = game.isGameOver();
        boolean isGo = game.getGameType() == AbstractGame.GameType.GO;

        boardPanel.refresh(board, turn, over, lastRow, lastCol);
        sidePanel.refresh(turn, isGo, game.getBlackCaptures(), game.getWhiteCaptures(), over);

        if (over) sidePanel.setResult(game.getResult().toString());

        repaint();
    }

    /** BoardPanel 落子后设置最后落子坐标 */
    public void setLastMove(int row, int col) {
        this.lastRow = row;
        this.lastCol = col;
    }

    /** 返回主页 */
    public void goHome() {
        owner.showWelcomePanel();
    }
}
