package chess;

import chess.client.ConsoleClient;

/**
 * 程序入口
 * 创建控制台客户端并启动主循环。
 */
public class Main {
    public static void main(String[] args) {
        new ConsoleClient().run();
    }
}
