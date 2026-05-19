package chess.command;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 命令历史栈（Caretaker）
 * 维护已执行的命令列表，支持 undo 弹出最近一条命令。
 * 实现 Serializable 支持存档序列化。
 */
public class CommandHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Deque<MoveCommand> history = new ArrayDeque<>();

    public void push(MoveCommand cmd) { history.push(cmd); }

    /**
     * 弹出并返回最近一条命令，如果为空返回 null。
     */
    public MoveCommand pop() {
        return history.isEmpty() ? null : history.pop();
    }

    public boolean isEmpty()   { return history.isEmpty(); }
    public int     size()      { return history.size(); }
    public void    clear()     { history.clear(); }

    /** 查看最近一条命令（不弹出） */
    public MoveCommand peek()  { return history.isEmpty() ? null : history.peek(); }
}
