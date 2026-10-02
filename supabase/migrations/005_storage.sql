-- Supabase Migration: 005_storage.sql
-- Description: Storage buckets setup and strict access control policies
-- Version: v1.000

-- 1. PROVISION STORAGE BUCKETS
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES 
    (
        'avatars', 
        'avatars', 
        true, 
        5242880, -- 5 MB limit
        ARRAY['image/jpeg', 'image/png', 'image/webp', 'image/gif']
    ),
    (
        'chat-media', 
        'chat-media', 
        false, -- Private bucket
        20971520, -- 20 MB limit
        ARRAY['image/jpeg', 'image/png', 'image/webp', 'image/gif', 'video/mp4']
    ),
    (
        'attachments', 
        'attachments', 
        false, -- Private bucket
        52428800, -- 50 MB limit
        NULL -- All mime types allowed
    ),
    (
        'voice-messages', 
        'voice-messages', 
        false, -- Private bucket
        10485760, -- 10 MB limit
        ARRAY['audio/m4a', 'audio/mp4', 'audio/aac', 'audio/ogg', 'audio/mpeg']
    )
ON CONFLICT (id) DO UPDATE SET
    public = EXCLUDED.public,
    file_size_limit = EXCLUDED.file_size_limit,
    allowed_mime_types = EXCLUDED.allowed_mime_types;

-- 2. STORAGE RLS POLICIES

-- AVATARS POLICIES
-- Path convention: avatars/{user_id}/{filename}
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
-- Path convention: chat-media/{conversation_id}/{filename}
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
-- Path convention: attachments/{conversation_id}/{filename}
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
-- Path convention: voice-messages/{conversation_id}/{filename}
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
