package chess.facade;

import chess.archive.*;
import chess.game.AbstractGame;
import chess.command.*;
import chess.game.*;
import chess.model.*;
import chess.ui.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 游戏外观（Facade）
 * 整合后端所有模块（游戏逻辑、命令历史、存档、界面），
 * 向客户端提供简洁统一的操作接口。
 */
public class GameFacade {

    private AbstractGame      game;
    private CommandHistory    history;
    private ConsoleUI         ui;
    private HintAreaComponent hintArea;

    private final UIDirector    director;
    private final GameUIBuilder builder;

    public GameFacade() {
        this.builder  = new GameUIBuilder();
        this.director = new UIDirector(builder);
    }

    // ── 游戏管理 ─────────────────────────────────────────

    public String newGame(String type, int boardSize) {
        if (boardSize < 8 || boardSize > 19)
            return "错误：棋盘大小必须在 8~19 之间。";
        if ("gomoku".equalsIgnoreCase(type)) {
            game = new GomokuGame(boardSize);
        } else if ("go".equalsIgnoreCase(type)) {
            game = new GoGame(boardSize);
        } else {
            return "错误：未知游戏类型 [" + type + "]，请输入 gomoku 或 go。";
        }
        history = new CommandHistory();
        rebuildUI();
        return "游戏已开始："
                + (game.getGameType() == AbstractGame.GameType.GO ? "围棋" : "五子棋")
                + " " + boardSize + "x" + boardSize + "，黑棋先行。";
    }

    public String restart() {
        if (game == null) return "错误：尚未开始游戏。";
        return newGame(
                game.getGameType() == AbstractGame.GameType.GO ? "go" : "gomoku",
                game.getBoardSize());
    }

    // ── 落子 ─────────────────────────────────────────────

    public String placeStone(char colLabel, int row) {
        if (game == null || game.isGameOver())
            return "错误：游戏未进行中，请先开始游戏。";

        int col = BoardAreaComponent.colLabelToIndex(colLabel);
        if (col < 0)
            return "错误：无效列标 '" + colLabel + "'，请使用 A-T（跳过 I）。";
        if (col >= game.getBoardSize())
            return "错误：列标超出棋盘范围（最大列：" +
                    BoardAreaComponent.colIndexToLabel(game.getBoardSize()-1) + "）。";

        int r = row - 1;
        if (r < 0 || r >= game.getBoardSize())
            return "错误：行号 " + row + " 超出范围（1~" + game.getBoardSize() + "）。";

        // 记录落子前的状态
        int   prevBlack = game.getBlackCaptures();
        int   prevWhite = game.getWhiteCaptures();
        int   prevHash  = game.getPrevBoardHash();
        int   prevPrev  = game.getPrevPrevHash();
        Stone turnBefore = game.getCurrentTurn();

        Position pos = new Position(r, col);
        ValidationResult vr = game.placeStone(pos);
        if (!vr.isValid()) return "非法落子：" + vr.getMessage();

        // 获取被提走的棋子（围棋）
        List<Position> captured = Collections.emptyList();
        if (game.getGameType() == AbstractGame.GameType.GO) {
            captured = ((GoGame) game).getLastCaptured();
        }

        history.push(new PlaceMoveCommand(
                game.getBoard(), pos, turnBefore, captured,
                prevBlack, prevWhite, prevHash, prevPrev));

        return buildMoveResult(turnBefore.getSymbol()
                + " 落子于 " + colLabel + row
                + (captured.isEmpty() ? "" : "，提子 " + captured.size() + " 枚"));
    }

    // ── 虚着 ─────────────────────────────────────────────

    public String pass() {
        if (game == null || game.isGameOver()) return "错误：游戏未进行中。";
        Stone turnBefore    = game.getCurrentTurn();
        int prevConsPass    = game.getConsecutivePasses();
        int prevHash        = game.getPrevBoardHash();
        int prevPrev        = game.getPrevPrevHash();

        ValidationResult vr = game.pass();
        if (!vr.isValid()) return "非法操作：" + vr.getMessage();

        history.push(new PassMoveCommand(turnBefore, prevConsPass, prevHash, prevPrev));
        return buildMoveResult(turnBefore.getSymbol() + " 选择虚着（pass）");
    }

