-- Supabase Migration: 003_functions_triggers.sql
-- Description: Automated triggers and secure database functions
-- Version: v1.000

-- 1. TRIGGER FUNCTION: AUTO-UPDATE updated_at
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

-- Attach updated_at trigger to relevant tables
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

-- 2. TRIGGER FUNCTION: NEW USER INITIALIZATION FROM auth.users
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

    -- Insert Profile
    INSERT INTO public.profiles (id, username, display_name, created_at, updated_at)
    VALUES (NEW.id, v_clean_username, v_display_name, now(), now())
    ON CONFLICT (id) DO UPDATE SET
        display_name = EXCLUDED.display_name,
        updated_at = now();

    -- Insert Notification Preferences
    INSERT INTO public.notification_preferences (user_id)
    VALUES (NEW.id)
    ON CONFLICT (user_id) DO NOTHING;

    -- Insert Privacy Settings
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

-- 3. HELPER FUNCTIONS FOR RLS & MEMBERSHIP VERIFICATION
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

-- 4. FUNCTION: GET OR CREATE DIRECT CONVERSATION (ATOMIC & CONCURRENCY SAFE)
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

    -- Check if either user has blocked the other
    SELECT EXISTS (
        SELECT 1 FROM public.blocks
        WHERE (blocker_id = v_caller_id AND blocked_id = other_user_id)
           OR (blocker_id = other_user_id AND blocked_id = v_caller_id)
    ) INTO v_is_blocked;

    IF v_is_blocked THEN
        RAISE EXCEPTION 'Cannot communicate with blocked contact';
    END IF;

    -- Look for existing DIRECT conversation between these two exact members
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

    -- Create new DIRECT conversation
    INSERT INTO public.conversations (type, created_by, last_message_text)
    VALUES ('DIRECT', v_caller_id, '')
    RETURNING id INTO v_conv_id;

    -- Add both participants as members
    INSERT INTO public.conversation_members (conversation_id, user_id, role)
    VALUES 
        (v_conv_id, v_caller_id, 'MEMBER'),
        (v_conv_id, other_user_id, 'MEMBER');

    RETURN v_conv_id;
END;
$$;
