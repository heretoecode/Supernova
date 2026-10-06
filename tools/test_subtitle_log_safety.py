"""Source guards for private subtitle authentication and download values."""
import pathlib
import re
import unittest

SOURCES = pathlib.Path(__file__).resolve().parents[1] / "src/main/java/com/archos/mediacenter/video/utils"


class SubtitleLogSafetyTest(unittest.TestCase):
    def test_credentials_and_download_links_are_not_logging_arguments(self):
        for filename in ("OpenSubtitlesApiHelper.java", "SubtitlesDownloaderActivity2.java"):
            for number, line in enumerate((SOURCES / filename).read_text().splitlines(), 1):
                if re.search(r"log\.(?:debug|info|warn|error|trace)\(", line):
                    arguments = re.sub(r'"(?:\\.|[^"\\])*"', '""', line)
                    self.assertNotRegex(arguments, r"\b(?:apiKey|authToken|password|subtitleLink|subUrl)\b",
                                        f"{filename}:{number} logs private authentication/transfer material")

    def test_login_parse_failure_does_not_log_response_exception(self):
        self.assertNotIn('log.error("login: caught JSONException", e)',
                         (SOURCES / "OpenSubtitlesApiHelper.java").read_text())


if __name__ == "__main__":
    unittest.main()
