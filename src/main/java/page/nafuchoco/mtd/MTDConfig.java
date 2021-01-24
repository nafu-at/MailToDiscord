/*
 * Copyright 2021 NAFU_at
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package page.nafuchoco.mtd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MTDConfig {
    @JsonProperty("authorization")
    private AuthorizationConfig authorization;

    @JsonProperty("logLevel")
    private String logLevel;
    @JsonProperty("debugMode")
    private boolean debugMode;

    @Getter
    @ToString
    public static class AuthorizationConfig {
        @JsonProperty("mailServer")
        private MailServerConfig mailServer;
        @JsonProperty("discord")
        private DiscordConfig discord;
    }

    @Getter
    @ToString
    public static class MailServerConfig {
        @JsonProperty("address")
        private String address;
        @JsonProperty("smtp")
        private ServerAuthConfig smtpServer;
        @JsonProperty("imap")
        private ServerAuthConfig imapServer;
    }

    @Getter
    @ToString
    public static class ServerAuthConfig {
        @JsonProperty("serverAddress")
        private String serverAddress;
        @JsonProperty("port")
        private int port;
        @JsonProperty("ssl")
        private SSLProtocol sslProtocol;
        @JsonProperty("username")
        private String username;
        @JsonProperty("password")
        private String password;
    }

    @Getter
    @ToString
    public static class DiscordConfig {
        @JsonProperty("accessToken")
        private String accessToken;
        @JsonProperty("guildId")
        private long guildId;
        @JsonProperty("receiveChannel")
        private long receiveChannel;
        @JsonProperty("sendChannel")
        private long sendChannel;
    }

    public enum SSLProtocol {
        NONE, STARTTLS, TLS
    }
}
