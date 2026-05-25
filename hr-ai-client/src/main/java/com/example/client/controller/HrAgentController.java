package com.example.client.controller;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HrAgentController {
    private final ChatClient chatClient;

    public HrAgentController(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder
                // Give the AI access to the RAG memory (Vector DB)
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().build()))
                // Give the AI access to the MCP hands (SQL DB)
                .defaultFunctions("get_pto_balance")
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
