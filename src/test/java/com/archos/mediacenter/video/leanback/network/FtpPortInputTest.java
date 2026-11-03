// Copyright 2026 Courville Software
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.archos.mediacenter.video.leanback.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import com.archos.mediacenter.video.leanback.network.ftp.FtpServerCredentialsDialog;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/** FTP port: empty means the default port; a typed non-number stops the connection; no private details in logs. */
public class FtpPortInputTest {
    @Test
    public void emptyMeansDefaultPort() {
        assertEquals(Integer.valueOf(-1), FtpServerCredentialsDialog.parsePort(""));
        assertEquals(Integer.valueOf(-1), FtpServerCredentialsDialog.parsePort("  "));
        assertEquals(Integer.valueOf(-1), FtpServerCredentialsDialog.parsePort(null));
    }

    @Test
    public void numbersAreKept() {
        assertEquals(Integer.valueOf(2121), FtpServerCredentialsDialog.parsePort("2121"));
        assertEquals(Integer.valueOf(21), FtpServerCredentialsDialog.parsePort(" 21 "));
    }

    @Test
    public void malformedInputIsRejected() {
        assertNull(FtpServerCredentialsDialog.parsePort("21a"));
        assertNull(FtpServerCredentialsDialog.parsePort("port"));
    }

    @Test
    public void dialogLogsNoUsernameHostOrPath() throws Exception {
        String source = new String(Files.readAllBytes(Paths.get(
            "src/main/java/com/archos/mediacenter/video/leanback/network/ftp/FtpServerCredentialsDialog.java")),
            StandardCharsets.UTF_8);
        for (String line : source.split("\n")) {
            if (!line.contains("log."))
                continue;
            assertFalse(line, line.contains("username)") || line.contains(", username"));
            assertFalse(line, line.contains("address)") || line.contains(", address"));
            assertFalse(line, line.contains("path)") || line.contains(", path"));
        }
    }
}
