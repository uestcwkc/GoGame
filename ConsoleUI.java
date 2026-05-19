package chess.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * 控制台界面产品（Product）
 * 由若干 UIComponent 组合而成，通过 UIDirector + UIBuilder 构建。
 */
public class ConsoleUI {

    private final List<UIComponent> components = new ArrayList<>();

    public void addComponent(UIComponent component) {
        components.add(component);
    }

    /** 渲染完整界面：依次输出所有已装配的组件 */
    public void render() {
        for (UIComponent c : components) {
            c.render();
        }
    }

    /** 仅重新渲染棋盘区 */
    public void renderBoardOnly() {
        for (UIComponent c : components) {
            if (c instanceof BoardAreaComponent) c.render();
        }
    }
}
