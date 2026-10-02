-- ============================================================================
-- Raseel Messenger (تطبيق رسيل للمراسلة) — Development Seed Data
-- CAUTION: Development and Testing Environments ONLY. Do NOT run in Production.
-- ============================================================================

-- Note: In Supabase, auth.users are managed by Supabase Auth service.
-- The profiles below demonstrate the schema structure.

-- Sample Profiles (using static UUIDs for local testing)
INSERT INTO public.profiles (id, username, display_name, bio, avatar_url, is_online, last_seen)
VALUES 
    (
        '00000000-0000-0000-0000-000000000001',
        'sara_ahmed',
        'سارة أحمد',
        'مصممة واجهات ومتحمسة لتطبيقات المراسلة ✨',
        'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150',
        true,
        now()
    ),
    (
        '00000000-0000-0000-0000-000000000002',
        'khaled_ali',
        'خالد العلي',
        'مطور أندرويد ومهتم بالبرمجيات الحرة 🚀',
        'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150',
        true,
        now() - INTERVAL '15 minutes'
    ),
    (
        '00000000-0000-0000-0000-000000000003',
        'mariam_n',
        'مريم النجار',
        'كاتبة ومترجمة ومحبة للقراءة 📚',
        'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150',
        false,
        now() - INTERVAL '2 hours'
    )
ON CONFLICT (id) DO NOTHING;

-- Sample Direct Conversation
INSERT INTO public.conversations (id, type, created_by, last_message_text, last_message_at)
VALUES 
    (
        '10000000-0000-0000-0000-000000000001',
        'DIRECT',
        '00000000-0000-0000-0000-000000000001',
        'أهلاً بك في رسيل! يسعدني تجربتك للتطبيق 👋',
        now() - INTERVAL '5 minutes'
    )
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.conversation_members (conversation_id, user_id, role)
VALUES 
    ('10000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'MEMBER'),
    ('10000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'MEMBER')
ON CONFLICT (conversation_id, user_id) DO NOTHING;

-- Sample Group Conversation
INSERT INTO public.conversations (id, type, title, created_by, last_message_text, last_message_at)
VALUES 
    (
        '20000000-0000-0000-0000-000000000001',
        'GROUP',
        'فريق التطوير (Dev Team)',
        '00000000-0000-0000-0000-000000000001',
        'تم إطلاق الإصدار v1.000 بنجاح',
        now() - INTERVAL '10 minutes'
    )
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.conversation_members (conversation_id, user_id, role)
VALUES 
    ('20000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'OWNER'),
    ('20000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'ADMIN'),
    ('20000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'MEMBER')
ON CONFLICT (conversation_id, user_id) DO NOTHING;
