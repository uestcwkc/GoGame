package chess.ui;

import chess.game.AbstractGame;

/** 操作提示区组件：显示可用指令列表，可由用户隐藏/显示 */
public class HintAreaComponent implements UIComponent {

    private boolean visible = true;
    private final AbstractGame game;

    public HintAreaComponent(AbstractGame game) { this.game = game; }

    public void setVisible(boolean visible) { this.visible = visible; }
    public boolean isVisible()              { return visible; }

    @Override
    public void render() {
        if (!visible) return;
        System.out.println("  ┌─ 操作指令 ─────────────────────────────");
        if (!game.isGameOver()) {
            System.out.println("  │  place <列><行>   落子  (例: place D5)");
            if (game.getGameType() == AbstractGame.GameType.GO)
                System.out.println("  │  pass             虚着（围棋）");
            System.out.println("  │  undo             悔棋");
            System.out.println("  │  resign           投子认负");
            System.out.println("  │  restart          重新开始");
            System.out.println("  │  save <文件名>    保存局面");
            System.out.println("  │  load <文件名>    读取局面");
            System.out.println("  │  hint off         隐藏提示");
        } else {
            System.out.println("  │  new gomoku <尺寸>  开始五子棋 (8-19)");
            System.out.println("  │  new go <尺寸>      开始围棋   (8-19)");
            System.out.println("  │  load <文件名>      读取局面");
        }
        System.out.println("  └─────────────────────────────────────────");
    }
}
