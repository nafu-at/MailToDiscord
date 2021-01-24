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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import jakarta.mail.*;
import jakarta.mail.search.FlagTerm;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.Properties;

@Slf4j
public class Main {
    private static JDA jda;
    private static MTDConfig config;

    public static void main(String[] args) {
        log.info("\n __  __       _ _ _____     ____  _                       _ \n" +
                "|  \\/  | __ _(_) |_   _|__ |  _ \\(_)___  ___ ___  _ __ __| |\n" +
                "| |\\/| |/ _` | | | | |/ _ \\| | | | / __|/ __/ _ \\| '__/ _` |\n" +
                "| |  | | (_| | | | | | (_) | |_| | \\__ \\ (_| (_) | | | (_| |\n" +
                "|_|  |_|\\__,_|_|_| |_|\\___/|____/|_|___/\\___\\___/|_|  \\__,_|\n" +
                "                                                            \n");
        log.info("Welcome to MailToDiscord. Starting v" + Main.class.getPackage().getImplementationVersion() + ".");

        // Load Configuration file.
        ConfigLoader configLoader = new ConfigLoader("config.yml");
        configLoader.reloadConfig();
        config = configLoader.getConfig();

        // Set Logger level.
        Level logLevel = config.isDebugMode() ? Level.DEBUG : Level.toLevel(config.getLogLevel());
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        Logger jdaLogger = (Logger) LoggerFactory.getLogger("net.dv8tion");
        Logger cpLogger = (Logger) LoggerFactory.getLogger("com.zaxxer.hikari");
        root.setLevel(logLevel);
        jdaLogger.setLevel(logLevel);
        cpLogger.setLevel(logLevel);

        log.debug(config.toString());

        // Login Discord Server
        try {
            JDABuilder builder = JDABuilder.createDefault(config.getAuthorization().getDiscord().getAccessToken());
            jda = builder.build().awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        Guild guild = jda.getGuildById(config.getAuthorization().getDiscord().getGuildId());
        TextChannel receiveChannel = guild.getTextChannelById(config.getAuthorization().getDiscord().getReceiveChannel());
        TextChannel sendChannel = guild.getTextChannelById(config.getAuthorization().getDiscord().getSendChannel());

        // Setup IMAP Server
        MTDConfig.ServerAuthConfig imapConfig = config.getAuthorization().getMailServer().getImapServer();
        Properties imapProperties = new Properties();
        switch (imapConfig.getSslProtocol()) {
            case TLS:
                imapProperties.setProperty("mail.imap.ssl.enable", String.valueOf(true));
                imapProperties.setProperty("mail.imap.ssl.trust", imapConfig.getServerAddress());
                break;

            case STARTTLS:
                imapProperties.setProperty("mail.imap.starttls.enable", String.valueOf(true));
                imapProperties.setProperty("mail.imap.ssl.trust", imapConfig.getServerAddress());
                break;

            case NONE:
            default:
                break;

        }

        // Connect to IMAP Server
        Session session = Session.getDefaultInstance(imapProperties);
        try {
            Store store = session.getStore("imap");
            session.setDebug(config.isDebugMode());
            store.connect(imapConfig.getServerAddress(), imapConfig.getPort(), imapConfig.getUsername(), imapConfig.getPassword());
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            inbox.addMessageCountListener(new MessageReceiveEventListener(receiveChannel));

            // Fetch unseen messages from inbox folder
            Message[] messages = inbox.search(
                    new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            // Sort messages from recent to oldest
            Arrays.sort(messages, (m1, m2) -> {
                try {
                    return m2.getSentDate().compareTo(m1.getSentDate());
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            });


            // Send a message to Discord
            for (Message message : messages) {
                receiveChannel.sendMessage(MailEmbedBuilder.buildMailEmbed(message)).queue();
                message.setFlag(Flags.Flag.SEEN, true);
                message.saveChanges();
            }
        } catch (NoSuchProviderException e1) {
            log.error("", e1);
        } catch (AuthenticationFailedException e2) {
            log.error("Attempted to connect to the mail server, but the authentication did not complete.", e2);
        } catch (MessagingException e3) {
            log.error("", e3);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down the system...");
            if (jda != null)
                jda.shutdown();
            log.info("See you again!");
        }));
    }

    public static JDA getJda() {
        return jda;
    }

    public static MTDConfig getConfig() {
        return config;
    }
}
