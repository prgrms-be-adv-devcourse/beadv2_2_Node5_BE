package com.node5.supportservice.reviewsummary.client;

import com.node5.supportservice.reviewsummary.exception.ReviewSummaryErrorCode;
import com.node5.supportservice.reviewsummary.exception.ReviewSummaryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LLMChatClient {
    private final ChatModel chatModel;

    public String reviewSummary(String systemPrompt) {
        try {
            List<Message> messages = new ArrayList<>();
            if (StringUtils.hasText(systemPrompt)) {
                messages.add(new SystemMessage(systemPrompt));
            }
            messages.add(new UserMessage("요약을 생성하라."));

            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .maxCompletionTokens(200)
                    .temperature(0.2)
                    .build();

            ChatResponse response = chatModel.call(new Prompt(messages, options));
            if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                log.warn("OpenAI chat response empty. response={}", response);
                throw new ReviewSummaryException(ReviewSummaryErrorCode.OPENAI_RESPONSE_EMPTY);
            }
            String content = response.getResults().get(0).getOutput().getText();
            if (!StringUtils.hasText(content)) {
                log.warn("OpenAI chat response empty content. response={}", response);
                throw new ReviewSummaryException(ReviewSummaryErrorCode.OPENAI_RESPONSE_EMPTY);
            }
            return content.trim();
        } catch (ReviewSummaryException ex) {
            throw ex;
        }
        catch (Exception ex) {
            log.warn("OpenAI chat request failed. message={}", ex.getMessage());
            throw new ReviewSummaryException(ReviewSummaryErrorCode.OPENAI_REQUEST_FAILED);
        }
    }
}
