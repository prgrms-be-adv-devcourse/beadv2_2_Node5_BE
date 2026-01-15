package com.node5.supportservice.chat;

import com.node5.supportservice.chat.exception.ChatErrorCode;
import com.node5.supportservice.chat.exception.ChatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatModel chatModel;

    public String callLlm(String systemPrompt, String userPrompt, String domain) {
        try {
            List<Message> messages = new ArrayList<>();
            if (StringUtils.hasText(systemPrompt)) {
                messages.add(new SystemMessage(systemPrompt));
            }
            messages.add(new UserMessage(userPrompt));

            ChatResponse response = chatModel.call(new Prompt(messages));
            if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                log.warn("[{}] OpenAI chat response empty. response={}", domain, response);
                throw new ChatException(ChatErrorCode.OPENAI_RESPONSE_EMPTY);
            }

            String content = response.getResults().get(0).getOutput().getText();
            if (!StringUtils.hasText(content)) {
                log.warn("[{}] OpenAI chat response empty content. response={}", domain, response);
                throw new ChatException(ChatErrorCode.OPENAI_RESPONSE_EMPTY);
            }
            return content.trim();
        } catch (Exception ex) {
            log.warn("[{}] OpenAI chat request failed. message={}", domain, ex.getMessage());
            throw new ChatException(ChatErrorCode.OPENAI_REQUEST_FAILED);
        }
    }

}
