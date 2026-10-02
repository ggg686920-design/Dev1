-- ============================================================================
-- Raseel Messenger (تطبيق رسيل للمراسلة) — Complete Production Database Schema
-- Version: v1.000
-- Target Backend: Supabase PostgreSQL 15+
-- ============================================================================

-- 1. EXTENSIONS
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================================
-- 2. CORE TABLES & CONSTRAINTS
-- ============================================================================

-- PROFILES TABLE
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    username TEXT NOT NULL,
    display_name TEXT NOT NULL,
    bio TEXT DEFAULT '',
    avatar_url TEXT DEFAULT '',
    is_online BOOLEAN DEFAULT false,
    last_seen TIMESTAMPTZ DEFAULT now(),
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT check_username_format CHECK (username ~ '^[a-z0-9_]{3,30}$'),
    CONSTRAINT check_username_not_reserved CHECK (
        username NOT IN ('admin', 'administrator', 'system', 'root', 'support', 'raseel', 'bot', 'help', 'security')
    )
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_profiles_username_lower ON public.profiles (LOWER(username));

-- CONVERSATIONS TABLE
CREATE TABLE IF NOT EXISTS public.conversations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type TEXT NOT NULL CHECK (type IN ('DIRECT', 'GROUP')),
    title TEXT,
    avatar_url TEXT,
    created_by UUID REFERENCES public.profiles(id) ON DELETE SET NULL,
    last_message_text TEXT DEFAULT '',
    last_message_at TIMESTAMPTZ DEFAULT now(),
    last_message_sender_id UUID REFERENCES public.profiles(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- CONVERSATION MEMBERS TABLE
CREATE TABLE IF NOT EXISTS public.conversation_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL REFERENCES public.conversations(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    role TEXT NOT NULL DEFAULT 'MEMBER' CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER')),
    joined_at TIMESTAMPTZ DEFAULT now(),
    last_read_at TIMESTAMPTZ DEFAULT now(),
    is_pinned BOOLEAN DEFAULT false,
    is_muted BOOLEAN DEFAULT false,
    unread_count INT DEFAULT 0 CHECK (unread_count >= 0),
    CONSTRAINT unique_conversation_member UNIQUE (conversation_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_conversation_members_user ON public.conversation_members (user_id);
CREATE INDEX IF NOT EXISTS idx_conversation_members_conv ON public.conversation_members (conversation_id);

-- MESSAGES TABLE
CREATE TABLE IF NOT EXISTS public.messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL REFERENCES public.conversations(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    content TEXT,
    message_type TEXT NOT NULL DEFAULT 'TEXT' CHECK (message_type IN ('TEXT', 'IMAGE', 'FILE', 'AUDIO', 'SYSTEM')),
    reply_to_message_id UUID REFERENCES public.messages(id) ON DELETE SET NULL,
    forwarded_from_message_id UUID REFERENCES public.messages(id) ON DELETE SET NULL,
    is_edited BOOLEAN DEFAULT false,
    deleted_at TIMESTAMPTZ,
    deleted_for_users UUID[] DEFAULT '{}',
    status TEXT NOT NULL DEFAULT 'SENT' CHECK (status IN ('SENT', 'DELIVERED', 'READ')),
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    attachment_url TEXT,
    attachment_name TEXT,
    attachment_size BIGINT,
    CONSTRAINT check_text_message_has_content CHECK (
        message_type <> 'TEXT' OR (content IS NOT NULL AND length(trim(content)) > 0)
    )
);

CREATE INDEX IF NOT EXISTS idx_messages_conversation_created ON public.messages (conversation_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_messages_sender ON public.messages (sender_id);
CREATE INDEX IF NOT EXISTS idx_messages_reply_to ON public.messages (reply_to_message_id) WHERE reply_to_message_id IS NOT NULL;

-- MESSAGE ATTACHMENTS TABLE
CREATE TABLE IF NOT EXISTS public.message_attachments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message_id UUID NOT NULL REFERENCES public.messages(id) ON DELETE CASCADE,
    storage_bucket TEXT NOT NULL CHECK (storage_bucket IN ('avatars', 'chat-media', 'attachments', 'voice-messages')),
    storage_path TEXT NOT NULL,
    file_name TEXT NOT NULL,
    mime_type TEXT NOT NULL,
    file_size BIGINT NOT NULL CHECK (file_size > 0),
    width INT,
    height INT,
    duration_ms INT,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_attachments_message ON public.message_attachments (message_id);

-- MESSAGE RECEIPTS TABLE
CREATE TABLE IF NOT EXISTS public.message_receipts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message_id UUID NOT NULL REFERENCES public.messages(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    delivered_at TIMESTAMPTZ,
    read_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT unique_message_user_receipt UNIQUE (message_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_receipts_message ON public.message_receipts (message_id);
CREATE INDEX IF NOT EXISTS idx_receipts_user ON public.message_receipts (user_id);

-- CONVERSATION USER SETTINGS TABLE
CREATE TABLE IF NOT EXISTS public.conversation_user_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    conversation_id UUID NOT NULL REFERENCES public.conversations(id) ON DELETE CASCADE,
    is_muted BOOLEAN DEFAULT false,
    is_pinned BOOLEAN DEFAULT false,
    is_archived BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT unique_user_conversation_settings UNIQUE (user_id, conversation_id)
);

CREATE INDEX IF NOT EXISTS idx_conv_user_settings ON public.conversation_user_settings (user_id, conversation_id);

-- BLOCKS TABLE
CREATE TABLE IF NOT EXISTS public.blocks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    blocker_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    blocked_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT unique_block_pair UNIQUE (blocker_id, blocked_id),
    CONSTRAINT check_no_self_block CHECK (blocker_id <> blocked_id)
);

CREATE INDEX IF NOT EXISTS idx_blocks_blocker ON public.blocks (blocker_id);
CREATE INDEX IF NOT EXISTS idx_blocks_blocked ON public.blocks (blocked_id);

-- USER DEVICES TABLE
CREATE TABLE IF NOT EXISTS public.user_devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    device_id TEXT NOT NULL,
    push_token TEXT,
    platform TEXT DEFAULT 'android' CHECK (platform IN ('android', 'ios', 'web')),
    last_seen_at TIMESTAMPTZ DEFAULT now(),
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT unique_user_device UNIQUE (user_id, device_id)
);

CREATE INDEX IF NOT EXISTS idx_user_devices_user ON public.user_devices (user_id);

-- NOTIFICATION PREFERENCES TABLE
CREATE TABLE IF NOT EXISTS public.notification_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    messages_enabled BOOLEAN DEFAULT true,
    show_message_preview BOOLEAN DEFAULT true,
    sound_enabled BOOLEAN DEFAULT true,
    vibration_enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- USER PRIVACY SETTINGS TABLE
CREATE TABLE IF NOT EXISTS public.user_privacy_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    last_seen_visibility TEXT NOT NULL DEFAULT 'EVERYONE' CHECK (last_seen_visibility IN ('EVERYONE', 'NOBODY')),
    profile_photo_visibility TEXT NOT NULL DEFAULT 'EVERYONE' CHECK (profile_photo_visibility IN ('EVERYONE', 'NOBODY')),
    read_receipts_enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- ============================================================================
-- 3. FUNCTIONS & TRIGGERS
-- ============================================================================

-- AUTO-UPDATE updated_at FUNCTION
CREATE OR REPLACE FUNCTION public.handle_updated_at()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_profiles_updated_at ON public.profiles;
CREATE TRIGGER trg_profiles_updated_at
    BEFORE UPDATE ON public.profiles
    FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

DROP TRIGGER IF EXISTS trg_conversations_updated_at ON public.conversations;
CREATE TRIGGER trg_conversations_updated_at
    BEFORE UPDATE ON public.conversations
    FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

DROP TRIGGER IF EXISTS trg_messages_updated_at ON public.messages;
CREATE TRIGGER trg_messages_updated_at
    BEFORE UPDATE ON public.messages
    FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

DROP TRIGGER IF EXISTS trg_conversation_user_settings_updated_at ON public.conversation_user_settings;
CREATE TRIGGER trg_conversation_user_settings_updated_at
    BEFORE UPDATE ON public.conversation_user_settings
    FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

DROP TRIGGER IF EXISTS trg_user_devices_updated_at ON public.user_devices;
CREATE TRIGGER trg_user_devices_updated_at
    BEFORE UPDATE ON public.user_devices
    FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

DROP TRIGGER IF EXISTS trg_notification_preferences_updated_at ON public.notification_preferences;
CREATE TRIGGER trg_notification_preferences_updated_at
    BEFORE UPDATE ON public.notification_preferences
    FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

DROP TRIGGER IF EXISTS trg_user_privacy_settings_updated_at ON public.user_privacy_settings;
CREATE TRIGGER trg_user_privacy_settings_updated_at
    BEFORE UPDATE ON public.user_privacy_settings
    FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- AUTO PROFILE INITIALIZATION ON SIGNUP
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
DECLARE
    v_raw_username TEXT;
    v_clean_username TEXT;
    v_display_name TEXT;
BEGIN
    v_raw_username := NULLIF(TRIM(NEW.raw_user_meta_data->>'username'), '');
    v_display_name := NULLIF(TRIM(NEW.raw_user_meta_data->>'display_name'), '');

    IF v_raw_username IS NULL THEN
        v_raw_username := 'user_' || SUBSTRING(REPLACE(NEW.id::text, '-', ''), 1, 8);
    END IF;

    v_clean_username := LOWER(REGEXP_REPLACE(v_raw_username, '[^a-zA-Z0-9_]', '', 'g'));
    IF LENGTH(v_clean_username) < 3 THEN
        v_clean_username := v_clean_username || '_' || SUBSTRING(REPLACE(NEW.id::text, '-', ''), 1, 4);
    END IF;

    IF v_display_name IS NULL THEN
        v_display_name := v_clean_username;
    END IF;

    INSERT INTO public.profiles (id, username, display_name, created_at, updated_at)
    VALUES (NEW.id, v_clean_username, v_display_name, now(), now())
    ON CONFLICT (id) DO UPDATE SET
        display_name = EXCLUDED.display_name,
        updated_at = now();

    INSERT INTO public.notification_preferences (user_id)
    VALUES (NEW.id)
    ON CONFLICT (user_id) DO NOTHING;

    INSERT INTO public.user_privacy_settings (user_id)
    VALUES (NEW.id)
    ON CONFLICT (user_id) DO NOTHING;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

-- HELPER FUNCTIONS FOR MEMBERSHIP AND ADMIN CHECKS
CREATE OR REPLACE FUNCTION public.is_conversation_member(p_conv_id UUID, p_user_id UUID)
RETURNS BOOLEAN
LANGUAGE sql
STABLE
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
    SELECT EXISTS (
        SELECT 1 FROM public.conversation_members
        WHERE conversation_id = p_conv_id
        AND user_id = p_user_id
    );
$$;

CREATE OR REPLACE FUNCTION public.is_group_admin_or_owner(p_conv_id UUID, p_user_id UUID)
RETURNS BOOLEAN
LANGUAGE sql
STABLE
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
    SELECT EXISTS (
        SELECT 1 FROM public.conversation_members
        WHERE conversation_id = p_conv_id
        AND user_id = p_user_id
        AND role IN ('OWNER', 'ADMIN')
    );
$$;

-- GET OR CREATE DIRECT CONVERSATION (ATOMIC)
CREATE OR REPLACE FUNCTION public.get_or_create_direct_conversation(other_user_id UUID)
RETURNS UUID
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
DECLARE
    v_caller_id UUID;
    v_conv_id UUID;
    v_is_blocked BOOLEAN;
BEGIN
    v_caller_id := auth.uid();
    IF v_caller_id IS NULL THEN
        RAISE EXCEPTION 'Authentication required';
    END IF;

    IF v_caller_id = other_user_id THEN
        RAISE EXCEPTION 'Cannot start a direct conversation with yourself';
    END IF;

    SELECT EXISTS (
        SELECT 1 FROM public.blocks
        WHERE (blocker_id = v_caller_id AND blocked_id = other_user_id)
           OR (blocker_id = other_user_id AND blocked_id = v_caller_id)
    ) INTO v_is_blocked;

    IF v_is_blocked THEN
        RAISE EXCEPTION 'Cannot communicate with blocked contact';
    END IF;

    SELECT cm1.conversation_id INTO v_conv_id
    FROM public.conversation_members cm1
    JOIN public.conversation_members cm2 ON cm1.conversation_id = cm2.conversation_id
    JOIN public.conversations c ON c.id = cm1.conversation_id
    WHERE c.type = 'DIRECT'
      AND cm1.user_id = v_caller_id
      AND cm2.user_id = other_user_id
    LIMIT 1;

    IF v_conv_id IS NOT NULL THEN
        RETURN v_conv_id;
    END IF;

    INSERT INTO public.conversations (type, created_by, last_message_text)
    VALUES ('DIRECT', v_caller_id, '')
    RETURNING id INTO v_conv_id;

    INSERT INTO public.conversation_members (conversation_id, user_id, role)
    VALUES 
        (v_conv_id, v_caller_id, 'MEMBER'),
        (v_conv_id, other_user_id, 'MEMBER');

    RETURN v_conv_id;
END;
$$;

-- ============================================================================
-- 4. ROW LEVEL SECURITY (RLS) POLICIES
-- ============================================================================

ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.conversations ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.conversation_members ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.messages ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.message_attachments ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.message_receipts ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.conversation_user_settings ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.blocks ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.user_devices ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notification_preferences ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.user_privacy_settings ENABLE ROW LEVEL SECURITY;

-- PROFILES
DROP POLICY IF EXISTS "Profiles are viewable by authenticated users" ON public.profiles;
CREATE POLICY "Profiles are viewable by authenticated users"
    ON public.profiles FOR SELECT
    TO authenticated
    USING (true);

DROP POLICY IF EXISTS "Users can insert their own profile" ON public.profiles;
CREATE POLICY "Users can insert their own profile"
    ON public.profiles FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid() = id);

DROP POLICY IF EXISTS "Users can update their own profile" ON public.profiles;
CREATE POLICY "Users can update their own profile"
    ON public.profiles FOR UPDATE
    TO authenticated
    USING (auth.uid() = id)
    WITH CHECK (auth.uid() = id);

-- CONVERSATIONS
DROP POLICY IF EXISTS "Users can view conversations they belong to" ON public.conversations;
CREATE POLICY "Users can view conversations they belong to"
    ON public.conversations FOR SELECT
    TO authenticated
    USING (public.is_conversation_member(id, auth.uid()));

DROP POLICY IF EXISTS "Authenticated users can create conversations" ON public.conversations;
CREATE POLICY "Authenticated users can create conversations"
    ON public.conversations FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid() = created_by OR created_by IS NULL);

DROP POLICY IF EXISTS "Members can update conversations" ON public.conversations;
CREATE POLICY "Members can update conversations"
    ON public.conversations FOR UPDATE
    TO authenticated
    USING (public.is_conversation_member(id, auth.uid()))
    WITH CHECK (public.is_conversation_member(id, auth.uid()));

DROP POLICY IF EXISTS "Owners can delete group conversations" ON public.conversations;
CREATE POLICY "Owners can delete group conversations"
    ON public.conversations FOR DELETE
    TO authenticated
    USING (
        type = 'GROUP' AND EXISTS (
            SELECT 1 FROM public.conversation_members
            WHERE conversation_id = conversations.id
            AND user_id = auth.uid()
            AND role = 'OWNER'
        )
    );

-- CONVERSATION MEMBERS
DROP POLICY IF EXISTS "Users can view members of their conversations" ON public.conversation_members;
CREATE POLICY "Users can view members of their conversations"
    ON public.conversation_members FOR SELECT
    TO authenticated
    USING (public.is_conversation_member(conversation_id, auth.uid()));

DROP POLICY IF EXISTS "Authorized members can add members" ON public.conversation_members;
CREATE POLICY "Authorized members can add members"
    ON public.conversation_members FOR INSERT
    TO authenticated
    WITH CHECK (
        user_id = auth.uid()
        OR public.is_group_admin_or_owner(conversation_id, auth.uid())
        OR NOT EXISTS (SELECT 1 FROM public.conversation_members WHERE conversation_id = conversation_members.conversation_id)
    );

DROP POLICY IF EXISTS "Users can update their own member settings or admins update roles" ON public.conversation_members;
CREATE POLICY "Users can update their own member settings or admins update roles"
    ON public.conversation_members FOR UPDATE
    TO authenticated
    USING (
        user_id = auth.uid()
        OR public.is_group_admin_or_owner(conversation_id, auth.uid())
    )
    WITH CHECK (
        user_id = auth.uid()
        OR public.is_group_admin_or_owner(conversation_id, auth.uid())
    );

DROP POLICY IF EXISTS "Users can leave or admins can remove members" ON public.conversation_members;
CREATE POLICY "Users can leave or admins can remove members"
    ON public.conversation_members FOR DELETE
    TO authenticated
    USING (
        user_id = auth.uid()
        OR public.is_group_admin_or_owner(conversation_id, auth.uid())
    );

-- MESSAGES
DROP POLICY IF EXISTS "Users can read messages in their conversations" ON public.messages;
CREATE POLICY "Users can read messages in their conversations"
    ON public.messages FOR SELECT
    TO authenticated
    USING (public.is_conversation_member(conversation_id, auth.uid()));

DROP POLICY IF EXISTS "Members can send messages" ON public.messages;
CREATE POLICY "Members can send messages"
    ON public.messages FOR INSERT
    TO authenticated
    WITH CHECK (
        sender_id = auth.uid()
        AND public.is_conversation_member(conversation_id, auth.uid())
    );

DROP POLICY IF EXISTS "Senders can edit or soft-delete messages" ON public.messages;
CREATE POLICY "Senders can edit or soft-delete messages"
    ON public.messages FOR UPDATE
    TO authenticated
    USING (
        sender_id = auth.uid()
        OR public.is_conversation_member(conversation_id, auth.uid())
    )
    WITH CHECK (
        sender_id = auth.uid()
        OR public.is_conversation_member(conversation_id, auth.uid())
    );

-- MESSAGE ATTACHMENTS
DROP POLICY IF EXISTS "Members can view attachments in their conversations" ON public.message_attachments;
CREATE POLICY "Members can view attachments in their conversations"
    ON public.message_attachments FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM public.messages m
            WHERE m.id = message_attachments.message_id
            AND public.is_conversation_member(m.conversation_id, auth.uid())
        )
    );

