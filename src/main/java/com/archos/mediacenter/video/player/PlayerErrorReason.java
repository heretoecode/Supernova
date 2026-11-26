package com.archos.mediacenter.video.player;

/**
 * Item 9 (owner, 26 Nov 2026: diagnostics record why, not just what): the engine's error codes in plain reasons, so a
 * failed open says "file could not be read" rather than only "1001".
 */
final class PlayerErrorReason
{
    private PlayerErrorReason()
    {
    }

    static String of(int code, int detail)
    {
        switch (code)
        {
        case 1001:
            return detail == 1000 ? "file_unreadable_network" : "file_unreadable";
        case 1002:
            return "engine_error";
        case 1003:
            return "connection_lost";
        case 1004:
            return "stopped_by_user";
        case 1005:
            return "video_not_supported";
        case 1006:
            return "audio_not_supported";
        case 1007:
            return "video_not_allowed";
        case 100:
            return "media_server_died";
        default:
            return "engine_code_" + code + "_" + detail;
        }
    }
}
