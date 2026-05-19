# GoGame
---

# 棋类对战平台 — Gomoku & Go

> 面向对象程序设计课程大作业
> 
> 基于 Java + Swing 实现的五子棋与围棋双模式对战平台，支持 GUI 鼠标交互界面和命令行两种运行模式。

---

## 功能特性

### 棋类支持
- **五子棋（Gomoku）**：标准五子连珠获胜规则，棋盘满则判平局
- **围棋（Go）**：支持提子、禁着点检测、打劫检测、虚着，双方均虚着时触发数目法终局（黑方贴目 6.5）

### 游戏功能
- 双人本地对战，黑白方交替落子
- 悔棋（撤销上一步，围棋悔棋时完整还原被提子）
- 投子认负
- 局面保存与读取（序列化到本地 `.sav` 文件）
- 棋盘大小自定义（8×8 ~ 19×19）
- 完善的非法输入拦截与错误提示

### 界面
- **GUI 模式**（默认）：Swing 实现，深色主题木色棋盘，鼠标点击落子，悬停预览落点，红圈标记最后落子，文件选择器存读档
- **命令行模式**：字符画棋盘，键盘指令交互，可选显示/隐藏操作提示

---

## 快速开始

### 环境要求

- Java 17 或以上（GUI 模式需要显示环境）

### 编译

**Linux / macOS：**
```bash
git clone <仓库地址>
cd <项目目录>
mkdir -p out
javac -encoding UTF-8 -d out -sourcepath src $(find src -name "*.java")
```

**Windows（PowerShell）：**
```powershell
git clone <仓库地址>
cd <项目目录>
New-Item -ItemType Directory -Force -Path out
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse -Filter "*.java" src).FullName
```

### 运行

```bash
# GUI 模式（默认，推荐）
java -cp out chess.Main

# 命令行模式
java -cp out chess.Main --console
```

---

## 使用说明

### GUI 模式

1. 启动后在欢迎页选择游戏类型（五子棋 / 围棋）和棋盘大小
2. 点击"开始游戏"进入对局
3. **鼠标点击棋盘交叉点落子**，悬停可预览落点
4. 右侧面板提供：虚着、悔棋、认负、重新开始、保存局面、读取存档、返回主页

### 命令行模式

| 指令 | 说明 |
|---|---|
| `new gomoku <尺寸>` | 开始五子棋，如 `new gomoku 15` |
| `new go <尺寸>` | 开始围棋，如 `new go 9` |
| `place <列><行>` | 落子，如 `place D5` 或 `place D 5` |
| `pass` | 虚着（围棋专用） |
| `undo` | 悔棋 |
| `resign` | 投子认负 |
| `restart` | 重新开始（保持棋类和棋盘大小） |
| `save <名称>` | 保存局面，如 `save slot1` |
| `load <名称>` | 读取存档，如 `load slot1` |
| `hint off / on` | 隐藏 / 显示操作提示 |
| `quit` | 退出程序 |

> **坐标格式说明**：列用字母 A–T（跳过 I 避免与数字 1 混淆），行用数字 1–19。例如 `place D5` 表示第 4 列第 5 行。

---

## 项目结构

