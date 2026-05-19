package chess.client;

/**
 * 解析后的指令对象（值对象）
 * 携带指令类型和参数列表。
 */
public class ParsedCommand {

    public enum Type {
        NEW_GAME,       // new <type> <size>
        PLACE,          // place <col><row>
        PASS,           // pass
        UNDO,           // undo
        RESIGN,         // resign
        RESTART,        // restart
        SAVE,           // save <name>
        LOAD,           // load <name>
        HINT_ON,        // hint on
        HINT_OFF,       // hint off
        QUIT,           // quit
        UNKNOWN         // 无法识别
    }

    public final Type   type;
    public final String[] args;  // 原始参数段
    public final String raw;     // 原始输入行

    public ParsedCommand(Type type, String[] args, String raw) {
        this.type = type;
        this.args = args;
        this.raw  = raw;
    }
}
