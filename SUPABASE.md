# Raseel Messenger — Supabase Backend Specification & Architecture (Phase 2)

وثيقة التوثيق الرسمية لمعمارية وقواعد بيانات وخوادم تطبيق **رسيل (Raseel Messenger)** على منصة **Supabase** (PostgreSQL 15+).

---

## 1. نظرة عامة على الـ Backend

- **محرك قاعدة البيانات:** PostgreSQL 15+
- **المصادقة (Auth):** Supabase Auth (Email + Password)
- **التخزين (Storage):** Supabase Storage (4 Buckets: `avatars`, `chat-media`, `attachments`, `voice-messages`)
- **الاتصال اللحظي (Realtime):** Supabase Realtime (WebSocket Phoenix Channels عبر نشر جداول قاعدة البيانات)
- **نموذج الأمان:** Zero-Trust Row Level Security (RLS) مفعّل 100% على كافة الجداول بدون استثناء

---

## 2. هيكلية الجداول والعلاقات (Database Schema)

### 2.1 جدول المستخدمين (`public.profiles`)
يرتبط مباشرة بجدول المصادقة `auth.users(id)` بنظام الحذف المتتالي `ON DELETE CASCADE`.

| الحقل | النوع | القيود | الوصف |
|---|---|---|---|
| `id` | `UUID` | `PRIMARY KEY`, `REFERENCES auth.users(id)` | المعرف الموحد للمستخدم |
| `username` | `TEXT` | `NOT NULL`, `CHECK (3-30 chars, lowercase)` | اسم المستخدم الفريد `@username` |
| `display_name` | `TEXT` | `NOT NULL` | الاسم الظاهر في المحادثات |
| `bio` | `TEXT` | `DEFAULT ''` | النبذة التعريفية الشخصية |
| `avatar_url` | `TEXT` | `DEFAULT ''` | رابط الصورة الشخصية |
| `is_online` | `BOOLEAN` | `DEFAULT false` | حالة الاتصال اللحظي |
| `last_seen` | `TIMESTAMPTZ`| `DEFAULT now()` | تاريخ وتوقيت آخر نشاط |
| `created_at` | `TIMESTAMPTZ`| `DEFAULT now()` | توقيت إنشاء الحساب |
| `updated_at` | `TIMESTAMPTZ`| `DEFAULT now()` | توقيت آخر تعديل للبيانات |

- **الفهارس:** `UNIQUE INDEX idx_profiles_username_lower ON public.profiles (LOWER(username))` لمنع التكرار بصرف النظر عن حالة الأحرف.

---

### 2.2 جدول المحادثات (`public.conversations`)
نموذج موحد يدعم المحادثات الفردية (`DIRECT`) والمجموعات (`GROUP`).

| الحقل | النوع | القيود | الوصف |
|---|---|---|---|
| `id` | `UUID` | `PRIMARY KEY`, `DEFAULT gen_random_uuid()` | معرف المحادثة |
| `type` | `TEXT` | `CHECK (type IN ('DIRECT', 'GROUP'))` | نوع المحادثة |
| `title` | `TEXT` | `NULL` للمحادثات الفردية | اسم المجموعة |
| `avatar_url` | `TEXT` | `NULL` | صورة المجموعة |
| `created_by` | `UUID` | `REFERENCES profiles(id) ON DELETE SET NULL` | منشئ المحادثة |
| `last_message_text` | `TEXT` | `DEFAULT ''` | مقتطف آخر رسالة |
| `last_message_at` | `TIMESTAMPTZ` | `DEFAULT now()` | توقيت آخر نشاط للمحادثة |
| `last_message_sender_id` | `UUID` | `REFERENCES profiles(id) ON DELETE SET NULL` | مرسل آخر رسالة |

---

### 2.3 جدول أعضاء المحادثات (`public.conversation_members`)

