package com.portfolio.smart_inventory.domain.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class GeminiRequest {

    private List<Content> contents;

    @Getter
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;
    }

    @Getter
    @AllArgsConstructor
    public static class Part {
        private String text;
    }

    public static GeminiRequest of(String prompt) {
        return new GeminiRequest(
                List.of(new Content(
                        List.of(new Part(prompt))
                ))
        );
    }
}