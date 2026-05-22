# Project Guide

## 项目概览

- **项目名：** test
- **包名：** `com.example.test`
- **语言：** Kotlin
- **UI 框架：** Jetpack Compose + Material 3 + Navigation Compose
- **Min SDK：** 24 | **Target SDK：** 36

## 文件结构

```
AndroidProjects/
├── CLAUDE.md
├── ProjectGuide.md
├── ProjectStudyNotes.md
├── build.gradle.kts
├── settings.gradle.kts
└── app/src/main/
    ├── AndroidManifest.xml
    ├── java/com/example/test/
    │   ├── TestAPP.kt            # Activity + 创建两个共享 VM
    │   ├── CustomButtonAppBar.kt # BottomBar，接收 currentRoute + onNavigate
    │   ├── CustomCard2.kt        # 带 studiers badge 的卡片
    │   ├── CustomEasyCard.kt     # 简化版卡片
    │   ├── data/
    │   │   ├── FlashcardSet.kt           # data class（5 字段）
    │   │   └── FlashcardSetViewModel.kt  # mutableStateListOf + addSet()
    │   ├── navigation/
    │   │   └── AppNav.kt         # Screen 路由枚举 + AppNavHost
    │   ├── screens/
    │   │   ├── home/HomeScreen.kt        # 读两个 VM（昵称 + sets）
    │   │   ├── create/CreateScreen.kt    # ADD 表单 → addSet()
    │   │   ├── library/LibraryScreen.kt  # 显示 sets
    │   │   ├── premium/PremiumScreen.kt
    │   │   └── profile/
    │   │       ├── ProfileScreen.kt
    │   │       ├── UserProfile.kt          # data class
    │   │       └── UserProfileViewModel.kt # 共享 VM
    │   └── ui/theme/
    │       ├── Color.kt
    │       ├── Shape.kt
    │       ├── Theme.kt
    │       └── Type.kt
    └── res/
        └── drawable/              # XML 图标资源
```

## 入口

- **Launcher Activity：** `TestAPP`
- `enableEdgeToEdge()` + `Scaffold` + `statusBarsPadding()`
- Activity 内创建 `navController` + 两个共享 VM（`UserProfileViewModel` 与 `FlashcardSetViewModel`，均通过 `viewModel()` 在 Activity 作用域创建），传给 `AppNavHost`

## 导航（Lab 4）

使用**两层嵌套 NavHost**：

```
AppNavHost (root)
├── "main"    → MainScaffold（Scaffold：TopBar + BottomBar + TabNavHost）
│               └── TabNavHost
│                   ├── "home"     → HomeScreen
│                   ├── "create"   → CreateScreen
│                   ├── "library"  → LibraryScreen
│                   └── "premium"  → PremiumScreen
└── "profile" → ProfileScreen（独立全屏，自带 TopAppBar + 返回箭头）
```

| Enum | 路由 | 触发方式 |
|---|---|---|
| `RootScreen.Main` | `main` | startDestination |
| `RootScreen.Profile` | `profile` | TopBar 右上角头像 |
| `TabScreen.Home/Create/Library/Premium` | `home/create/library/premium` | BottomBar |

- 路由定义：`navigation/AppNav.kt`（`RootScreen` + `TabScreen` 两个 enum）
- Tab 切换：`popUpTo(Home, saveState=true) + launchSingleTop + restoreState`，避免返回栈堆积并恢复各 tab 滚动位置。
- Profile 跳转：根层 `navigate("profile", launchSingleTop=true)`；返回靠自带箭头调用 `popBackStack()` 或系统返回键。

## 切换动画

| 层级 | 动作 | 动画 |
|---|---|---|
| Tab 层 | Home ↔ Create ↔ Library ↔ Premium | 按 `tabOrder` 索引判断方向，新页从左/右滑入，旧页向相反方向滑出 |
| Root 层（push）| Main → Profile | Profile 从右滑入 + fadeIn；Main 轻微 fadeOut |
| Root 层（pop）| Profile → Main | Profile 向右滑出 + fadeOut；Main fadeIn |

动画时长常量集中在 `AppNav.kt` 顶部（`TAB_SLIDE_MS / TAB_FADE_MS / ROOT_SLIDE_MS / ROOT_FADE_MS`）。

## ViewModel（Lab 4 / Project 1）

### UserProfileViewModel（个人资料）

- **Data class：** `UserProfile(nickname, bio, email)`（3 字段）
- **ViewModel：** `UserProfileViewModel` 用 `mutableStateOf(UserProfile(...))` 持有单实例，提供 `updateNickname/updateBio/updateEmail`。
- **作用域：** 在 `setContent` 里用 `viewModel()` 创建，作用域为 Activity → 所有 screen 共享同一实例 → 旋转屏幕后数据保留。
- **使用：** `ProfileScreen` 编辑；`HomeScreen` 顶部显示 `"Hello, $nickname"`（跨屏幕读取）。

### FlashcardSetViewModel（学习集合，Project 1 新增）

- **Data class：** `FlashcardSet(id, title, description, author, cardCount)`（5 字段）
- **ViewModel：** `FlashcardSetViewModel` 用 `mutableStateListOf<FlashcardSet>` 持有列表，提供 `addSet(title, description, author, cardCount)`。
- **ADD 流程：** `CreateScreen` 收集表单 → `addSet()` 写入 list → Compose 自动 recomposition → `LibraryScreen` 与 `HomeScreen` 同步显示新 item。
- **作用域：** Activity 范围共享，跨屏幕同一实例。

