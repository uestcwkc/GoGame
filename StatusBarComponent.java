package chess.ui;

import chess.game.AbstractGame;
import chess.model.Stone;

/** 状态栏组件：显示当前回合、游戏类型、提子数等信息 */
public class StatusBarComponent implements UIComponent {

    private final AbstractGame game;

    public StatusBarComponent(AbstractGame game) { this.game = game; }

    @Override
    public void render() {
        System.out.println("━".repeat(50));
        if (game.isGameOver()) {
            System.out.println("  【游戏结束】" + game.getResult());
        } else {
            String type = game.getGameType() == AbstractGame.GameType.GO ? "围棋" : "五子棋";
            System.out.printf("  【%s %dx%d】当前回合：%s%n",
                    type, game.getBoardSize(), game.getBoardSize(),
                    game.getCurrentTurn() == Stone.BLACK ? "黑棋 ●" : "白棋 ○");
            if (game.getGameType() == AbstractGame.GameType.GO) {
                System.out.printf("  黑棋提子：%d 枚    白棋提子：%d 枚%n",
                        game.getBlackCaptures(), game.getWhiteCaptures());
            }
        }
        System.out.println("━".repeat(50));
    }
}
