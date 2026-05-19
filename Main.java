package chess;

import chess.client.ConsoleClient;
import chess.gui.GuiClient;

/**
 * 程序入口
 * 默认启动 GUI 模式；传入 --console 参数则启动命令行模式。
 */
public class Main {
    public static void main(String[] args) {
        boolean consoleMode = args.length > 0 && args[0].equalsIgnoreCase("--console");
        if (consoleMode) {
            new ConsoleClient().run();
        } else {
            GuiClient.launch();
        }
    }
}