DROP POLICY IF EXISTS "Message senders can insert attachments" ON public.message_attachments;
CREATE POLICY "Message senders can insert attachments"
    ON public.message_attachments FOR INSERT
    TO authenticated
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM public.messages m
            WHERE m.id = message_attachments.message_id
            AND m.sender_id = auth.uid()
        )
    );

-- MESSAGE RECEIPTS
DROP POLICY IF EXISTS "Members can view receipts in their conversations" ON public.message_receipts;
CREATE POLICY "Members can view receipts in their conversations"
    ON public.message_receipts FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM public.messages m
            WHERE m.id = message_receipts.message_id
            AND public.is_conversation_member(m.conversation_id, auth.uid())
        )
    );

DROP POLICY IF EXISTS "Users can manage their own receipts" ON public.message_receipts;
CREATE POLICY "Users can manage their own receipts"
    ON public.message_receipts FOR ALL
    TO authenticated
    USING (user_id = auth.uid())
    WITH CHECK (user_id = auth.uid());

-- CONVERSATION USER SETTINGS
DROP POLICY IF EXISTS "Users can manage their own conversation settings" ON public.conversation_user_settings;
CREATE POLICY "Users can manage their own conversation settings"
    ON public.conversation_user_settings FOR ALL
    TO authenticated
    USING (user_id = auth.uid())
    WITH CHECK (user_id = auth.uid());

