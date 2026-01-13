package com.node5.supportservice.recommendation.client.openai;

import com.node5.supportservice.recommendation.exception.RecommendationErrorCode;
import com.node5.supportservice.recommendation.exception.RecommendationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatClient {
    private final ChatModel chatModel;

    public String generateRecommendation(String prompt, String systemPrompt) {
        try {
            java.util.List<org.springframework.ai.chat.messages.Message> messages = new java.util.ArrayList<>();
            if (StringUtils.hasText(systemPrompt)) {
                messages.add(new SystemMessage(systemPrompt));
            }
            messages.add(new UserMessage(prompt));

            ChatResponse response = chatModel.call(new Prompt(messages));
            if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                log.warn("OpenAI chat response empty. response={}", response);
                throw new RecommendationException(RecommendationErrorCode.OPENAI_RESPONSE_EMPTY);
            }
            String content = response.getResults().get(0).getOutput().getText();
            if (!StringUtils.hasText(content)) {
                log.warn("OpenAI chat response empty content. response={}", response);
                throw new RecommendationException(RecommendationErrorCode.OPENAI_RESPONSE_EMPTY);
            }
            return content.trim();
        } catch (RecommendationException ex) {
            throw ex;
        } catch (Exception ex) {
            log.warn("OpenAI chat request failed. message={}", ex.getMessage());
            throw new RecommendationException(RecommendationErrorCode.OPENAI_REQUEST_FAILED);
        }
    }
}
