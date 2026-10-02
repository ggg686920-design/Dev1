-- Compatibility fields used by the existing Android message model.
-- The normalized message_attachments table remains available for future metadata.
ALTER TABLE public.messages ADD COLUMN IF NOT EXISTS attachment_url TEXT;
ALTER TABLE public.messages ADD COLUMN IF NOT EXISTS attachment_name TEXT;
ALTER TABLE public.messages ADD COLUMN IF NOT EXISTS attachment_size BIGINT;
CREATE INDEX IF NOT EXISTS idx_messages_attachment_name
    ON public.messages (attachment_name)
    WHERE attachment_name IS NOT NULL;