-- BLOCKS
DROP POLICY IF EXISTS "Users can view and manage their own blocks" ON public.blocks;
CREATE POLICY "Users can view and manage their own blocks"
    ON public.blocks FOR ALL
    TO authenticated
    USING (blocker_id = auth.uid())
    WITH CHECK (blocker_id = auth.uid());

-- USER DEVICES
DROP POLICY IF EXISTS "Users can manage their own devices" ON public.user_devices;
CREATE POLICY "Users can manage their own devices"
    ON public.user_devices FOR ALL
    TO authenticated
    USING (user_id = auth.uid())
    WITH CHECK (user_id = auth.uid());

-- NOTIFICATION PREFERENCES
DROP POLICY IF EXISTS "Users can view and edit their own notification preferences" ON public.notification_preferences;
CREATE POLICY "Users can view and edit their own notification preferences"
    ON public.notification_preferences FOR ALL
    TO authenticated
    USING (user_id = auth.uid())
    WITH CHECK (user_id = auth.uid());

-- USER PRIVACY SETTINGS
DROP POLICY IF EXISTS "Users can view and edit their own privacy settings" ON public.user_privacy_settings;
CREATE POLICY "Users can view and edit their own privacy settings"
    ON public.user_privacy_settings FOR ALL
    TO authenticated
    USING (user_id = auth.uid())
    WITH CHECK (user_id = auth.uid());

