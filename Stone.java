package chess.model;

/**
 * 棋子颜色枚举（享元模式基础）
 * 棋子只有颜色属性，通过枚举实现全局单一实例，StoneFactory 统一获取。
 */
public enum Stone {
    BLACK("●"),
    WHITE("○"),
    EMPTY("·");

    private final String symbol;
    Stone(String symbol) { this.symbol = symbol; }
    public String getSymbol() { return symbol; }

    public Stone opposite() {
        if (this == BLACK) return WHITE;
        if (this == WHITE) return BLACK;
        return EMPTY;
    }
}
