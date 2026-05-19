package chess.client;

import chess.facade.GameFacade;

import java.util.Scanner;

/**
 * 控制台客户端
 * 负责：读取用户输入 → CommandParser 解析 → 调用 GameFacade 执行 → 渲染界面。
 * 与后端完全解耦，仅通过 GameFacade 交互。
 */
public class ConsoleClient {

    private final GameFacade    facade  = new GameFacade();
    private final CommandParser parser  = new CommandParser();
    private final Scanner       scanner = new Scanner(System.in);

    public void run() {
        facade.renderUI();   // 显示欢迎界面

        while (true) {
            System.out.print(">>> ");
            String input = scanner.nextLine();
            ParsedCommand cmd = parser.parse(input);
            String feedback = dispatch(cmd);

            if (feedback == null) break;  // quit 信号

            if (!feedback.isEmpty())
                System.out.println(feedback);

            facade.renderUI();
        }

        System.out.println("感谢游玩，再见！");
        scanner.close();
    }

    /**
     * 将解析后的指令分发给 Facade 执行。
     * @return 操作结果描述；null 表示退出
     */
    private String dispatch(ParsedCommand cmd) {
        switch (cmd.type) {

            case NEW_GAME: {
                if (cmd.args.length < 3)
                    return "用法：new <gomoku|go> <棋盘大小>  例：new gomoku 15";
                try {
                    int size = Integer.parseInt(cmd.args[2]);
                    return facade.newGame(cmd.args[1], size);
                } catch (NumberFormatException e) {
                    return "错误：棋盘大小必须是整数。";
                }
            }

            case PLACE: {
                // 支持两种格式：place D5  或  place D 5
                try {
                    char colLabel;
                    int  rowNum;
                    if (cmd.args.length == 2) {
                        // place D5
                        String coord = cmd.args[1].toUpperCase();
                        if (coord.length() < 2)
                            return "用法：place <列><行>  例：place D5 或 place D 5";
                        colLabel = coord.charAt(0);
                        rowNum   = Integer.parseInt(coord.substring(1));
                    } else {
                        // place D 5
                        colLabel = cmd.args[1].toUpperCase().charAt(0);
                        rowNum   = Integer.parseInt(cmd.args[2]);
                    }
                    return facade.placeStone(colLabel, rowNum);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    return "用法：place <列><行>  例：place D5 或 place D 5";
                }
            }

            case PASS:
                return facade.pass();

            case UNDO:
                return facade.undo();

            case RESIGN:
                return facade.resign();

            case RESTART:
                return facade.restart();

            case SAVE: {
                if (cmd.args.length < 2)
                    return "用法：save <文件名>  例：save slot1";
                return facade.saveGame(cmd.args[1]);
            }

            case LOAD: {
                if (cmd.args.length < 2)
                    return "用法：load <文件名>  例：load slot1";
                return facade.loadGame(cmd.args[1]);
            }

            case HINT_ON:
                return facade.toggleHint(true);

            case HINT_OFF:
                return facade.toggleHint(false);

            case QUIT:
                return null;   // 触发退出

            case UNKNOWN:
            default:
                return "未知指令：" + cmd.raw
                        + "。输入 hint on 查看可用指令。";
        }
    }
}
