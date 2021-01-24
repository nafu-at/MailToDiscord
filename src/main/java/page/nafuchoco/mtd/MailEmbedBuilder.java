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

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

@Slf4j
public class MailEmbedBuilder {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");

    private MailEmbedBuilder() {
        throw new UnsupportedOperationException();
    }

    public static MessageEmbed buildMailEmbed(Message message) {
        try {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(message.getSubject());
            builder.addField("from", Arrays.toString(message.getFrom()), true);
            builder.addField("date", DATE_FORMAT.format(message.getSentDate()), true);
            builder.setDescription(message.getContent().toString());
            return builder.build();
        } catch (MessagingException e) {
            log.error("", e);
        } catch (IOException e) {
            log.error("", e);
        }
        return null;
    }
}
