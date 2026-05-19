package chess.gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * GUI 客户端启动入口
 * 在 EDT（事件调度线程）中启动主窗口。
 */
public class GuiClient {
    public static void launch() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
