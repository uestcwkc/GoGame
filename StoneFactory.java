package chess.model;

/**
 * 棋子享元工厂（Flyweight Factory）
 * BLACK / WHITE 枚举值本身即为唯一实例，通过此工厂统一获取，语义更清晰。
 */
public class StoneFactory {
    private StoneFactory() {}
    public static Stone get(Stone color) { return color; }
}