-- ============================================================================
-- 5. STORAGE BUCKETS & STORAGE RLS POLICIES
-- ============================================================================

INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES 
    ('avatars', 'avatars', true, 5242880, ARRAY['image/jpeg', 'image/png', 'image/webp', 'image/gif']),
    ('chat-media', 'chat-media', false, 20971520, ARRAY['image/jpeg', 'image/png', 'image/webp', 'image/gif', 'video/mp4']),
    ('attachments', 'attachments', false, 52428800, NULL),
    ('voice-messages', 'voice-messages', false, 10485760, ARRAY['audio/m4a', 'audio/mp4', 'audio/aac', 'audio/ogg', 'audio/mpeg'])
ON CONFLICT (id) DO UPDATE SET
    public = EXCLUDED.public,
    file_size_limit = EXCLUDED.file_size_limit,
    allowed_mime_types = EXCLUDED.allowed_mime_types;

-- AVATARS POLICIES
DROP POLICY IF EXISTS "Avatars viewable by all authenticated users" ON storage.objects;
CREATE POLICY "Avatars viewable by all authenticated users"
    ON storage.objects FOR SELECT
    TO authenticated
    USING (bucket_id = 'avatars');

DROP POLICY IF EXISTS "Users can upload their own avatar" ON storage.objects;
CREATE POLICY "Users can upload their own avatar"
    ON storage.objects FOR INSERT
    TO authenticated
    WITH CHECK (
        bucket_id = 'avatars' 
        AND auth.uid()::text = (storage.foldername(name))[1]
    );

