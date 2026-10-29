# 修的 Bug / Error（原本结构没变，只改这些）

已用 `./gradlew :app:assembleDebug` 完整编译 + 打包 APK 通过。

---

## 第四批（提醒稳定性 / 开机 / Supabase 同步 / 载入速度）

### 1. 提醒被误关的隐患 —— 已修
之前 `ReminderReceiver` 发完通知后会「读 Room 的通知设定，重排全部 4 个提醒」。
如果那次读取失败 → 拿到全关的预设 → 把 4 个提醒全取消。
**改法**：改成把「要排哪个提醒、几点」直接放进 alarm 的 intent extra；receiver 到点后
**只重排自己这一个**（`ReminderScheduler.rearm(...)`），不碰其它 3 个，也不需要读 Room。

### 2. 重开机后提醒失效 —— 已修
`AlarmManager` 的闹钟开机就清空。
**改法**：新增 `notification/BootReceiver.kt`（Manifest 注册 `BOOT_COMPLETED` +
`RECEIVE_BOOT_COMPLETED` 权限）。开机后遍历 Room 里所有帐号，逐个把它们的提醒重新排。

### 3. Supabase 多设备撞车 / 数据不同步 —— 已修
之前 Room 的 user id 是自增的，每台设备第一个用户都是 id 1 → Supabase 里
`user_id=1` 的资料互相覆盖。
**改法**：
- `UserEntity.id` 不再自增，改成**由 email 算出来的固定值**（`UserData.stableId(email)`）。
  同一个帐号在任何设备上 id 都一样 → Supabase 资料对得上。
- 注册时如果 Supabase 已经有这个 email（别的设备注册过）→ 挡下来，叫你去登入。
- 饮食 / 运动记录的 id 从「本地 max+1」改成**时间戳**（跟饮水 / 睡眠一样），
  这样不同设备加的记录不会因为 id 相同而在 Supabase 互相覆盖。
- DB version 2 → 3（schema 变了）。第一次跑会重建本地库，要**重新注册**（数据在 Supabase 里）。

### 6. 远端删除现在会同步到本地 —— 已修
之前 `loadForUser` 只做「远端有的塞进 Room」，不删本地。
**改法**：`RemoteHealthData.fetch*` 现在回传可空 —— `null` = 连不上服务器。
`loadForUser` 里：连得上 → 把 Room 那张表**清空后用远端的完整覆盖**（删除会同步过来）；
连不上 → 本地资料原封不动（离线优先）。

### 4. 登入后追踪页空白太久 —— 已修
`loadForUser` 原本 6 个 Supabase 请求一个接一个做。
**改法**：6 个请求**并行**（`async` + `await`）；Supabase client 加 8 秒 `requestTimeout`。
网路不好时最多等 ~8 秒而不是 ~50 秒。

### 7. 首页「Trackers」底部按钮 —— 已修
改成也跳 `lastTracker`（跟其它地方一致：回到你上次在的那个 tracker）。

### 5. （没改，你说不用）
「add successfully」提示弹出的 2.5 秒会挡住底部一小条的点击 —— 轻微，跳过。

---

## 第二批修的（Success 提示 / Insight 返回 / 通知）

### A. 加了 workout 之后的「add successful」提示不消失、挡住底部导航

原本这个提示（`SuccessPillSnackbar`）只有 exercise 有，而且 `AppNavGraph` 里
`onDismissSnackbar` 没传进去 → 提示永远不会消失，换页回来还在，挡住底部 icon。
另外 exercise tracker 页**根本没有底部导航栏**（其它三个 tracker 都有）。

**改法**：
- 把 success 提示改成 **App 级共用**：`AppNavGraph` 里一个 `successMessage` 状态 +
  底部一个 `SuccessPillSnackbar` overlay，2.5 秒自动消失。
- **水 / 食物 / 睡眠 / 运动** 记完都会弹这个提示（"Water logged successfully" 等）。
- `ExerciseTrackerMainScreen` 加上 `AppBottomNavigation` 底部栏，跟其它 tracker 一致。
- 移除 `ExerciseTrackerMainScreen` 里旧的 `showSnackbar` / `onDismissSnackbar` 参数和内嵌提示。

