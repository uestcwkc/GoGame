package chess.command;

import java.io.Serializable;

/**
 * 命令接口（Command），继承 Serializable 以支持存档序列化。
 */
public interface MoveCommand extends Serializable {
    void execute();
    void undo();
    String describe();
}