DROP POLICY IF EXISTS "Users can update their own avatar" ON storage.objects;
CREATE POLICY "Users can update their own avatar"
    ON storage.objects FOR UPDATE
    TO authenticated
    USING (
        bucket_id = 'avatars' 
        AND auth.uid()::text = (storage.foldername(name))[1]
    );

DROP POLICY IF EXISTS "Users can delete their own avatar" ON storage.objects;
CREATE POLICY "Users can delete their own avatar"
    ON storage.objects FOR DELETE
    TO authenticated
    USING (
        bucket_id = 'avatars' 
        AND auth.uid()::text = (storage.foldername(name))[1]
    );

-- CHAT MEDIA POLICIES
DROP POLICY IF EXISTS "Conversation members can read chat-media" ON storage.objects;
CREATE POLICY "Conversation members can read chat-media"
    ON storage.objects FOR SELECT
    TO authenticated
    USING (
        bucket_id = 'chat-media'
        AND public.is_conversation_member((storage.foldername(name))[1]::uuid, auth.uid())
    );

DROP POLICY IF EXISTS "Conversation members can upload chat-media" ON storage.objects;
CREATE POLICY "Conversation members can upload chat-media"
    ON storage.objects FOR INSERT
    TO authenticated
    WITH CHECK (
        bucket_id = 'chat-media'
        AND public.is_conversation_member((storage.foldername(name))[1]::uuid, auth.uid())
    );

