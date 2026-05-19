package chess.client;

/**
 * 指令解析器
 * 将用户输入字符串解析为 ParsedCommand 对象，与执行逻辑解耦。
 *
 * 支持格式：
 *   new gomoku 15    new go 9
 *   place D5         place d5
 *   pass
 *   undo
 *   resign
 *   restart
 *   save slot1       save slot1.sav
 *   load slot1
 *   hint on / hint off
 *   quit / exit
 */
public class CommandParser {

    public ParsedCommand parse(String input) {
        if (input == null) return unknown(input);
        String trimmed = input.trim();
        if (trimmed.isEmpty()) return unknown(trimmed);

        String[] tokens = trimmed.split("\\s+");
        String cmd = tokens[0].toLowerCase();

        switch (cmd) {
            case "new":
                return new ParsedCommand(ParsedCommand.Type.NEW_GAME, tokens, trimmed);

            case "place": {
                if (tokens.length < 2)
                    return unknown(trimmed);
                return new ParsedCommand(ParsedCommand.Type.PLACE, tokens, trimmed);
            }

            case "pass":
                return new ParsedCommand(ParsedCommand.Type.PASS, tokens, trimmed);

            case "undo":
                return new ParsedCommand(ParsedCommand.Type.UNDO, tokens, trimmed);

            case "resign":
                return new ParsedCommand(ParsedCommand.Type.RESIGN, tokens, trimmed);

            case "restart":
                return new ParsedCommand(ParsedCommand.Type.RESTART, tokens, trimmed);

            case "save":
                return new ParsedCommand(ParsedCommand.Type.SAVE, tokens, trimmed);

            case "load":
                return new ParsedCommand(ParsedCommand.Type.LOAD, tokens, trimmed);

            case "hint":
                if (tokens.length >= 2 && tokens[1].equalsIgnoreCase("off"))
                    return new ParsedCommand(ParsedCommand.Type.HINT_OFF, tokens, trimmed);
                return new ParsedCommand(ParsedCommand.Type.HINT_ON, tokens, trimmed);

            case "quit": case "exit": case "q":
                return new ParsedCommand(ParsedCommand.Type.QUIT, tokens, trimmed);

            default:
                return unknown(trimmed);
        }
    }

    private ParsedCommand unknown(String raw) {
        return new ParsedCommand(ParsedCommand.Type.UNKNOWN,
                raw == null ? new String[0] : raw.split("\\s+"), raw);
    }
}
