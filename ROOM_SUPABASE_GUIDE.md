# Room + Supabase 实作说明（AMIT3353 Assignment）

这份文件说明这个 app 的「本地资料储存（Room）」和「远端资料储存（Supabase）」是怎么做的，
用的是 **Practical 7 / RoomDbTest**（Room）和 **Practical 9**（Supabase）教的方法。
拿去应付 code inspection，或在另一台电脑重做时照着做。

> **状态**：Supabase 已接好并验证过。`SupabaseModule.kt` 里已填入
> `https://wkuhybginfhgklakpeum.supabase.co` + secret key；7 张表已建；
> 已用 REST API 实测 INSERT / SELECT / UPDATE / DELETE 全部通过（HTTP 201/200/204）；
> `assembleDebug` 编译 + 打包 APK 通过。你在 Android Studio Sync + Run 就能用。
> 换成你自己的新 project 时，照下面第 2.1、2.2 节重做即可。

---

## 0. 一句话架构

```
UI 画面 (Screen)  ──►  UserHealthData / UserData  ──►  Room (本地)  +  Supabase (远端)
   不变                  内存缓存 + 写穿透              AppDatabase      RemoteHealthData
```

- 每一次「新增 / 修改 / 删除」，`UserHealthData` 会：①改内存（画面立刻更新）②在背景写进 Room ③在背景写进 Supabase。
- 登入后 / app 重开时，`UserHealthData.loadForUser(userId)` 会：Supabase 拉资料 → 写进 Room → 载入内存。
- 所以：**关掉 app 再开，资料还在（Room）**；**打开 Supabase 网站的 Table Editor，也看得到同一批资料（远端）**。

评分表（QA Checklist）要的「Both local and remote data saving methods have been implemented」就是这两个。

---

## 1. Room（本地）— 对照 Practical 7 / RoomDbTest

### 1.1 依赖（已加好）