### B. 从 tracker 去 Insight 再回来，永远回到 Exercise 那页

原本 `InsightScreen` 的「Trackers」按钮写死 `navigate("main_tracker")`。

**改法**：`AppNavGraph` 加一个 `lastTracker` 状态。你在哪个 tracker 点去 Insight，
就记住哪个（water / meal / exercise / sleep）；从 Insight 点「Trackers」回来时
`navigate(lastTracker)`。现在：water→Insight→回来 = water，exercise→Insight→回来 = exercise。

### C. 设了 reminder 但通知不出现

**根本原因**：`ReminderReceiver`（到点发通知的那个类）**没有在 `AndroidManifest.xml` 里注册**。
AlarmManager 到点发广播，但没有 receiver 接 → 什么都不发生。

**改法**：
- `AndroidManifest.xml`：加 `<receiver android:name=".notification.ReminderReceiver" .../>`。
  只要 `POST_NOTIFICATIONS`，**不需要**精确闹钟权限。
- `ReminderScheduler`：用 `alarmManager.setAndAllowWhileIdle(...)`（普通闹钟）。
  一次性的，所以 `ReminderReceiver` 发完通知会**重新排明天的**（re-arm）。
- 通知频道 `IMPORTANCE_HIGH` + 默认音效：到点会**从顶部弹出来 + 有提示音**（跟测试按钮一样）。
- 每次改频道设定（音效/重要性）都换新的 channel id（现在是 `health_reminders_v4`），
  因为一个 channel 建出来之后代码就改不了它，只能建新的。

> 「到点」是**大概**那个时间（普通闹钟系统会稍微批处理，模拟器亮屏时通常 1 分钟内）。
> 没做精确到秒的闹钟（不需要那种，也免去精确闹钟权限）。

**重点：改了 `AndroidManifest.xml` / channel 一定要卸载 + 重装**，否则不生效：
模拟器里先卸载 App → Android Studio **Run**（或拖 `HealthCoach-debug.apk` 进去）。

**Notifications 页新增「Send a test notification now」按钮**：按一下立刻弹一条通知（不经过闹钟），
用来确认权限，也方便 demo。

**怎么测**：
1. 首页 ☰ → Notifications（进去会问通知权限 → 允许）
2. 按「Send a test notification now」→ 顶部弹一条 + 底部 Toast（没有 = 通知权限没开，会自动跳设置页）
3. 打开某个 reminder 开关，时间设成**现在 +1~2 分钟**，保存
4. 回首页，模拟器保持亮屏 → 到那个时间顶部弹出提醒
5. 卡住的话：Logcat 搜 `Reminder`，看有没有 `scheduled ...` / `alarm fired ...` / `notification posted ...`

---

## 0. 每次开 App 都先显示 Login 页（按你要求改的行为）

原本 `SessionManager` 会把 user id 存 SharedPreferences，`UserData.initialize()` 开机时读回来
自动登入 → 直接进 home。

**改法**（`data/UserData.kt` 的 `initialize`）：不再还原 session，开机时
`currentUser = null` + `sessionManager.clearSession()`。所以现在 `AppNavGraph` 的
`startDest` 永远算成 `"login"`，一 run 就是登入页，登入后才进 home。
（`loginUser` 里的 `saveSession` 还留着，之后想恢复「记住登入」只要把 `initialize` 改回去。）

---

## 1. 点「Sleep」tab 会闪退（崩溃）—— 已修

**问题**：`WaterTrackerScreen` / `TrackerScreen`(meal) / `ExerciseTrackerMainScreen` 里的
「Sleep」tab 会 `navController.navigate("sleep_tracker")`，但 `AppNavGraph.kt` **根本没有
`composable("sleep_tracker")` 这个路由**。Navigation Compose 找不到目标 →
`IllegalArgumentException` → App 崩溃。

顺带：`SleepTrackerScreen`（睡眠列表画面）整个写好了却**从来没被接进导航图**，
`sleep_detail/{sleepId}` 路由也因此永远进不去。