| الحقل | النوع | القيود | الوصف |
|---|---|---|---|
| `id` | `UUID` | `PRIMARY KEY`, `DEFAULT gen_random_uuid()` | المعرف |
| `conversation_id` | `UUID` | `REFERENCES conversations(id) ON DELETE CASCADE` | معرف المحادثة |
| `user_id` | `UUID` | `REFERENCES profiles(id) ON DELETE CASCADE` | معرف العضو |
| `role` | `TEXT` | `CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER'))` | رتبة العضو في المحادثة |
| `joined_at` | `TIMESTAMPTZ` | `DEFAULT now()` | تاريخ الانضمام |
| `last_read_at` | `TIMESTAMPTZ` | `DEFAULT now()` | توقيت آخر قراءة |
| `is_pinned` | `BOOLEAN` | `DEFAULT false` | تثبيت المحادثة للمستخدم |
| `is_muted` | `BOOLEAN` | `DEFAULT false` | كتم تنبيهات المحادثة |
| `unread_count` | `INT` | `DEFAULT 0 CHECK (unread_count >= 0)` | عدد الرسائل غير المقروءة |

- **القيود:** `UNIQUE (conversation_id, user_id)` تمنع انضمام نفس المستخدم لنفس المحادثة أكثر من مرة.

---

### 2.4 جدول الرسائل (`public.messages`)

| الحقل | النوع | القيود | الوصف |
|---|---|---|---|
| `id` | `UUID` | `PRIMARY KEY`, `DEFAULT gen_random_uuid()` | معرف الرسالة |
| `conversation_id` | `UUID` | `REFERENCES conversations(id) ON DELETE CASCADE` | المحادثة التابعة لها |
| `sender_id` | `UUID` | `REFERENCES profiles(id) ON DELETE CASCADE` | مرسل الرسالة |
| `content` | `TEXT` | مدعوم بنص أو وسائط | محتوى الرسالة |
| `message_type` | `TEXT` | `CHECK (type IN ('TEXT', 'IMAGE', 'FILE', 'AUDIO', 'SYSTEM'))` | نوع الرسالة |
| `reply_to_message_id` | `UUID` | `REFERENCES messages(id) ON DELETE SET NULL` | الرسالة المردود عليها |
| `forwarded_from_message_id`| `UUID` | `REFERENCES messages(id) ON DELETE SET NULL` | الرسالة المحولة الأصلية |
| `is_edited` | `BOOLEAN` | `DEFAULT false` | هل تم تعديل الرسالة |
| `deleted_at` | `TIMESTAMPTZ`| `NULL` | توقيت الحذف (Soft Delete) |
| `deleted_for_users` | `UUID[]` | `DEFAULT '{}'` | مصفوفة معرّفات من حذفوها لديهم فقط |
| `status` | `TEXT` | `CHECK (status IN ('SENT', 'DELIVERED', 'READ'))` | حالة وصول وقراءة الرسالة |

---

### 2.5 الجداول التكميلية
- `message_attachments`: تخزين تفاصيل الملفات (المجلد، الاسم، الحجم، نوع MIME، الأبعاد، والمدة الزمنية للصوت).
- `message_receipts`: توثيق دقيق لتسليم وقراءة كل رسالة لكل مستخدم `UNIQUE (message_id, user_id)`.
- `conversation_user_settings`: إعدادات وتفضيلات المستخدم الخاصة بكل محادثة (كتم، تثبيت، أرشفة).
- `blocks`: حظر جهات الاتصال مع قيد صارم `CHECK (blocker_id <> blocked_id)` يمنع حظر النفس.
- `user_devices`: تسجيل معرّفات الأجهزة ورموز الإشعارات `push_token` لمنصات Android/iOS.
- `notification_preferences`: تفضيلات التنبيهات (تفعيل الرسائل، معاينة المحتوى، الأصوات، الاهتزاز).
- `user_privacy_settings`: خصوصية الحساب (ظهور آخر نشاط `last_seen_visibility`، وصورة الملف `profile_photo_visibility`، ومؤشرات القراءة).

---

## 3. الدوال البرمجية والمشغلات (Functions & Triggers)