```
src/
└── chess/
    ├── Main.java                      # 程序入口（--console 参数切换命令行模式）
    ├── model/                         # 数据层
    │   ├── Stone.java                 # 棋子颜色枚举（享元）
    │   ├── StoneFactory.java          # 享元工厂
    │   ├── Position.java              # 棋盘坐标值对象
    │   ├── Board.java                 # 棋盘数据模型
    │   └── GameResult.java            # 游戏结果
    ├── game/                          # 游戏逻辑层（模板方法 + 策略）
    │   ├── AbstractGame.java          # 抽象游戏（模板方法）
    │   ├── GomokuGame.java            # 五子棋
    │   ├── GoGame.java                # 围棋（含提子逻辑）
    │   ├── MoveValidator.java         # 落子合法性策略接口
    │   ├── GomokuValidator.java       # 五子棋校验
    │   ├── GoValidator.java           # 围棋校验（禁着 + 打劫）
    │   ├── WinJudge.java              # 胜负判断策略接口
    │   ├── GomokuWinJudge.java        # 五子棋胜负
    │   ├── GoWinJudge.java            # 围棋胜负（数目法）
    │   └── ValidationResult.java      # 校验结果
    ├── command/                       # 命令层（命令模式 — 悔棋）
    │   ├── MoveCommand.java           # 命令接口
    │   ├── PlaceMoveCommand.java      # 落子命令（含提子还原）
    │   ├── PassMoveCommand.java       # 虚着命令
    │   └── CommandHistory.java        # 命令历史栈
    ├── archive/                       # 存档层（备忘录模式）
    │   ├── GameMemento.java           # 游戏状态快照
    │   └── GameArchiveManager.java    # 存档文件读写
    ├── ui/                            # 命令行界面层（建造者模式）
    │   ├── UIComponent.java           # 界面组件接口
    │   ├── BoardAreaComponent.java    # 字符画棋盘
    │   ├── StatusBarComponent.java    # 状态栏
    │   ├── HintAreaComponent.java     # 操作提示区
    │   ├── ConsoleUI.java             # 界面产品
    │   ├── UIBuilder.java             # 抽象建造者
    │   ├── GameUIBuilder.java         # 具体建造者
    │   └── UIDirector.java            # 指挥者
    ├── gui/                           # GUI 界面层（Swing）
    │   ├── GuiClient.java             # GUI 启动入口
    │   ├── Theme.java                 # 全局配色与字体
    │   ├── StyledButton.java          # 自定义圆角按钮
    │   ├── MainFrame.java             # 主窗口（CardLayout 切换）
    │   ├── WelcomePanel.java          # 欢迎/开始页
    │   ├── GamePanel.java             # 对局协调面板
    │   ├── BoardPanel.java            # 棋盘绘制与鼠标交互
    │   └── SidePanel.java             # 侧边信息与操作面板
    ├── facade/
    │   └── GameFacade.java            # 外观层（整合所有后端模块）
    └── client/
        ├── CommandParser.java         # 命令行指令解析器
        ├── ParsedCommand.java         # 解析结果值对象
        └── ConsoleClient.java         # 命令行客户端主循环
```

---

## 设计模式

| 模式 | 应用位置 |
|---|---|
| 模板方法 | `AbstractGame` → `GomokuGame` / `GoGame` |
| 策略 | `MoveValidator`（两种校验器）、`WinJudge`（两种判断器） |
| 命令 | `PlaceMoveCommand` / `PassMoveCommand` + `CommandHistory` |
| 备忘录 | `GameMemento` + `GameArchiveManager` |
| 建造者 | `UIBuilder` / `GameUIBuilder` / `UIDirector` |
| 享元 | `Stone`（枚举）+ `StoneFactory` |
| 外观 | `GameFacade` |

---

## 存档文件

存档默认保存在运行目录下的 `saves/` 文件夹（首次保存时自动创建）：

```
saves/
├── slot1.sav
├── slot2.sav
└── ...
```

可通过 `save <名称>` / `load <名称>` 指令（命令行）或文件选择对话框（GUI）操作。存档文件通过 Java 序列化保存完整游戏状态，包括棋盘、当前回合、提子数和命令历史（可在读档后继续悔棋）。

---

## 已知限制

- 围棋终局判断采用简单数目法（空地归属），极端复杂棋形下地盘归属可能与人工判断有差异
- 悔棋次数不设上限
- 不支持人机对战（AI）
- GUI 模式需要本地显示环境，无头服务器请使用 `--console` 模式

---

## 演示视频

[清华云盘链接]（待上传）

---

## License

本项目为课程作业，仅供学习参考。
```