## 全局变量

| 变量 | 文件 | 值 | 说明 |
|---|---|---|---|
| `unionVerticalPaddingValue` | TestAPP.kt 顶层 | 20.dp | 页面左右统一内边距 |

## 组件一览

### TestAPP.kt

| 组件 | 说明 |
|---|---|
| `CustomTopBar` | TopAppBar，SearchBar + 头像 `IconButton`（clickable → profile） |
| `CusTitle` | 区块标题（20sp, Bold） |

### CustomButtonAppBar.kt

| 组件 | 说明 |
|---|---|
| `CustomBottomBar` | 接收 `currentRoute` 和 `onNavigate(route)`，按路由高亮当前项 |
| `BottomBarItemView` | 单个底栏按钮（选中时染 primary 色） |
| `BottomBarItem` | data class：`icon, contentDescription, label, route` |

### Screen 文件（screens/<name>/）

每个 screen 是一个独立 Composable，内部使用 `MaterialTheme.shapes / typography / colorScheme` 保持 Lab 3 主题一致。

## 主题资源

| 形状 Token | 半径 | 常用处 |
|---|---|---|
| `shapes.small` | 8.dp | 小图标底板 |
| `shapes.medium` | 16.dp | 图标容器、文本框 |
| `shapes.large` | 23.dp | 普通卡片 |
| `shapes.extraLarge` | 28.dp | 大 banner 卡片 |

## 图标使用方式

| 类型 | 用法 |
|---|---|
| 内置图标 | `imageVector = Icons.Default.xxx` |
| 外部 XML 图标 | `painter = painterResource(R.drawable.xxx)` |

## Lab 4 合规清单

- [x] ≥3 个屏幕（实际 5 个：Home / Create / Library / Premium / Profile）
- [x] `NavController` + `NavHost` 明确路由
- [x] Data class ≥2 字段（`UserProfile` 3 字段）
- [x] ViewModel 持有 data class 实例，跨屏幕共享
- [x] 保持 Lab 3 的自定义 Material Theme

## Project 1 改造记录（SDG 4 – Quality Education）

### 已完成的代码改造

| 修改 | 文件 | 作用 |
|---|---|---|
| 新增 `FlashcardSet` 数据类 | `data/FlashcardSet.kt` | 5 字段：id / title / description / author / cardCount |
| 新增共享 ViewModel | `data/FlashcardSetViewModel.kt` | `mutableStateListOf` + `addSet()` 方法，Activity 作用域共享 |
| Create 屏幕改成真正的表单 | `screens/create/CreateScreen.kt` | 3 个 OutlinedTextField（title/description/cards）+ "Save & view in Library" 按钮 → 调用 `addSet` → 自动跳转 Library tab |
| Library 屏幕接 ViewModel | `screens/library/LibraryScreen.kt` | `flashcardSetViewModel.sets` 实时反映新增 item，包括 set 数计数 |
| Home 屏幕展示昵称 + 集合 | `screens/home/HomeScreen.kt` | 顶部 "Hello, $nickname" 读 `UserProfileViewModel`，"Jump back in" 和 "Recents" 读 `FlashcardSetViewModel` |
| 导航接线 | `navigation/AppNav.kt` | 注入两个 VM、加 `onSetCreated` 回调切换到 Library |
| 入口注册新 VM | `TestAPP.kt` | `viewModel()` 创建 `FlashcardSetViewModel` 并下传 |

### ADD → DISPLAY 数据流（VSR 视频要演示这个）

```
CreateScreen 表单输入 title + description + 卡片数
   ↓ 点击 "Save & view in Library"
FlashcardSetViewModel.addSet()  ←——— mutableStateListOf 触发 recomposition
   ↓                                ↘
LibraryScreen 顶端立刻多一行         HomeScreen "Recents"/"Jump back in" 同步更新
```

这条数据流同时跨 **3 个屏幕** 显示同一份 ViewModel 数据，对 Live Q&A 很加分。

### 还需要做的事

1. **项目改名**（提交规范要求 `MatricNo_Name_Instructor_Project1`）：
   - 把外层文件夹 `AndroidProjects` 改成 `MatricNo_Name_Instructor_Project1`
   - `settings.gradle.kts` 第 25 行 `rootProject.name = "test"` 改成同样的字符串
2. **写 Problem Statement**（1 分）— 可以基于这段：
   > **SDG 4 – Quality Education.** Many students struggle to keep their study notes in one place and revisit them efficiently. This app lets users create flashcard sets, store them in their library, and review them across screens — supporting self-directed learning and equal access to learning tools.
3. **录 VSR 视频**（≤2 分钟）：
   - 念一下 SDG 4 + 问题 + 解决方案
   - 导航演示 5 个屏幕（Home / Create / Library / Premium / Profile）
   - **重点演示**：Create 输入 → 保存 → Library 出现 → 回 Home 看到 Recents 也变了
   - 屏幕上要看到项目名（标题栏 / Android Studio 顶部都行）
   - 讲一下 `AppNav.kt` 里的路由 enum 和 `FlashcardSetViewModel.addSet()`
