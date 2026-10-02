# Raseel Messenger (تطبيق رسيل للمراسلة) — v1.000

تطبيق مراسلة فوري حديث، متكامل، وآمن مبني بنظام Android Native باستخدام Kotlin و Jetpack Compose، مع تكامل كامل مع منصة Supabase (قاعدة بيانات PostgreSQL، المصادقة Auth، التخزين Storage، والاتصال اللحظي Realtime عبر WebSockets).

---

## 1. Project Overview (نظرة عامة)

- **اسم التطبيق:** رسيل (Raseel)
- **الإصدار:** v1.000 (versionCode: 1000)
- **المنصة:** Android 7.0+ (API 24+)
- **نوع الواجهة:** Jetpack Compose (Material 3)
- **دعم اللغة والاتجاه:** العربية الافتراضية مع دعم RTL الكامل وإمكانية التبديل إلى الإنجليزية
- **الباك إند:** Supabase (Auth + PostgREST + Realtime WebSockets + Storage)

---

## 2. المميزات الرئيسية في الإصدار v1.000

1. **المصادقة (Authentication):**
   - تسجيل حساب جديد (الاسم الظاهر، اسم المستخدم الفريد `@username`، البريد الإلكتروني، كلمة المرور).
   - تسجيل الدخول وحفظ الجلسة تلقائياً (Session Persistence).
   - تسجيل الخروج الآمن وإنهاء الجلسة.

2. **المحادثات الفردية والجماعية (Direct & Group Chats):**
   - محادثات فردية مباشرة تمنع تكرار المحادثات لنفس المستخدمين.
   - محادثات مجموعات مع تحديد اسم المجموعة وصورتها واختيار الأعضاء.
   - نظام صلاحيات وإدارة المجموعات (Owner, Admin, Member): تعيين المشرفين، إزالة الأعضاء، مغادرة المجموعة.

3. **المراسلة اللحظية (Realtime Messaging):**
   - اتصال لحظي WebSocket مباشر مع خادم Supabase Realtime عبر Phoenix Channels.
   - وصول الرسائل بدون الحاجة لعمل Refresh.
   - تحديثات الواجهة المتفائلة (Optimistic UI) مع معالجة عدم تكرار الرسائل.

4. **أنواع الرسائل والوسائط (Media & Attachments):**
   - **الرسائل النصية:** تدعم الرد، النسخ، والتعديل.
   - **الصور:** اختيار من المعرض عبر Android Photo Picker الآمن، رفعها إلى Supabase Storage، وعرضها فورياً.
   - **الملفات والمستندات:** اختيار الملفات، عرض اسمها وحجمها، ورفعها.
   - **الرسائل الصوتية:** تسجيل صوتي عبر الميكروفون مع عداد زمني، رفع تلقائي، ومشغل صوت تفاعلي مع شريط تقدم.

5. **حالات وتفاعل الرسائل (Message Status & Actions):**
   - مؤشرات التسليم والقراءة الحقيقية: ✓ Sent، ✓✓ Delivered، ✓✓ Read (باللون السماوي).
   - تعديل الرسائل النصية الخاصة بالمرسل فقط مع إظهار وسم `معدلة`.
   - حذف الرسائل: "حذف لدي فقط" أو "حذف للجميع" مع إظهار "تم حذف هذه الرسالة".
   - الرد على الرسائل (Reply) مع شريط معاينة الرسالة المردود عليها.

6. **إدارة الخصوصية وجهات الاتصال:**
   - حظر وإلغاء حظر المستخدمين (Blocking).
   - تثبيت المحادثات في أعلى القائمة (Pinning).
   - كتم الإشعارات للمحادثات (Muting).
   - التحكم في ظهور آخر ظهور (Last Seen) وصورة الحساب ومؤشرات القراءة.

7. **الإشعارات وتخصيص المظهر:**
   - قنوات إشعارات أندرويد (Notification Channel: Messages).
   - دعم الوضع الداكن (Dark Mode) والفاتح (Light Mode).
   - إمكانية ضبط وتعديل مفاتيح ورابط Supabase مباشرة من داخل شاشة الإعدادات.

---

## 3. Architecture (الهيكلية)

تم بناء المشروع باتباع أفضل الممارسات:
- **Clean Architecture + MVVM + Repository Pattern:**
  - `core/`: الاتصال بالشبكة (`SupabaseClient`, `SupabaseRealtimeManager`)، الإشعارات (`NotificationHelper`)، الوسائط الصوتية (`AudioRecorderManager`, `AudioPlayerManager`)، وتنسيق التواريخ والنتائج (`DateUtils`, `Resource`).
  - `data/`: نماذج البيانات (`Models.kt`) ومستودعات البيانات (`Repositories.kt` لـ Auth, Chat, Message, Storage, Profile, Block).
  - `presentation/`: شاشات العرض وحالات الواجهة لكل من (auth, home, chat, groups, search, profile, settings).

---

## 4. Supabase Setup & Database Migrations (Phase 2 Backend)

تم تنظيم الـ Backend بشكل معياري احترافي من خلال ملفات الهجرة في مجلد `supabase/migrations/`:
- `001_initial_schema.sql`: الجداول الأساسية والقيود والمفاتيح الأجنبية.
- `002_constraints_indexes.sql`: فهارس الأداء السريعة وقيود أسماء المستخدمين.
- `003_functions_triggers.sql`: دوال ومحفزات الأمان والمحادثات المباشرة الذرية والتحديث التلقائي.
- `004_rls_policies.sql`: سياسات أمان Row Level Security لكافة الجداول الـ 11.
- `005_storage.sql`: إعداد حاويات التخزين وسياسات الأمان المخصصة للمسارات.
- `006_realtime.sql`: تفعيل الـ Realtime للجداول المطلوبة.

كما يتوفر ملف شامل ومدمج لتشغيله بنقرة واحدة في محرر Supabase:
`supabase/schema.sql`

للتوثيق التفصيلي الشامل لجميع الجداول والدوال وسياسات الأمان، يرجى مراجعة ملف:
`SUPABASE.md`

---

## 5. Environment Variables & App Configuration

في ملف `.env` (أو من خلال شاشة إعدادات الخادم في التطبيق):
```env
SUPABASE_URL=https://your-project-id.supabase.co
SUPABASE_ANON_KEY=your-anon-public-key
```

> **ملاحظة أمان هامة:** التطبيق يستخدم حصراً المفتاح العام `anon key` مع تفعيل RLS في قاعدة البيانات، ولا يتم وضع `service_role key` نهائياً داخل التطبيق.

---

## 6. Build & Packaging Instructions

- للتحقق والتجميع:
  ```bash
  gradle :app:assembleDebug
  ```
- لتشغيل الاختبارات:
  ```bash
  gradle :app:testDebugUnitTest
  ```
- لبناء APK الإصدار النهائي:
  ```bash
  gradle :app:assembleRelease
  ```
  الملف الناتج يكون في: `app/build/outputs/apk/release/app-release-unsigned.apk`
