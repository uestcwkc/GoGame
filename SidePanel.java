package chess.gui;

import chess.facade.GameFacade;
import chess.game.AbstractGame;
import chess.model.Stone;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * 右侧信息与操作面板
 * 显示：回合指示、提子数（围棋）、操作日志
 * 提供：虚着、悔棋、投子认负、重开、存档、读档、返回主页 按钮
 */
public class SidePanel extends JPanel {

    private final GameFacade facade;
    private final GamePanel  gamePanel;

    private final JLabel  turnLabel;
    private final JLabel  captureLabel;
    private final JLabel  resultLabel;
    private final JTextArea logArea;
    private final StyledButton passBtn;
    private final StyledButton undoBtn;
    private final StyledButton resignBtn;
    private final StyledButton restartBtn;

    public SidePanel(GameFacade facade, GamePanel gamePanel) {
        this.facade    = facade;
        this.gamePanel = gamePanel;

        setBackground(Theme.BG_SIDE);
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(220, 0));
        setBorder(new EmptyBorder(16, 14, 16, 14));

        // ── 顶部：状态信息 ──
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Theme.BG_SIDE);

        JLabel platformLabel = new JLabel("棋类对战平台");
        platformLabel.setFont(Theme.FONT_TITLE);
        platformLabel.setForeground(Theme.ACCENT);
        platformLabel.setAlignmentX(LEFT_ALIGNMENT);

        infoPanel.add(platformLabel);
        infoPanel.add(Box.createVerticalStrut(16));

        turnLabel = makeInfoLabel("当前回合：黑棋 ●");
        captureLabel = makeInfoLabel("");
        resultLabel  = makeInfoLabel("");
        resultLabel.setForeground(new Color(220, 180, 80));

        infoPanel.add(turnLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(captureLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(resultLabel);

        infoPanel.add(Box.createVerticalStrut(16));
        infoPanel.add(makeSep());
        infoPanel.add(Box.createVerticalStrut(12));

        // ── 操作按钮 ──
        passBtn    = new StyledButton("虚着 (Pass)");
        undoBtn    = new StyledButton("悔棋 (Undo)");
        resignBtn  = new StyledButton("认负 (Resign)", Theme.BTN_DANGER, Theme.BTN_DANGER_H);
        restartBtn = new StyledButton("重新开始");

        Dimension btnSize = new Dimension(192, 34);
        for (StyledButton b : new StyledButton[]{passBtn, undoBtn, resignBtn, restartBtn}) {
            b.setPreferredSize(btnSize);
            b.setMaximumSize(btnSize);
            b.setAlignmentX(LEFT_ALIGNMENT);
        }

        passBtn  .addActionListener(e -> gamePanel.onActionResult(facade.pass()));
        undoBtn  .addActionListener(e -> gamePanel.onActionResult(facade.undo()));
        resignBtn.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this,
                    "确认投子认负？", "投子认负", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION)
                gamePanel.onActionResult(facade.resign());
        });
        restartBtn.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this,
                    "确认重新开始？当前局面将丢失。", "重新开始", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION)
                gamePanel.onActionResult(facade.restart());
        });

        infoPanel.add(passBtn);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(undoBtn);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(resignBtn);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(restartBtn);
        infoPanel.add(Box.createVerticalStrut(16));
        infoPanel.add(makeSep());
        infoPanel.add(Box.createVerticalStrut(12));

        // 存档/读档
        StyledButton saveBtn = new StyledButton("保存局面");
        StyledButton loadBtn = new StyledButton("读取存档");
        StyledButton homeBtn = new StyledButton("返回主页", new Color(90, 90, 90), new Color(110, 110, 110));

        for (StyledButton b : new StyledButton[]{saveBtn, loadBtn, homeBtn}) {
            b.setPreferredSize(btnSize);
            b.setMaximumSize(btnSize);
            b.setAlignmentX(LEFT_ALIGNMENT);
        }

        saveBtn.addActionListener(e -> saveGame());
        loadBtn.addActionListener(e -> loadGame());
        homeBtn.addActionListener(e -> gamePanel.goHome());

        infoPanel.add(saveBtn);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(loadBtn);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(homeBtn);

        // ── 底部：操作日志 ──
        logArea = new JTextArea(6, 18);
        logArea.setEditable(false);
        logArea.setBackground(new Color(30, 33, 40));
        logArea.setForeground(Theme.TEXT_DIM);
        logArea.setFont(Theme.FONT_MONO);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(new EmptyBorder(6, 6, 6, 6));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.SEPARATOR));
        scroll.setBackground(Theme.BG_SIDE);

        add(infoPanel, BorderLayout.NORTH);
        add(scroll,    BorderLayout.CENTER);
    }

    /** 刷新状态显示 */
    public void refresh(Stone turn, boolean isGo,
                        int bCap, int wCap, boolean over) {
        if (over) {
            turnLabel.setText("【游戏已结束】");
            turnLabel.setForeground(new Color(220, 180, 80));
        } else {
            turnLabel.setText("当前回合：" + (turn == Stone.BLACK ? "黑棋 ●" : "白棋 ○"));
            turnLabel.setForeground(turn == Stone.BLACK ? new Color(200, 200, 200) : new Color(240, 240, 240));
        }
        captureLabel.setText(isGo
                ? "黑提：" + bCap + " 枚  白提：" + wCap + " 枚"
                : "");
        passBtn.setEnabled(!over && isGo);
        undoBtn.setEnabled(!over);
        resignBtn.setEnabled(!over);
    }

    public void setResult(String text) {
        resultLabel.setText("<html><b>" + text + "</b></html>");
    }

    public void appendLog(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void clearLog() { logArea.setText(""); }

    // ── 存档/读档 ──────────────────────────────────────────────────

    private void saveGame() {
        JFileChooser fc = new JFileChooser("saves");
        fc.setDialogTitle("保存局面");
        fc.setSelectedFile(new File("saves/save1.sav"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String msg = facade.saveGame(fc.getSelectedFile().getAbsolutePath());
            appendLog(msg);
        }
    }

    private void loadGame() {
        JFileChooser fc = new JFileChooser("saves");
        fc.setDialogTitle("读取存档");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String msg = facade.loadGame(fc.getSelectedFile().getAbsolutePath());
            appendLog(msg);
            if (!msg.startsWith("错误") && !msg.startsWith("读档失败"))
                gamePanel.refreshAll();
        }
    }

    // ── 工具方法 ──────────────────────────────────────────────────

    private JLabel makeInfoLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_LABEL);
        l.setForeground(Theme.TEXT_PRIMARY);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JSeparator makeSep() {
        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.SEPARATOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
}
