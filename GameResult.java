package chess.model;

import java.io.Serializable;

/** 游戏结果：携带胜负状态和原因描述。 */
public class GameResult implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum Status { ONGOING, BLACK_WIN, WHITE_WIN, DRAW }

    private final Status status;
    private final String reason;

    public GameResult(Status status, String reason) {
        this.status = status; this.reason = reason;
    }
    public static GameResult ongoing() { return new GameResult(Status.ONGOING, ""); }

    public boolean isOver()    { return status != Status.ONGOING; }
    public Status  getStatus() { return status; }
    public String  getReason() { return reason; }

    @Override public String toString() {
        switch (status) {
            case BLACK_WIN: return "黑棋获胜！原因：" + reason;
            case WHITE_WIN: return "白棋获胜！原因：" + reason;
            case DRAW:      return "平局！原因：" + reason;
            default:        return "对局进行中";
        }
    }
}