-- ATTACHMENTS POLICIES
DROP POLICY IF EXISTS "Conversation members can read attachments" ON storage.objects;
CREATE POLICY "Conversation members can read attachments"
    ON storage.objects FOR SELECT
    TO authenticated
    USING (
        bucket_id = 'attachments'
        AND public.is_conversation_member((storage.foldername(name))[1]::uuid, auth.uid())
    );

DROP POLICY IF EXISTS "Conversation members can upload attachments" ON storage.objects;
CREATE POLICY "Conversation members can upload attachments"
    ON storage.objects FOR INSERT
    TO authenticated
    WITH CHECK (
        bucket_id = 'attachments'
        AND public.is_conversation_member((storage.foldername(name))[1]::uuid, auth.uid())
    );

-- VOICE MESSAGES POLICIES
DROP POLICY IF EXISTS "Conversation members can read voice-messages" ON storage.objects;
CREATE POLICY "Conversation members can read voice-messages"
    ON storage.objects FOR SELECT
    TO authenticated
    USING (
        bucket_id = 'voice-messages'
        AND public.is_conversation_member((storage.foldername(name))[1]::uuid, auth.uid())
    );

DROP POLICY IF EXISTS "Conversation members can upload voice-messages" ON storage.objects;
CREATE POLICY "Conversation members can upload voice-messages"
    ON storage.objects FOR INSERT
    TO authenticated
    WITH CHECK (
        bucket_id = 'voice-messages'
        AND public.is_conversation_member((storage.foldername(name))[1]::uuid, auth.uid())
    );

-- ============================================================================
-- 6. REALTIME CONFIGURATION
-- ============================================================================

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_publication_tables 
        WHERE pubname = 'supabase_realtime' AND tablename = 'messages'
    ) THEN
        ALTER PUBLICATION supabase_realtime ADD TABLE public.messages;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_publication_tables 
        WHERE pubname = 'supabase_realtime' AND tablename = 'conversations'
    ) THEN
        ALTER PUBLICATION supabase_realtime ADD TABLE public.conversations;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_publication_tables 
        WHERE pubname = 'supabase_realtime' AND tablename = 'conversation_members'
    ) THEN
        ALTER PUBLICATION supabase_realtime ADD TABLE public.conversation_members;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_publication_tables 
        WHERE pubname = 'supabase_realtime' AND tablename = 'message_receipts'
    ) THEN
        ALTER PUBLICATION supabase_realtime ADD TABLE public.message_receipts;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_publication_tables 
        WHERE pubname = 'supabase_realtime' AND tablename = 'profiles'
    ) THEN
        ALTER PUBLICATION supabase_realtime ADD TABLE public.profiles;
    END IF;
END $$;
