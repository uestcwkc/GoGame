package chess.archive;

import chess.command.CommandHistory;
import chess.game.AbstractGame;

import java.io.Serializable;

/**
 * 游戏存档备忘录（Memento）
 * 存储整个 AbstractGame 的深拷贝快照，实现 Serializable 支持硬盘存储。
 * 采用直接序列化游戏对象的方式，简洁且完整。
 */
public class GameMemento implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AbstractGame gameSnapshot;
    private final CommandHistory historySnapshot;
    private final String description;

    public GameMemento(AbstractGame game, CommandHistory history, String description) {
        // 深拷贝由序列化/反序列化保证，此处直接持有引用（存档时才序列化）
        this.gameSnapshot    = game;
        this.historySnapshot = history;
        this.description     = description;
    }

    public AbstractGame    getGame()        { return gameSnapshot; }
    public CommandHistory  getHistory()     { return historySnapshot; }
    public String          getDescription() { return description; }
}
