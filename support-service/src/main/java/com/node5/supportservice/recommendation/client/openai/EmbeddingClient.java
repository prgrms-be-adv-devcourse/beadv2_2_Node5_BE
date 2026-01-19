package com.node5.supportservice.recommendation.client.openai;

import com.node5.supportservice.recommendation.exception.RecommendationErrorCode;
import com.node5.supportservice.recommendation.exception.RecommendationException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmbeddingClient {
    private final EmbeddingModel embeddingModel;

    public float[] embed(String text) {
        try {
            float[] vector = embeddingModel.embed(text);
            if (vector.length == 0) {
                throw new RecommendationException(RecommendationErrorCode.OPENAI_EMBEDDING_RESPONSE_EMPTY);
            }
            return vector;
        } catch (RecommendationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RecommendationException(RecommendationErrorCode.OPENAI_EMBEDDING_REQUEST_FAILED);
        }
    }
}
