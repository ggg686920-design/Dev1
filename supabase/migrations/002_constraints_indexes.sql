-- Supabase Migration: 002_constraints_indexes.sql
-- Description: Indexes, constraints, and uniqueness guarantees
-- Version: v1.000

-- 1. USERNAME INTEGRITY AND PERFORMANCE
-- Enforce valid username pattern: 3 to 30 chars, lowercase letters, numbers, and underscores
ALTER TABLE public.profiles DROP CONSTRAINT IF EXISTS check_username_format;
ALTER TABLE public.profiles ADD CONSTRAINT check_username_format CHECK (
    username ~ '^[a-z0-9_]{3,30}$'
);

-- Prevent reserved system usernames
ALTER TABLE public.profiles DROP CONSTRAINT IF EXISTS check_username_not_reserved;
ALTER TABLE public.profiles ADD CONSTRAINT check_username_not_reserved CHECK (
    username NOT IN ('admin', 'administrator', 'system', 'root', 'support', 'raseel', 'bot', 'help', 'security')
);

-- Case-insensitive Unique Index for usernames
CREATE UNIQUE INDEX IF NOT EXISTS idx_profiles_username_lower ON public.profiles (LOWER(username));

-- 2. MESSAGES INDEXES
-- Critical for fast paginated chat loading (LIMIT/OFFSET order by created_at DESC)
CREATE INDEX IF NOT EXISTS idx_messages_conversation_created 
    ON public.messages (conversation_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_messages_sender 
    ON public.messages (sender_id);

CREATE INDEX IF NOT EXISTS idx_messages_reply_to 
    ON public.messages (reply_to_message_id) 
    WHERE reply_to_message_id IS NOT NULL;

-- 3. CONVERSATION MEMBERS INDEXES
-- Fast lookup of user's active conversations
CREATE INDEX IF NOT EXISTS idx_conversation_members_user 
    ON public.conversation_members (user_id);

-- Fast lookup of members in a conversation
CREATE INDEX IF NOT EXISTS idx_conversation_members_conv 
    ON public.conversation_members (conversation_id);

-- 4. CONVERSATION USER SETTINGS INDEXES
CREATE INDEX IF NOT EXISTS idx_conv_user_settings 
    ON public.conversation_user_settings (user_id, conversation_id);

-- 5. BLOCKS INDEXES
-- Efficient lookup for blocker checking and blocked checking
CREATE INDEX IF NOT EXISTS idx_blocks_blocker 
    ON public.blocks (blocker_id);

CREATE INDEX IF NOT EXISTS idx_blocks_blocked 
    ON public.blocks (blocked_id);

-- 6. RECEIPTS & ATTACHMENTS INDEXES
CREATE INDEX IF NOT EXISTS idx_receipts_message 
    ON public.message_receipts (message_id);

CREATE INDEX IF NOT EXISTS idx_receipts_user 
    ON public.message_receipts (user_id);

CREATE INDEX IF NOT EXISTS idx_attachments_message 
    ON public.message_attachments (message_id);

-- 7. USER DEVICES INDEX
CREATE INDEX IF NOT EXISTS idx_user_devices_user 
    ON public.user_devices (user_id);
