package chess.ui;

import chess.game.AbstractGame;

/**
 * 具体界面建造者（Concrete Builder）
 * 构建完整的对局界面：状态栏 + 棋盘区 + 操作提示区。
 */
public class GameUIBuilder extends UIBuilder {

    private HintAreaComponent hintArea;

    @Override
    public void buildStatusBar() {
        ui.addComponent(new StatusBarComponent(game));
    }

    @Override
    public void buildBoardArea() {
        ui.addComponent(new BoardAreaComponent(game.getBoard()));
    }

    @Override
    public void buildHintArea() {
        hintArea = new HintAreaComponent(game);
        ui.addComponent(hintArea);
    }

    public HintAreaComponent getHintArea() { return hintArea; }
}