**修法**（`nav/AppNavGraph.kt`）：
- `import com.example.assignment.sleeptrackerscreen.SleepTrackerScreen`
- 在 `composable("add_sleep")` 前面加一个 `composable("sleep_tracker") { ... }`，
  写法完全照 `composable("water_tracker")`：传入 `UserHealthData.sleepForDate(...)`、
  goal、上一天 / 下一天、FAB → `add_sleep`、点某笔 → `sleep_detail/{id}`、
  四个 tab 互跳、Home、Insight。

现在四个 tracker 画面（Water / Meal / Exercise / Sleep）的 tab 可以互相切换了。

---

## 2. 从首页「Meal」进去记完餐后卡在结果页 —— 已修

**问题**：`detectResult` 存完餐之后是
`navController.popBackStack("tracker", inclusive = false)`。
如果使用者是从**首页** → Meal → LogMeal → DetectResult 进来的（没经过 `tracker` 路由），
返回堆叠里没有 `"tracker"`，`popBackStack` 变成 no-op，人就停在「Meal Result」页动不了。

**修法**（`nav/AppNavGraph.kt`，`detectResult` 的 `onSave`）：
改成
```kotlin
navController.navigate("tracker") {
    popUpTo("logMeal") { inclusive = true }
    launchSingleTop = true
}
```
不管从首页还是从 meal tracker 进来，存完都会落在餐点列表页，看得到刚记的那一餐。

---

## 3. 提醒通知显示错误的数字（App 被系统杀掉后）—— 已修

**问题**：`ReminderReceiver`（每天定时提醒）直接读 `UserHealthData` 的**内存**数据。
`UserHealthData` 是进程级单例，App 进程被系统回收后再触发闹钟，会在**全新的空进程**里跑，
内存是空的、`appContext` 是 null → 提醒会显示「You've had 0 / 2000 ml today」
之类的错误内容（其实今天已经喝过了）。

**修法**（`notification/ReminderReceiver.kt`）：
- `onReceive` 里先 `goAsync()`，开一个协程：
  `UserHealthData.initialize(context)` → `UserHealthData.loadFromRoom(userId)`
  （只从 Room 读，不碰网路）→ 再建通知 → `pendingResult.finish()`。
- 原本组装通知的逻辑整段搬进一个私有 `notify(context, userId, type)`，内容一字没改。
- `UserHealthData` 新增 `suspend fun loadFromRoom(userId)`：把 `loadForUser` 里
  「Room → 内存」那半段抽出来，给背景元件用（不触发 Supabase 网路请求）。

---

## 4. Workout History 顶部「总时长」算错 —— 已修

**问题**：真实的运动记录 `duration` 存的是纯秒数字串（例如 `"70"`，来自
`calculating_session` 的 `durationSec.toString()`）。
`WorkoutHistoryScreen` 顶部统计总时长时只用 regex 抓 `"xx min"` / `"xx sec"`，
纯数字串抓不到 → 那笔记录算成 0，总时长偏少。
（单笔卡片显示是对的，因为用的是另一个会处理纯数字的函式。）

**修法**（`exercisescreen/WorkoutHistoryScreen.kt`）：regex 都没命中时，
`totalSeconds += duration.trim().toLongOrNull() ?: 0L`。

---

## 编译环境注意

- 这台机器只有 JDK 25，而 Room 的 KSP2 在 JDK 25 上会报
  `unexpected jvm signature V` —— **触发条件是 DAO 里有回传 `Unit` 的方法**。
  本专案所有 DAO 方法都已经加上回传型别（`insert` 回 `Long`、`update`/`delete` 回 `Int`），
  所以在 toolchain 25 下也能编译通过。你之后自己加 DAO 方法时记得也要有回传型别。

---

## 没改但你可以知道的小问题（不是 error，没动）

- `data/waterscreen/WaterDetails.kt`、`ui.theme` 里部分颜色是死代码（没人引用）。
- 很多画面用 `Icons.Filled.ArrowBack` 等已过时的图示（编译只是 warning，不影响执行）。
- 闹钟提醒用 `AlarmManager` 排程，手机**重开机后提醒会消失**（没有 boot receiver 重新排程）。
- `MealDetailScreen` 的 `progress = meal.kcal / dailyTarget`：`dailyTarget` 理论上可能是 0
  会除零，但 `HealthGoalsScreen` 强制目标必须 > 0，实际到不了。