    // ── 悔棋 ─────────────────────────────────────────────

    public String undo() {
        if (game == null)         return "错误：游戏未进行中。";
        if (game.isGameOver())    return "错误：游戏已结束，无法悔棋。";
        if (history.isEmpty())    return "错误：当前无棋可悔。";

        MoveCommand cmd = history.pop();
        cmd.undo();

        // 恢复回合
        game.setCurrentTurn(game.getCurrentTurn().opposite());

        if (cmd instanceof PlaceMoveCommand) {
            PlaceMoveCommand pc = (PlaceMoveCommand) cmd;
            game.setBlackCaptures(pc.getPrevBlackCaptures());
            game.setWhiteCaptures(pc.getPrevWhiteCaptures());
            game.setPrevBoardHash(pc.getPrevBoardHash());
            game.setPrevPrevHash(pc.getPrevPrevHash());
            game.setConsecutivePasses(0);
        } else if (cmd instanceof PassMoveCommand) {
            PassMoveCommand pc = (PassMoveCommand) cmd;
            game.setConsecutivePasses(pc.getPrevConsecutivePasses());
            game.setPrevBoardHash(pc.getPrevBoardHash());
            game.setPrevPrevHash(pc.getPrevPrevHash());
        }
        game.setResult(GameResult.ongoing());
        game.setGameOver(false);

        return "悔棋成功，撤销：" + cmd.describe();
    }

    // ── 投子认负 ──────────────────────────────────────────

    public String resign() {
        if (game == null || game.isGameOver()) return "错误：游戏未进行中。";
        GameResult result = game.resign();
        history.clear();
        return "投子认负！" + result;
    }

    // ── 存档 ─────────────────────────────────────────────

    public String saveGame(String fileName) {
        if (game == null) return "错误：当前没有进行中的游戏。";
        String path = toSavePath(fileName);
        try {
            GameArchiveManager.save(new GameMemento(game, history, "手动存档"), path);
            return "存档成功：" + path;
        } catch (IOException e) {
            return "存档失败：" + e.getMessage();
        }
    }

    public String loadGame(String fileName) {
        String path = toSavePath(fileName);
        if (!GameArchiveManager.exists(path))
            return "错误：存档文件不存在：" + path;
        try {
            GameMemento memento = GameArchiveManager.load(path);
            this.game    = memento.getGame();
            this.history = memento.getHistory();
            rebuildUI();
            return "读档成功：" + path;
        } catch (Exception e) {
            return "读档失败：" + e.getMessage();
        }
    }

    // ── 界面 ─────────────────────────────────────────────

    public void renderUI() {
        if (ui == null) {
            printWelcome();
            return;
        }
        System.out.println();
        ui.render();
    }

    public String toggleHint(boolean show) {
        if (hintArea == null) return "";
        hintArea.setVisible(show);
        return show ? "操作提示已显示。" : "操作提示已隐藏。";
    }

    public boolean isGameActive()  { return game != null && !game.isGameOver(); }
    public boolean isGameStarted() { return game != null; }

    // ── 内部工具 ──────────────────────────────────────────

    private void rebuildUI() {
        ui       = director.construct(game);
        hintArea = builder.getHintArea();
    }

    private String buildMoveResult(String desc) {
        if (game.isGameOver())
            return desc + "\n>>> " + game.getResult();
        return desc;
    }

    private String toSavePath(String name) {
        String n = name.contains(".") ? name : name + ".sav";
        return n.contains("/") || n.contains("\\") ? n : "saves/" + n;
    }

    private void printWelcome() {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║          棋类游戏平台  v1.0                  ║");
        System.out.println("║  支持五子棋（Gomoku）和围棋（Go）             ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  new gomoku <尺寸>   开始五子棋 (8-19路)     ║");
        System.out.println("║  new go <尺寸>       开始围棋   (8-19路)     ║");
        System.out.println("║  load <文件名>       读取存档                ║");
        System.out.println("║  quit                退出程序                ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    /** 返回当前游戏实例（GUI 用于直接读取状态） */
    public AbstractGame getCurrentGame() { return game; }

}