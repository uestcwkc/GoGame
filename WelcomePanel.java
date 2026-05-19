package chess.gui;

import chess.facade.GameFacade;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * 欢迎/开始面板：选择游戏类型、棋盘大小，或读取存档。
 */
public class WelcomePanel extends JPanel {

    private final GameFacade    facade;
    private final MainFrame     owner;
    private final JComboBox<String> typeBox;
    private final JSpinner      sizeSpinner;
    private final JLabel        statusLabel;

    public WelcomePanel(MainFrame owner, GameFacade facade) {
        this.owner  = owner;
        this.facade = facade;
        setBackground(Theme.BG_DARK);
        setLayout(new GridBagLayout());

        // ── 中心卡片 ──
        JPanel card = new JPanel();
        card.setBackground(Theme.BG_PANEL);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(36, 48, 36, 48));

        // 标题
        JLabel title = new JLabel("棋类对战平台");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Gomoku & Go");
        subtitle.setFont(Theme.FONT_LABEL);
        subtitle.setForeground(Theme.TEXT_DIM);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        // 游戏类型
        JLabel typeLabel = makeLabel("游戏类型：");
        typeBox = new JComboBox<>(new String[]{"五子棋 (Gomoku)", "围棋 (Go)"});
        styleCombo(typeBox);

        // 棋盘大小
        JLabel sizeLabel = makeLabel("棋盘大小：");
        SpinnerNumberModel model = new SpinnerNumberModel(15, 8, 19, 1);
        sizeSpinner = new JSpinner(model);
        styleSpinner(sizeSpinner);

        // 更新默认大小：围棋默认19，五子棋默认15
        typeBox.addActionListener(e -> {
            sizeSpinner.setValue(typeBox.getSelectedIndex() == 0 ? 15 : 19);
        });

        // 按钮
        StyledButton startBtn = new StyledButton("开始游戏", Theme.BTN_SUCCESS, Theme.BTN_SUCCESS_H);
        startBtn.setPreferredSize(new Dimension(180, 38));
        startBtn.setMaximumSize(new Dimension(180, 38));
        startBtn.setAlignmentX(CENTER_ALIGNMENT);
        startBtn.addActionListener(e -> startGame());

        StyledButton loadBtn = new StyledButton("读取存档", Theme.ACCENT, Theme.ACCENT_HOVER);
        loadBtn.setPreferredSize(new Dimension(180, 38));
        loadBtn.setMaximumSize(new Dimension(180, 38));
        loadBtn.setAlignmentX(CENTER_ALIGNMENT);
        loadBtn.addActionListener(e -> loadGame());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(new Color(220, 100, 100));
        statusLabel.setAlignmentX(CENTER_ALIGNMENT);

        // 组装
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(28));
        card.add(makeRow(typeLabel, typeBox));
        card.add(Box.createVerticalStrut(12));
        card.add(makeRow(sizeLabel, sizeSpinner));
        card.add(Box.createVerticalStrut(24));
        card.add(startBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(loadBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(statusLabel);

        add(card);
    }

    private void startGame() {
        String type = typeBox.getSelectedIndex() == 0 ? "gomoku" : "go";
        int size = (int) sizeSpinner.getValue();
        String msg = facade.newGame(type, size);
        if (msg.startsWith("错误")) {
            statusLabel.setText(msg);
        } else {
            owner.showGamePanel();
        }
    }

    private void loadGame() {
        JFileChooser fc = new JFileChooser("saves");
        fc.setDialogTitle("选择存档文件");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            String msg = facade.loadGame(f.getAbsolutePath());
            if (msg.startsWith("错误") || msg.startsWith("读档失败")) {
                statusLabel.setText(msg);
            } else {
                owner.showGamePanel();
            }
        }
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_LABEL);
        l.setForeground(Theme.TEXT_PRIMARY);
        return l;
    }

    private JPanel makeRow(JLabel label, JComponent comp) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setBackground(Theme.BG_PANEL);
        label.setPreferredSize(new Dimension(80, 28));
        comp.setPreferredSize(new Dimension(160, 28));
        row.add(label);
        row.add(comp);
        return row;
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setBackground(Theme.BG_DARK);
        cb.setForeground(Theme.TEXT_PRIMARY);
        cb.setFont(Theme.FONT_LABEL);
    }

    private void styleSpinner(JSpinner sp) {
        sp.setBackground(Theme.BG_DARK);
        sp.setForeground(Theme.TEXT_PRIMARY);
        sp.setFont(Theme.FONT_LABEL);
    }
}