`gradle/libs.versions.toml`
```toml
[versions]
room = "2.6.1"
[libraries]
androidx-room-runtime  = { group = "androidx.room", name = "room-runtime",  version.ref = "room" }
androidx-room-ktx      = { group = "androidx.room", name = "room-ktx",      version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

`app/build.gradle.kts`
```kotlin
plugins {
    alias(libs.plugins.ksp)   // Room 用 KSP 产生程式码
}
ksp { arg("room.generateKotlin", "true") }
dependencies {
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
```

### 1.2 三个组成部分（跟 RoomDbTest 的 `Person` / `PersonDao` / `PersonDatabase` 一样）

| RoomDbTest | 这个 app |
|---|---|
| `Person`（@Entity） | `data/local/entity/` 下的 `MealEntity` / `WorkoutEntity` / `SleepEntity` / `WaterEntity` / `GoalsEntity` / `NotificationSettingsEntity` / `UserEntity` |
| `PersonDao`（@Dao） | `data/local/dao/` 下对应的 `MealDao` / `WorkoutDao` / ... |
| `PersonDatabase`（@Database 单例） | `data/local/AppDatabase.kt` |

**Entity** — 栏位跟 `model/` 里的资料类别一对一，另外多一个 `userId`（每个使用者各自的资料）。
Meal / Workout / Sleep / Water 的主键是 `(userId, id)` 复合主键，因为 id 是「每个使用者各自从 1 开始编号」的，
不同使用者会撞到同一个 id。范例：

```kotlin
@Entity(tableName = "meal_table", primaryKeys = ["userId", "id"])
data class MealEntity(
    val userId: Int,
    val id: Int,
    val mealType: String,
    /* ... 其余栏位跟 MealEntry 一样 ... */
) {
    fun toModel(): MealEntry = MealEntry(id, mealType, /* ... */)
    companion object { fun fromModel(userId: Int, m: MealEntry) = MealEntity(userId, m.id, /* ... */) }
}
```

**DAO** — 跟 Practical 7 一样：一个查询 + suspend 的 insert / update / delete。

```kotlin
@Dao
interface MealDao {
    @Query("SELECT * FROM meal_table WHERE userId = :userId ORDER BY id ASC")
    suspend fun getMealsForUser(userId: Int): List<MealEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(meal: MealEntity)
    @Update                                          suspend fun update(meal: MealEntity)
    @Query("DELETE FROM meal_table WHERE userId = :userId AND id = :mealId")
    suspend fun deleteById(userId: Int, mealId: Int)
    @Query("DELETE FROM meal_table WHERE userId = :userId")
    suspend fun clearForUser(userId: Int)
}
```

**Database** — 跟 RoomDbTest 的 `PersonDatabase` 一模一样的单例写法，只是 entities 变多、`version = 2`：

```kotlin
@Database(
    entities = [UserEntity::class, MealEntity::class, WorkoutEntity::class,
        SleepEntity::class, WaterEntity::class, GoalsEntity::class,
        NotificationSettingsEntity::class],
    version = 2, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun mealDao(): MealDao
    /* ... 其余 dao ... */
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "health_coach_database")
                    .fallbackToDestructiveMigration()   // 学生专案，改结构就重建表
                    .build().also { INSTANCE = it }
            }
    }
}
```

> `version` 从 1 变 2（多了 5 张表），加 `fallbackToDestructiveMigration()`：升级时直接重建，不用写 migration。
> 第一次装新版会清掉旧的本地资料库（旧版反正也只存了帐号），属正常。

---

## 2. Supabase（远端）— 对照 Practical 9

### 2.1 在 Supabase 网站要做的事（每台电脑 / 每个新专案都要做一次）

1. https://supabase.com 注册 → 建 organization（Free）。
2. 建 project，region 选 **Southeast Asia (Singapore)**。等它建好。
3. 左边 **SQL Editor** → New query → 贴下面第 2.2 节整段 SQL → **Run**。会一次建好 7 张表。
4. 左边 **Project Settings → API Keys** → 分页 **API Keys** → **Create new API keys** → 建一把 **Secret key**。
   复制起来（长得像 `sb_secret_xxxxxxxx`）。
   > 跟 Practical 9 一样用 Secret key，它会绕过 RLS，4 个 CRUD 直接能用。
5. 顶部 **Connect → Mobile Frameworks → 下拉选 Android Kotlin** → 复制 **Project URL**（`https://xxxx.supabase.co`）。
6. 打开 `app/src/main/java/com/example/assignment/data/remote/SupabaseModule.kt`，把两个值填进去：
   ```kotlin
   private const val SUPABASE_URL = "https://xxxx.supabase.co"
   private const val SUPABASE_KEY = "sb_secret_xxxxxxxx"
   ```
   （填之前 `isConfigured` 是 false，app 会只用 Room 跑，不会崩。）

### 2.2 建表 SQL（贴进 SQL Editor 一次跑完）

```sql
create table if not exists public.profiles (
  id bigint primary key,
  full_name text not null default '',
  email text not null,
  password text not null default '',
  age int not null default 0,
  gender text not null default '',
  height double precision not null default 0,
  weight double precision not null default 0,
  profile_image_uri text not null default '',
  profile_setup_completed boolean not null default false,
  created_at timestamptz not null default now()
);

create table if not exists public.meals (
  user_id bigint not null,
  id bigint not null,
  meal_type text not null default '',
  food_name text not null default '',
  kcal int not null default 0,
  protein int not null default 0,
  carbs int not null default 0,
  fat int not null default 0,
  time text not null default '',
  date text not null default '',
  notes text not null default '',
  image_uri text not null default '',
  created_at timestamptz not null default now(),
  primary key (user_id, id)
);

create table if not exists public.workouts (
  user_id bigint not null,
  id text not null,
  title text not null default '',
  time text not null default '',
  duration text not null default '',
  calories int not null default 0,
  distance_km double precision,
  date text not null default '',
  created_at timestamptz not null default now(),
  primary key (user_id, id)
);

create table if not exists public.sleep (
  user_id bigint not null,
  id bigint not null,
  date text not null default '',
  sleep_type text not null default '',
  bedtime text not null default '',
  wake_up_time text not null default '',
  duration_minutes int not null default 0,
  quality int not null default 0,
  notes text not null default '',
  created_at timestamptz not null default now(),
  primary key (user_id, id)
);

create table if not exists public.water (
  user_id bigint not null,
  water_id bigint not null,
  intake_amount int not null default 0,
  unit text not null default 'ml',
  logged_at text not null default '',
  created_at timestamptz not null default now(),
  primary key (user_id, water_id)
);

create table if not exists public.goals (
  user_id bigint primary key,
  water_goal int not null default 2000,
  meal_goal int not null default 2200,
  exercise_goal int not null default 60,
  sleep_goal int not null default 480,
  created_at timestamptz not null default now()
);

create table if not exists public.notification_settings (
  user_id bigint primary key,
  water_reminder boolean not null default false,
  water_time text not null default '10:00',
  meal_reminder boolean not null default false,
  meal_time text not null default '12:30',
  exercise_reminder boolean not null default false,
  exercise_time text not null default '18:00',
  sleep_reminder boolean not null default false,
  sleep_time text not null default '22:30',
  achievement_notification boolean not null default false,
  created_at timestamptz not null default now()
);
```

