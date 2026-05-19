package chess.command;

import chess.model.Stone;

/**
 * 虚着命令（围棋专用）
 * 不修改棋盘，记录回合和连续虚着次数以支持撤销。
 */
public class PassMoveCommand implements MoveCommand {
    private static final long serialVersionUID = 1L;

    private final Stone color;
    private final int   prevConsecutivePasses;
    private final int   prevBoardHash;
    private final int   prevPrevHash;

    public PassMoveCommand(Stone color, int prevConsecutivePasses,
                           int prevBoardHash, int prevPrevHash) {
        this.color                 = color;
        this.prevConsecutivePasses = prevConsecutivePasses;
        this.prevBoardHash         = prevBoardHash;
        this.prevPrevHash          = prevPrevHash;
    }

    @Override public void execute() {}
    @Override public void undo()    {}

    @Override public String describe() { return color.getSymbol() + " 虚着（pass）"; }

    public Stone getColor()                 { return color; }
    public int   getPrevConsecutivePasses() { return prevConsecutivePasses; }
    public int   getPrevBoardHash()         { return prevBoardHash; }
    public int   getPrevPrevHash()          { return prevPrevHash; }
}
