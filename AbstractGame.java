package chess.game;

import chess.model.*;

import java.io.Serializable;
import java.util.List;

/**
 * 抽象游戏（模板方法模式）
 * 定义游戏流程骨架：开始→落子/虚着→合法性校验→执行→胜负判断→结束。
 * 五子棋和围棋各自实现钩子方法。
 */
public abstract class AbstractGame implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum GameType { GOMOKU, GO }

    protected Board      board;
    protected Stone      currentTurn;
    protected GameResult result;
    protected int        boardSize;
    protected GameType   gameType;

    protected int prevBoardHash = 0;
    protected int prevPrevHash  = 0;

    protected transient MoveValidator validator;
    protected transient WinJudge      winJudge;

    protected int     blackCaptures      = 0;
    protected int     whiteCaptures      = 0;
    protected int     consecutivePasses  = 0;
    protected boolean gameOver           = false;

    public AbstractGame(int boardSize, GameType type) {
        this.boardSize   = boardSize;
        this.gameType    = type;
        this.board       = new Board(boardSize);
        this.currentTurn = Stone.BLACK;
        this.result      = GameResult.ongoing();
        this.validator   = createValidator();
        this.winJudge    = createWinJudge();
    }

    protected abstract MoveValidator createValidator();
    protected abstract WinJudge      createWinJudge();
    protected abstract int[]         getExtraData();
    protected abstract List<Position> afterPlace(Position pos, Stone color);

    // ── 公开游戏操作 ──────────────────────────────────────

    public ValidationResult placeStone(Position pos) {
        if (gameOver) return ValidationResult.fail("游戏已结束，请开始新游戏。");

        ValidationResult vr = validator.validate(board, pos, currentTurn, prevPrevHash);
        if (!vr.isValid()) return vr;

        prevPrevHash  = prevBoardHash;
        prevBoardHash = board.boardHash();

        board.set(pos, StoneFactory.get(currentTurn));
        afterPlace(pos, currentTurn);
        consecutivePasses = 0;

        checkResult(pos);
        if (!gameOver) switchTurn();
        return ValidationResult.ok();
    }

    public ValidationResult pass() {
        if (gameOver) return ValidationResult.fail("游戏已结束。");
        if (gameType != GameType.GO) return ValidationResult.fail("五子棋不支持虚着。");

        prevPrevHash  = prevBoardHash;
        prevBoardHash = board.boardHash();
        consecutivePasses++;

        checkResult(null);
        if (!gameOver) switchTurn();
        return ValidationResult.ok();
    }

    public GameResult resign() {
        if (gameOver) return result;
        Stone winner = currentTurn.opposite();
        GameResult.Status s = (winner == Stone.BLACK)
                ? GameResult.Status.BLACK_WIN : GameResult.Status.WHITE_WIN;
        result   = new GameResult(s, currentTurn.getSymbol() + " 投子认负");
        gameOver = true;
        return result;
    }

    protected void checkResult(Position lastMove) {
        result = winJudge.judge(board, lastMove, currentTurn, getExtraData());
        if (result.isOver()) gameOver = true;
    }

    protected void switchTurn() { currentTurn = currentTurn.opposite(); }

    public void rebuildTransients() {
        this.validator = createValidator();
        this.winJudge  = createWinJudge();
    }

    // ── Getters / Setters（供 Facade 访问） ───────────────

    public Board      getBoard()              { return board; }
    public Stone      getCurrentTurn()        { return currentTurn; }
    public GameResult getResult()             { return result; }
    public boolean    isGameOver()            { return gameOver; }
    public GameType   getGameType()           { return gameType; }
    public int        getBoardSize()          { return boardSize; }
    public int        getBlackCaptures()      { return blackCaptures; }
    public int        getWhiteCaptures()      { return whiteCaptures; }
    public int        getPrevBoardHash()      { return prevBoardHash; }
    public int        getPrevPrevHash()       { return prevPrevHash; }
    public int        getConsecutivePasses()  { return consecutivePasses; }

    // Facade 悔棋时需要直接写回这些状态
    public void setCurrentTurn(Stone t)          { this.currentTurn = t; }
    public void setBlackCaptures(int v)          { this.blackCaptures = v; }
    public void setWhiteCaptures(int v)          { this.whiteCaptures = v; }
    public void setPrevBoardHash(int v)          { this.prevBoardHash = v; }
    public void setPrevPrevHash(int v)           { this.prevPrevHash = v; }
    public void setConsecutivePasses(int v)      { this.consecutivePasses = v; }
    public void setResult(GameResult r)          { this.result = r; }
    public void setGameOver(boolean v)           { this.gameOver = v; }
}
