# dev Messenger (تطبيق ديف للمراسلة الفورية وفائقة التخصيص) — v2.000

تطبيق مراسلة فوري حديث، متكامل، وآمن وفائق التخصيص مبني بنظام Android Native باستخدام Kotlin و Jetpack Compose (Material 3)، مع محرك ثيمات متقدم يتحكم بأكثر من 90% من مظهر الواجهة، ومتجر أصول سحابي، وتكامل اختياري مع منصة Supabase (أو العمل بالوضع المحلي Demo Mode فورياً).

---

## 1. نظرة عامة (Project Overview)

- **اسم التطبيق:** dev
- **الإصدار:** v2.000 (versionCode: 2000)
- **المنصة:** Android 7.0+ (API 24+)
- **نوع الواجهة:** Jetpack Compose (Material Design 3)
- **دعم اللغة والاتجاه:** دعم كامل للعربية (RTL الافتراضي) والإنجليزية (LTR)
- **محرك التخصيص:** Dev Theme & Customization Engine v2
- **الباك إند:** Supabase (Auth + PostgreSQL + Realtime WebSockets + Storage) مع وضع تجريبي محلي متكامل Offline/Demo Mode يعمل فورياً بدون إعدادات

---

## 2. المميزات الرئيسية في التطبيق

### أ. محرك التخصيص الفائق (Ultra-Customization Studio):
1. **32+ ثيم مدمج ومصمم بالكامل:**
   - Dark, Light, AMOLED, Cyberpunk, Gaming, Glassmorphism, Luxury, Minimal, Space, Neon, Sunset, Forest, Retro, Abstract, وغيرها.
2. **32+ خلفية ونمط تفاعلي (Wallpapers & Video Loops):**
   - تدرجات خطية ودائرية، شبكات عقدية متحركة، دوائر وألياف تقنية، فضاء ونجوم، مع تحكم في تقليل الحركة (Reduce Motion).
3. **10+ أنماط أيقونات متطورة (Icon Packs):**
   - Minimal, Outline, Filled, Rounded, Glass, Neon, Gradient, Gaming, Pixel, Luxury.
4. **مصمم فقاعات الشات المتقدم (Bubble Designer):**
   - تخصيص ألوان الرسائل الواردة والصادرة، هوامش الفقاعات، نصف القطر، الذيل، الإطارات والظلال.
5. **مصمم شاشة الهوم وعناصر الواجهة (Home & UI Designer):**
   - شريط التنقل (Pill, Floating, Glass, Minimal, Classic)، أنماط الأزرار، وحقول الإدخال.
6. **متجر الأصول السحابي (Online Asset Store):**
   - جلب وتصفح الثيمات والخلفيات وحزم الأيقونات والديكورات من مصادر عامة ومرخصة، مع نظام التنزيل وإدارة التخزين المؤقت (Cache Manager).
7. **الاستيراد والتصدير:**
   - تصدير واستيراد الثيمات بصيغة `.devtheme` كـ JSON، وزر توليد ثيم عشوائي متناسق (Surprise Me)، والمعاينة الحية الفورية (Live Preview).

### ب. وظائف المراسلة المتقدمة:
1. **المحادثات الفردية والجماعية:**
   - محادثات مباشرة بين المستخدمين، ومجموعات مع صلاحيات المشرفين والأعضاء وصور مخصصة.
2. **المراسلة اللحظية والوسائط المتعددة:**
   - رسائل نصية، صور وفيديوهات ومستندات، ورسائل صوتية مع مشغل صوت تفاعلي وشريط تقدم.
   - حالات تسليم وقراءة حقيقية: ✓ Sent, ✓✓ Delivered, ✓✓ Read.
   - تعديل الرسائل وحذفها (لدي فقط أو للجميع).
3. **لوحة الرموز التعبيرية المحسنة (Emoji Picker):**
   - اختيار متعدد وسلس للرموز التعبيرية بدون إغلاق عشوائي، مع زر تبديل فوري للوحة المفاتيح وزر حذف الرمز الأخير (Backspace).
4. **الخصوصية والبحث:**
   - حظر وإلغاء حظر المستخدمين، تثبيت المحادثات، وكتم الإشعارات، والبحث المتقدم في الرسائل.

---

## 3. الهيكلية المعمارية (Architecture)

مبني وفق مبادئ **Clean Architecture + MVVM + Repository Pattern**:
- `core/customization/`: محرك التخصيص والثيمات، مصير الخلفيات، أنماط الأيقونات، وإدارة التخزين.
- `core/assets/`: متجر الأصول السحابي، إدارة التنزيلات والذاكرة المؤقتة.
- `core/network/`: اتصال Supabase و Realtime WebSockets عبر OkHttp.
- `data/models/`: نماذج البيانات والرسائل والمحادثات والملفات الشخصية.
- `data/repository/`: مستودعات البيانات مع طبقة التبديل الشفاف بين Supabase و DemoDataManager.
- `presentation/`: واجهات Jetpack Compose موزعة على وحدات (customization, chat, home, auth, groups, settings, contacts).

---

## 4. التشغيل والبناء (Build & Run)

### المتطلبات:
- Android Studio Ladybug / Meerkat أو أحدث
- JDK 17 أو 21
- Android SDK 36 (minSdk 24)

### الأوامر عبر Gradle:
- **فحص وبناء حزمة التصحيح (Debug APK):**
  ```bash
  gradle :app:assembleDebug
  ```
  الملف الناتج: `app/build/outputs/apk/debug/app-debug.apk`

- **تشغيل الاختبارات (Local Unit & Robolectric Tests):**
  ```bash
  gradle :app:testDebugUnitTest
  ```

- **بناء حزمة الإصدار (Release APK):**
  ```bash
  gradle :app:assembleRelease
  ```

---

## 5. جاهزية الرفع على GitHub (GitHub Readiness Checklist)

- [x] تم استبعاد ملفات البناء المؤقتة و `.gradle` و `.env` و `debug.keystore` في `.gitignore`.
- [x] تم توفير ملف `.env.example` كقالب آمن بدون مفاتيح سرية مكشوفة.
- [x] الكود البرمجي بالكامل لا يحتوي على مفاتيح سرية مضمنة، ويعتمد على Secrets Gradle Plugin.
- [x] التطبيق يعمل تلقائياً وبسلاسة في الوضع التجريبي (Demo Mode) فور تثبيته دون اشتراط إدخال بيانات سحابية.
- [x] جميع اختبارات الكود والوحدات تجتاز بنجاح (`testDebugUnitTest` PASSED).
- [x] البناء والترجمة ناجحان 100% (`assembleDebug` SUCCESSFUL).
