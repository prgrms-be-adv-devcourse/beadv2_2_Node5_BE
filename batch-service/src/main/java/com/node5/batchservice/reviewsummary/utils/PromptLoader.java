package com.node5.batchservice.reviewsummary.utils;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@Component
public class PromptLoader {
    public String load(String name) {
        try (InputStream is =
                     getClass().getClassLoader()
                             .getResourceAsStream("prompts/" + name)) {

            if (is == null) {
                throw new IllegalArgumentException("Prompt not found: " + name);
            }

            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
