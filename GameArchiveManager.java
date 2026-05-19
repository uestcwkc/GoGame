package chess.archive;

import java.io.*;
import java.nio.file.*;

/**
 * 存档管理器（Caretaker）
 * 负责将 GameMemento 序列化到硬盘文件，以及从文件反序列化。
 */
public class GameArchiveManager {

    private GameArchiveManager() {}

    /**
     * 保存存档到指定路径
     * @param memento   要保存的备忘录
     * @param filePath  目标文件路径（如 "saves/slot1.sav"）
     * @throws IOException 写入失败
     */
    public static void save(GameMemento memento, String filePath) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {
            oos.writeObject(memento);
        }
    }

    /**
     * 从指定路径读取存档
     * @param filePath 存档文件路径
     * @return 反序列化的 GameMemento
     * @throws IOException            文件不存在或读取失败
     * @throws ClassNotFoundException 存档版本不兼容
     */
    public static GameMemento load(String filePath)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))) {
            GameMemento memento = (GameMemento) ois.readObject();
            // 反序列化后重建 transient 字段
            memento.getGame().rebuildTransients();
            return memento;
        }
    }

    /** 判断存档文件是否存在 */
    public static boolean exists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
}
