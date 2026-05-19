package chess.ui;

import chess.game.AbstractGame;

/**
 * 抽象界面建造者（Abstract Builder）
 * 声明构建各界面组件的步骤，子类按需实现，默认空实现。
 */
public abstract class UIBuilder {

    protected ConsoleUI ui;
    protected AbstractGame game;

    public void init(AbstractGame game) {
        this.game = game;
        this.ui   = new ConsoleUI();
    }

    public void buildBoardArea()  {}
    public void buildStatusBar()  {}
    public void buildHintArea()   {}

    public ConsoleUI getResult()  { return ui; }
}
