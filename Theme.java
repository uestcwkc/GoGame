package chess.gui;

import java.awt.Color;
import java.awt.Font;

/** 全局配色与字体常量，统一管理 UI 风格。 */
public final class Theme {
    private Theme() {}

    // 颜色
    public static final Color BG_DARK       = new Color(40,  44,  52);
    public static final Color BG_PANEL      = new Color(55,  60,  70);
    public static final Color BG_SIDE       = new Color(48,  52,  62);
    public static final Color BOARD_LIGHT   = new Color(220, 185, 130);  // 木色浅
    public static final Color BOARD_DARK    = new Color(200, 162, 100);  // 木色深（星位）
    public static final Color GRID_LINE     = new Color(100,  70,  30);
    public static final Color STONE_BLACK   = new Color(20,   20,  20);
    public static final Color STONE_WHITE   = new Color(245, 245, 245);
    public static final Color STONE_SHADOW  = new Color(0, 0, 0, 80);
    public static final Color LAST_MARK     = new Color(220,  50,  50);
    public static final Color HOVER_MARK    = new Color(100, 180, 100, 140);
    public static final Color TEXT_PRIMARY  = new Color(230, 230, 230);
    public static final Color TEXT_DIM      = new Color(160, 160, 160);
    public static final Color ACCENT        = new Color(90,  160, 230);
    public static final Color ACCENT_HOVER  = new Color(120, 190, 255);
    public static final Color BTN_DANGER    = new Color(200,  70,  70);
    public static final Color BTN_DANGER_H  = new Color(230,  90,  90);
    public static final Color BTN_SUCCESS   = new Color(60,  160,  80);
    public static final Color BTN_SUCCESS_H = new Color(80,  190, 100);
    public static final Color SEPARATOR     = new Color(70,   76,  88);

    // 字体
    public static final Font FONT_TITLE   = new Font("SansSerif", Font.BOLD,  18);
    public static final Font FONT_LABEL   = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_BTN     = new Font("SansSerif", Font.BOLD,  13);
    public static final Font FONT_MONO    = new Font("Monospaced", Font.PLAIN, 12);
    public static final Font FONT_COORD   = new Font("SansSerif", Font.PLAIN, 11);
}