> 用 SQL Editor 建的表 **预设 RLS 是关的**，配合 Secret key 使用，跟 Practical 9 一样，不用再设 policy。

### 2.3 依赖（已加好）

`gradle/libs.versions.toml`
```toml
[versions]
supabase = "3.5.0"
ktor = "3.0.3"
coroutines = "1.9.0"
[libraries]
supabase-bom             = { group = "io.github.jan-tennert.supabase", name = "bom", version.ref = "supabase" }
supabase-postgrest       = { group = "io.github.jan-tennert.supabase", name = "postgrest-kt" }
ktor-client-android      = { group = "io.ktor", name = "ktor-client-android", version.ref = "ktor" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
[plugins]
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

`app/build.gradle.kts`
```kotlin
plugins {
    alias(libs.plugins.kotlin.serialization)     // Practical 9：@Serializable 要用
}
android {
    defaultConfig { minSdk = 26 }                // Supabase Kotlin library 要求 minSdk >= 26
}
dependencies {
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.ktor.client.android)
    implementation(libs.kotlinx.coroutines.android)
}
```

`build.gradle.kts`（专案根目录）多一行：
```kotlin
alias(libs.plugins.kotlin.serialization) apply false
```

`AndroidManifest.xml` 在 `<manifest>` 里、`<application>` 外多一行（Practical 9 步骤 18）：
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 2.4 程式（对照 Practical 9 的 `MainActivity.kt`）

**Supabase client 单例** — `data/remote/SupabaseModule.kt`，对应 Practical 9 里那段
`val supabase = createSupabaseClient(...) { install(Postgrest) }`：

```kotlin
object SupabaseModule {
    private const val SUPABASE_URL = "YOUR_SUPABASE_URL"
    private const val SUPABASE_KEY = "YOUR_SUPABASE_SECRET_KEY"
    val isConfigured = SUPABASE_URL.startsWith("https://") && SUPABASE_KEY.length > 20
    val client by lazy {
        createSupabaseClient(supabaseUrl = SUPABASE_URL, supabaseKey = SUPABASE_KEY) {
            install(Postgrest)
        }
    }
}
```

**@Serializable DTO** — `data/remote/HealthDtos.kt`，对应 Practical 9 的 `data class User` / `UserInput`。
Postgres 栏位是 snake_case，所以用 `@SerialName` 对应到 Kotlin 的 camelCase：

```kotlin
@Serializable
data class MealDto(
    @SerialName("user_id") val userId: Int,
    val id: Int,
    @SerialName("meal_type") val mealType: String,
    /* ... */
)
```

**CRUD** — `data/remote/RemoteHealthData.kt`，每个函式的写法都跟 Practical 9 一样：

```kotlin
// 读（对应 supabase.from("contact").select().decodeList<User>()）
db.from("meals").select { filter { eq("user_id", userId) } }.decodeList<MealDto>()

// 新增（对应 .insert(UserInput(...)))
db.from("meals").insert(MealDto.fromModel(userId, meal))

// 修改（对应 .update({ set(...) }) { filter { eq("id", ...) } }）
db.from("meals").update(MealDto.fromModel(userId, meal)) {
    filter { eq("user_id", userId); eq("id", meal.id) }
}

