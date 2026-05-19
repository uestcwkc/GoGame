package chess.gui;

import chess.facade.GameFacade;

import javax.swing.*;
import java.awt.*;

/**
 * 主窗口（顶层容器）
 * 使用 CardLayout 在欢迎页和对局页之间切换。
 */
public class MainFrame extends JFrame {

    private static final String CARD_WELCOME = "welcome";
    private static final String CARD_GAME    = "game";

    private final GameFacade  facade;
    private final CardLayout  cardLayout;
    private final JPanel      cardPanel;

    private final WelcomePanel welcomePanel;
    private       GamePanel    gamePanel;

    public MainFrame() {
        super("棋类对战平台 — Gomoku & Go");
        this.facade     = new GameFacade();
        this.cardLayout = new CardLayout();
        this.cardPanel  = new JPanel(cardLayout);

        welcomePanel = new WelcomePanel(this, facade);
        cardPanel.add(welcomePanel, CARD_WELCOME);

        setContentPane(cardPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 720);
        setMinimumSize(new Dimension(700, 560));
        setLocationRelativeTo(null);
    }

    /** 从欢迎页跳转到对局页（游戏已通过 facade.newGame 初始化） */
    public void showGamePanel() {
        // 每次新开游戏重建 GamePanel
        if (gamePanel != null) cardPanel.remove(gamePanel);
        gamePanel = new GamePanel(this, facade);
        cardPanel.add(gamePanel, CARD_GAME);
        cardLayout.show(cardPanel, CARD_GAME);
        gamePanel.init();
    }

    /** 从对局页返回欢迎页 */
    public void showWelcomePanel() {
        cardLayout.show(cardPanel, CARD_WELCOME);
    }
}
