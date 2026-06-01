package com.example.client.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HrAgentController {
    
    private final ChatClient chatClient;

    public HrAgentController(ChatClient.Builder builder, VectorStore vectorStore, ToolCallbackProvider mcpTools) {
        this.chatClient = builder
                // 1. Cleaned up the Advisor construction to use robust framework defaults
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                
                // 2. FIXED: Pass the 'mcpTools' bean directly to defaultToolCallbacks!
                .defaultToolCallbacks(mcpTools)         
                .build();
    }

    @GetMapping("/ask-hr")
    public String askAgent(@RequestParam @NonNull String question) {
        return chatClient.prompt()
                .system("You are a helpful HR Assistant. Answer employee questions using the provided context and tools.")
                .user(question)
                .call()
                .content();
    }
}