// 删除（对应 .delete { filter { eq("id", userId) } }）
db.from("meals").delete { filter { eq("user_id", userId); eq("id", mealId) } }
```

> `goals` / `notification_settings` 每个使用者只有一列，用 `db.from("goals").upsert(dto)`（有就改、没有就新增）。
> 网路失败时每个函式都用 try/catch 吞掉，只写 log，不让 app 崩 —— Room 那边还是成功的。

---

## 3. Room + Supabase 怎么接在一起

改动集中在 **资料层**，画面（Screen）完全没动。

### `data/UserHealthData.kt`（原本是纯内存）
- 加 `initialize(context)`：存 applicationContext。
- 加 `loadForUser(userId)`：Supabase 拉 → 写 Room → 载入内存的 `SnapshotStateList`。
- 每个 `addMeal / updateMeal / deleteMeal / addWorkout / ...` 里加一个 `persist { }`：
  背景执行「写 Room + 写 Supabase」。内存照旧先改，画面立即反应。
- 新增 `updateWorkout` / `deleteWorkout`（原本 workout 编辑是直接改 list）。

```kotlin
fun addMeal(userId: Int, meal: MealEntry) {
    meals(userId).add(meal)                 // 1. 内存（画面立刻更新）
    persist {                               // 2. 背景
        database()?.mealDao()?.insert(MealEntity.fromModel(userId, meal))   // Room
        RemoteHealthData.insertMeal(userId, meal)                          // Supabase
    }
}
```

### `data/UserData.kt`（帐号）
- `registerUser`：写完 Room 之后 `RemoteHealthData.upsertProfile(...)`。
- `loginUser`：本机 Room 找不到帐号时，去 Supabase `fetchProfileByEmail` 拉回来存进 Room（换手机也能登入）。
- `updateProfile`：Room + Supabase 一起更新。

### `MainActivity.kt`
```kotlin
UserHealthData.initialize(applicationContext)
UserData.initialize(applicationContext)
UserData.currentUser?.id?.let { UserHealthData.loadForUser(it) }   // 已登入 → 先载资料
```

### `nav/AppNavGraph.kt`
```kotlin
val loggedInUserId = UserData.currentUser?.id
LaunchedEffect(loggedInUserId) {           // 登入 / 重新登入 → 载该使用者资料
    loggedInUserId?.let { UserHealthData.loadForUser(it) }
}
```
（另外把 workout_detail 那一行 `list[index] = updatedWorkout` 换成 `UserHealthData.updateWorkout(...)`。）

---

## 4. 怎么验证「本地 + 远端」都有

1. **本地（Room）**：登入 → 新增几笔饮水 / 饮食 / 运动 / 睡眠 → 把 app 从最近工作清单滑掉杀掉 → 重开 → 资料还在。
   （也可以 Android Studio → View → Tool Windows → **App Inspection → Database Inspector** 看 `health_coach_database` 里的表。）
2. **远端（Supabase）**：做完上一步后，打开 Supabase 网站 → **Table Editor** → 点 `meals` / `water` / ... → 看到刚刚那几笔。
   或改一笔 / 删一笔，重新整理，Supabase 里也跟着变。
3. **换装置**：在另一个模拟器安装、用同一个 email / password 登入 → `loadForUser` 会从 Supabase 把资料拉回来。

---

## 5. Build 注意事项（这些都已经处理好，列出来是给之后改 code 时参考）

- 已在本机用 `./gradlew :app:assembleDebug` 完整编译通过（Room 产码 + Supabase + 打包 APK 都 OK），
  只有一些「Icons.Filled.ArrowBack 已过时」之类的既有警告，跟这次的改动无关。
- **KSP 的 `unexpected jvm signature V` 错误**：如果你之后自己加 Room 的 DAO method，
  **每个 method 都要有回传型别**（`insert` 回 `Long`、`update`/`delete` 回 `Int`），
  不要写回传 `Unit` 的 DAO method，否则 Room 的 KSP 会崩在这个错误上。本专案所有 DAO 都已经这样写。
- `Kotlin does not yet support 25 JDK target, falling back to ... JVM_24`：只是警告，能正常 build，不用理。
- 若报 `plugin 'org.jetbrains.kotlin.plugin.serialization' not found`：
  确认根 `build.gradle.kts` 有 `alias(libs.plugins.kotlin.serialization) apply false`。
- minSdk 已从 24 → 26。模拟器 / 实机需 Android 8.0 以上。
- 第一次执行会因为 DB version 2 重建本地资料库（正常，旧版只存了帐号）。
- `local.properties` 每台电脑各自产生（Android Studio 开专案时会自动建），不要 commit。
```
