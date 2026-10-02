-- Supabase Migration: 004_rls_policies.sql
-- Description: Zero-Trust Row Level Security (RLS) policies for all exposed tables
-- Version: v1.000

-- Enable RLS on every table
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

-- ----------------------------------------------------
-- 1. PROFILES POLICIES
-- ----------------------------------------------------
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

-- ----------------------------------------------------
-- 2. CONVERSATIONS POLICIES
-- ----------------------------------------------------
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

-- ----------------------------------------------------
-- 3. CONVERSATION MEMBERS POLICIES
-- ----------------------------------------------------
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

-- ----------------------------------------------------
-- 4. MESSAGES POLICIES
-- ----------------------------------------------------
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

-- ----------------------------------------------------
-- 5. MESSAGE ATTACHMENTS POLICIES
-- ----------------------------------------------------
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

-- ----------------------------------------------------
-- 6. MESSAGE RECEIPTS POLICIES
-- ----------------------------------------------------
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

-- ----------------------------------------------------
-- 7. CONVERSATION USER SETTINGS POLICIES
-- ----------------------------------------------------
DROP POLICY IF EXISTS "Users can manage their own conversation settings" ON public.conversation_user_settings;
CREATE POLICY "Users can manage their own conversation settings"
    ON public.conversation_user_settings FOR ALL
    TO authenticated
    USING (user_id = auth.uid())
    WITH CHECK (user_id = auth.uid());

-- ----------------------------------------------------
-- 8. BLOCKS POLICIES
-- ----------------------------------------------------
DROP POLICY IF EXISTS "Users can view and manage their own blocks" ON public.blocks;
CREATE POLICY "Users can view and manage their own blocks"
    ON public.blocks FOR ALL
    TO authenticated
    USING (blocker_id = auth.uid())
    WITH CHECK (blocker_id = auth.uid());

-- ----------------------------------------------------
-- 9. USER DEVICES POLICIES
-- ----------------------------------------------------
DROP POLICY IF EXISTS "Users can manage their own devices" ON public.user_devices;
CREATE POLICY "Users can manage their own devices"
    ON public.user_devices FOR ALL
    TO authenticated
    USING (user_id = auth.uid())
    WITH CHECK (user_id = auth.uid());

-- ----------------------------------------------------
-- 10. NOTIFICATION PREFERENCES POLICIES
-- ----------------------------------------------------
DROP POLICY IF EXISTS "Users can view and edit their own notification preferences" ON public.notification_preferences;
CREATE POLICY "Users can view and edit their own notification preferences"
    ON public.notification_preferences FOR ALL
    TO authenticated
    USING (user_id = auth.uid())
    WITH CHECK (user_id = auth.uid());

-- ----------------------------------------------------
-- 11. USER PRIVACY SETTINGS POLICIES
-- ----------------------------------------------------
DROP POLICY IF EXISTS "Users can view and edit their own privacy settings" ON public.user_privacy_settings;
CREATE POLICY "Users can view and edit their own privacy settings"
    ON public.user_privacy_settings FOR ALL
    TO authenticated
    USING (user_id = auth.uid())
    WITH CHECK (user_id = auth.uid());
