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
import jakarta.mail.event.MessageCountAdapter;
import jakarta.mail.event.MessageCountEvent;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

public class MessageReceiveEventListener extends MessageCountAdapter {
    private final TextChannel receiveChannel;

    public MessageReceiveEventListener(@NotNull TextChannel receiveChannel) {
        this.receiveChannel = receiveChannel;
    }

    @Override
    public void messagesAdded(MessageCountEvent event) {
        // Sort messages from recent to oldest
        Message[] messages = event.getMessages();

        for (Message message : messages) {
            // Send message to Discord
            receiveChannel.sendMessage(MailEmbedBuilder.buildMailEmbed(message)).queue();
        }
    }
}
