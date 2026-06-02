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
                // 1. Set the global Fixed System Prompt rules upon initialization
                .defaultSystem("""
                    You are an elite, highly professional Corporate HR AI Assistant.
                    Your objective is to provide accurate, helpful, and concise information regarding company policies and personal employee balances.
                    
                    CRITICAL INSTRUCTIONS & EXECUTION WORKFLOW:
                    1. FOR GENERAL POLICY INQUIRIES (e.g., leave guidelines, eligibility rules):
                       - Rely entirely on the corporate context retrieved automatically from the Vector Database.
                       - Base your answers strictly on the facts provided in that context.
                       
                    2. FOR LIVE EMPLOYEE DATA & BALANCE LOOKUPS (e.g., tracking remaining PTO):
                       - You MUST execute the 'get_pto_balance' tool. Never guess or hallucinate an employee's personal balance.
                       
                    3. PARAMETER ELICITATION RULES:
                       - If a query requires an Employee ID (e.g., looking up a PTO balance) and the user has not provided it, do not fail.
                       - Invoke the parameter elicitation workflow immediately to prompt the user directly for their missing Employee ID.
                    
                    4. TONE & PRIVACY:
                       - Maintain a polite, crisp, corporate tone.
                       - Keep responses structured, easy to read, and secure.
                    """)
                // 2. Attach your RAG vector advisor
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                // 3. Attach your asynchronous MCP tool provider
                .defaultToolCallbacks(mcpTools)         
                .build();
    }

    @GetMapping("/ask-hr")
    public String askAgent(@RequestParam @NonNull String question) {
        return chatClient.prompt()
                // The fixed rules are now handled at the bean default level. 
                // Passing the user prompt here smoothly merges everything into the model payload.
                .user(question)
                .call()
                .content();
    }
}