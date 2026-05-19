package chess.ui;

import chess.game.AbstractGame;

/**
 * 界面指挥者（Director）
 * 规定构建顺序：状态栏 → 棋盘 → 提示区。
 * 客户端只与 Director 交互，无需关心组件细节。
 */
public class UIDirector {

    private UIBuilder builder;

    public UIDirector(UIBuilder builder) { this.builder = builder; }

    public void setBuilder(UIBuilder builder) { this.builder = builder; }

    /**
     * 构建完整对局界面并返回。
     */
    public ConsoleUI construct(AbstractGame game) {
        builder.init(game);
        builder.buildStatusBar();
        builder.buildBoardArea();
        builder.buildHintArea();
        return builder.getResult();
    }
}
