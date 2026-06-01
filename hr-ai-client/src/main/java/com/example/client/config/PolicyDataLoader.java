package com.example.client.config;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;


@Component
public class PolicyDataLoader implements CommandLineRunner {
    
    private final VectorStore vectorStore;

    public PolicyDataLoader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Ingesting HR Policies into Vector Database ---");
        
        Document policy = new Document(
            "Fathers are entitled to 4 weeks (20 business days) of paid paternity leave. " +
            "This leave can be taken consecutively or split, provided the employee has enough PTO.",
            Map.of("category", "leave_policy", "year", "2025")
        );
        
        vectorStore.accept(List.of(policy));
        System.out.println("--- Ingestion Complete ---");
    }
}