1. **`handle_updated_at()`:**
   مشغل تلقائي يحدث عمود `updated_at = now()` قبل كل عملية تحديث، مع ضبط صارم للبيئة `SECURITY DEFINER SET search_path = public, pg_temp`.

2. **`handle_new_user()`:**
   مشغل مربوط بـ `auth.users AFTER INSERT`: يستخرج بيانات المستخدم تلقائياً وينشئ سجله في `public.profiles` وضبط تفضيلات الإشعارات والخصوصية الافتراضية بأمان.

3. **`is_conversation_member(p_conv_id, p_user_id)`:**
   دالة تحقق خفيفة ومفهرسة تستخدم داخل سياسات الـ RLS لمنع تكرار الاستعلامات الثقيلة.

4. **`is_group_admin_or_owner(p_conv_id, p_user_id)`:**
   دالة فحص صلاحيات الإدارة في المجموعات للتحقق قبل إضافة الأعضاء أو ترقيتهم أو حذفهم.

5. **`get_or_create_direct_conversation(other_user_id)`:**
   دالة ذرية (Atomic Transaction) تضمن:
   - منع إنشاء محادثة مع النفس.
   - التحقق من عدم وجود حظر متبادل بين الطرفين.
   - البحث عن المحادثة الثنائية القائمة بالفعل وإرجاعها (منع الازدواجية).
   - أو إنشاء محادثة جديدة وإضافة الطرفين في معاملة واحدة.

---

## 4. سياسات الأمان (Row Level Security - RLS)

- تم تفعيل RLS على **كافة الجداول الـ 11**.
- **المحادثات:** لا يمكن لأي مستخدم استعلام أو قراءة أو الاستماع لمحادثة ما لم يكن مسجلاً في جدول `conversation_members` التابع لها.
- **الرسائل:**
  - القراءة: مقتصرة حصراً على أعضاء المحادثة.
  - الإرسال: يشترط أن يكون `auth.uid() = sender_id`، وأن يكون المرسل عضواً فعالاً.
  - التعديل والحذف: مقتصر حصراً على المرسل الأصلي للرسالة.
- **الحظر:** لا يمكن للمستخدم إضافة أو حذف أو استعراض حظر إلا لسجلاته الشخصية `blocker_id = auth.uid()`.

---

## 5. سياسات التخزين (Storage Buckets & Policies)

1. **`avatars` (عام للقراءة):**
   - القراءة: متاحة للمستخدمين المسجلين.
   - الرفع/التعديل: محصور في المسار `avatars/{user_id}/*` حيث `auth.uid()::text = (storage.foldername(name))[1]`.

2. **`chat-media` و `attachments` و `voice-messages` (خاصة مشفرة):**
   - المسار المنظم: `{bucket}/{conversation_id}/{filename}`
   - القراءة والرفع: مشروطة حصراً بكون المستخدم عضواً معتمداً في المحادثة المحددة في المسار:
     `public.is_conversation_member((storage.foldername(name))[1]::uuid, auth.uid())`
   - يتم حجب الملفات تلقائياً عن أي مستخدم خارج المحادثة.

---

## 6. الاتصال اللحظي (Realtime)

تم تفعيل المنشور `supabase_realtime` للجداول التالية:
- `public.messages` (استقبال فوري للرسائل)
- `public.conversations` (تحديث قائمة الدردشات وآخر رسالة)
- `public.conversation_members` (تحديثات العضوية والصلاحيات والعدد غير المقروء)
- `public.message_receipts` (مؤشرات التسليم والقراءة اللحظية)
- `public.profiles` (تحديثات التواجد وحالة Online/Offline)

---

## 7. خطوات تثبيت وتشغيل الـ Migrations

1. في لوحة تحكم مشروعك في [Supabase](https://supabase.com):
2. توجه إلى **SQL Editor**.
3. افتح الملف المدمج `supabase/schema.sql` وانسخ محتواه كاملاً.
4. انقر فوق **Run**.
5. ستنشأ الجداول والفهارس وسياسات الأمان وحاويات التخزين في ثوانٍ معدودة وبدون أي أخطاء.
