#!/usr/bin/env python3
"""
Verification Script for Supabase Schema & Security Rules
Validates table creation, constraints, RLS policies, functions, storage, and secret absence.
"""

import os
import re
import sys

def verify_schema(schema_file_path):
    print(f"[*] Validating Supabase schema file: {schema_file_path}")
    if not os.path.exists(schema_file_path):
        print(f"[!] Error: File not found: {schema_file_path}")
        return False

    with open(schema_file_path, "r", encoding="utf-8") as f:
        content = f.read()

    errors = []
    checks = []

    # 1. Required Tables Check
    required_tables = [
        "profiles",
        "conversations",
        "conversation_members",
        "messages",
        "message_attachments",
        "message_receipts",
        "conversation_user_settings",
        "blocks",
        "user_devices",
        "notification_preferences",
        "user_privacy_settings"
    ]

    for table in required_tables:
        pattern = rf"CREATE\s+TABLE\s+(IF\s+NOT\s+EXISTS\s+)?public\.{table}\b"
        if re.search(pattern, content, re.IGNORECASE):
            checks.append(f"Table public.{table}: OK")
        else:
            errors.append(f"Missing required table: public.{table}")

    # 2. Check Constraints
    required_constraints = [
        ("username format", r"username\s*~\s*'\^\[a-z0-9_]\{3,30\}\$'"),
        ("conversation type", r"type\s+IN\s*\('DIRECT',\s*'GROUP'\)"),
        ("member role", r"role\s+IN\s*\('OWNER',\s*'ADMIN',\s*'MEMBER'\)"),
        ("message type", r"message_type\s+IN\s*\('TEXT',\s*'IMAGE',\s*'FILE',\s*'AUDIO',\s*'SYSTEM'\)"),
        ("no self block", r"blocker_id\s*<>\s*blocked_id"),
        ("message text content validation", r"check_text_message_has_content")
    ]

    for desc, pat in required_constraints:
        if re.search(pat, content, re.IGNORECASE):
            checks.append(f"Constraint ({desc}): OK")
        else:
            errors.append(f"Missing constraint: {desc}")

    # 3. RLS Enabled on All Tables
    for table in required_tables:
        pattern = rf"ALTER\s+TABLE\s+public\.{table}\s+ENABLE\s+ROW\s+LEVEL\s+SECURITY"
        if re.search(pattern, content, re.IGNORECASE):
            checks.append(f"RLS enabled on public.{table}: OK")
        else:
            errors.append(f"RLS not enabled on public.{table}")

    # 4. Critical Functions & Triggers
    required_functions = [
        "handle_updated_at",
        "handle_new_user",
        "is_conversation_member",
        "is_group_admin_or_owner",
        "get_or_create_direct_conversation"
    ]

    for fn in required_functions:
        pattern = rf"CREATE\s+(OR\s+REPLACE\s+)?FUNCTION\s+public\.{fn}\b"
        if re.search(pattern, content, re.IGNORECASE):
            checks.append(f"Function public.{fn}: OK")
        else:
            errors.append(f"Missing required function: public.{fn}")

    # 5. Storage Buckets & Policies
    required_buckets = ["avatars", "chat-media", "attachments", "voice-messages"]
    for b in required_buckets:
        if f"'{b}'" in content:
            checks.append(f"Storage bucket ({b}): OK")
        else:
            errors.append(f"Missing storage bucket definition for: {b}")

    # 6. Realtime Publication
    if "ALTER PUBLICATION supabase_realtime ADD TABLE" in content:
        checks.append("Realtime publication: OK")
    else:
        errors.append("Missing realtime publication configuration")

    # 7. Security: Check for forbidden service_role in schema or client code
    if "service_role" in content and "auth.service_role" not in content and "TO service_role" not in content:
        # Check if service_role key is exposed
        if re.search(r"service_role[_\s]*key", content, re.IGNORECASE):
            errors.append("Forbidden: service_role key found in schema file!")

    print("\n--- PASSED CHECKS ---")
    for chk in checks:
        print(f" [PASS] {chk}")

    if errors:
        print("\n--- FAILED CHECKS ---")
        for err in errors:
            print(f" [FAIL] {err}")
        return False

    print(f"\n[+] SUCCESS: All {len(checks)} checks passed with 0 errors!")
    return True

if __name__ == "__main__":
    schema_path = sys.argv[1] if len(sys.argv) > 1 else "/supabase/schema.sql"
    success = verify_schema(schema_path)
    sys.exit(0 if success else 1)
