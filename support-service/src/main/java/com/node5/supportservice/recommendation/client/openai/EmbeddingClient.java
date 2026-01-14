package com.node5.supportservice.recommendation.client.openai;

import com.node5.supportservice.recommendation.exception.RecommendationErrorCode;
import com.node5.supportservice.recommendation.exception.RecommendationException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmbeddingClient {
    private final EmbeddingModel embeddingModel;

    public List<Double> embed(String text) {
        try {
            float[] vector = embeddingModel.embed(text);
            List<Double> embedding = toDoubleList(vector);
            if (embedding.isEmpty()) {
                throw new RecommendationException(RecommendationErrorCode.OPENAI_EMBEDDING_RESPONSE_EMPTY);
            }
            return embedding;
        } catch (RecommendationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RecommendationException(RecommendationErrorCode.OPENAI_EMBEDDING_REQUEST_FAILED);
        }
    }

    private List<Double> toDoubleList(float[] vector) {
        if (vector == null || vector.length == 0) {
            return List.of();
        }
        List<Double> result = new ArrayList<>(vector.length);
        for (float value : vector) {
            result.add((double) value);
        }
        return result;
    }
}
