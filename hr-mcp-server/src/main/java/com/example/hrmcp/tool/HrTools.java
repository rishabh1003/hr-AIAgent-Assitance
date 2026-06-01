package com.example.hrmcp.tool;

import com.example.hrmcp.repository.EmployeeRepository;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.stereotype.Service;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springaicommunity.mcp.context.McpAsyncRequestContext;
import reactor.core.publisher.Mono;

@Service
public class HrTools {
    
    private final EmployeeRepository repository;

    // Static inner record representing the expected structure for missing parameters
    public record MissingData(String employeeId) {}

    public HrTools(EmployeeRepository repository) {
        this.repository = repository;
    }

    @McpTool(name = "get_pto_balance", description = "Retrieves the live PTO (Paid Time Off) balance in days for a specific employee ID.")
    public Mono<String> getPtoBalance(
            @McpToolParam(description = "The exact Employee ID (e.g., 'EMP-992')", required = false) String employeeId,
            McpAsyncRequestContext ctx) {

        // PATH A: The LLM already has the ID (No elicitation needed)
        if (employeeId != null && !employeeId.isBlank()) {
            return Mono.just(fetchFromDatabase(employeeId));
        }

        // PATH B & C: Check if elicitation is enabled reactively
        return ctx.elicitEnabled().flatMap(isEnabled -> {

            if (!isEnabled) {
                return Mono.just("Error: Missing Employee ID. Please ask the user to provide their exact Employee ID.");
            }

            // PATH C: Elicitation - Request the data structure reactively
            return ctx.elicit(
                    e -> e.message("Let's complete the request. Please provide your Employee ID:"),
                    MissingData.class
            ).map(result -> {

                // Safely assert protocol schema acceptance
                if (result.action() != McpSchema.ElicitResult.Action.ACCEPT) {
                    return "Request cancelled: No Employee ID was provided.";
                }

                // Extract structural container safely
                MissingData data = result.structuredContent();
                if (data != null && data.employeeId() != null) {
                    return fetchFromDatabase(data.employeeId());
                } else {
                    return "Error: Invalid data received.";
                }
            });
        });
    }

    /**
     * FIXED: Converted to return Mono<String> to comply with the ASYNC server scanner topology.
     */
    @McpTool(name = "ping_test", description = "A simple test tool to check if tools are registering.")
    public Mono<String> pingTest() {
        return Mono.just("The tool system is working!");
    }

    private String fetchFromDatabase(String id) {
        // Ensure this method safely handles whatever type your JPA repository returns (Entity or String)
        Object result = repository.findByEmployeeId(id);
        return result != null ? result.toString() : "No employee found with ID: " + id;
    }
}