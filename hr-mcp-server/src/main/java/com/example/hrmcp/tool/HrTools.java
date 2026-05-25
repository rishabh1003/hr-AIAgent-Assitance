package com.example.hrmcp.tool;



import com.example.hrmcp.repository.EmployeeRepository;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springaicommunity.mcp.context.McpAsyncRequestContext;
import reactor.core.publisher.Mono;

@Service
public class HrTools {
    private final EmployeeRepository repository;


    public record MissingData(String employeeId) {}

    public HrTools(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "get_pto_balance", description = "Retrieves the live PTO (Paid Time Off) balance in days for a specific employee ID.")
    public Mono<String> getPtoBalance(
            @ToolParam(description = "The exact Employee ID (e.g., 'EMP-992')", required = false) String employeeId, 
            McpAsyncRequestContext ctx) {
        
        // PATH A: The LLM already has the ID (No elicitation needed)
        if (employeeId != null && !employeeId.isBlank()) {
            return Mono.just(fetchFromDatabase(employeeId));
        }

        // PATH B: ID is missing, but the Client UI doesn't support pausing for input
        // FIX: elicitEnabled() is a boolean, not an object.
        if (ctx.elicitEnabled()==null) {
            return Mono.just("Error: Missing Employee ID. Please ask the user to provide their exact Employee ID.");
        }

        // PATH C: Elicitation - Pause the server and ask the user for the missing ID
        // FIX: ctx.elicit returns a Mono. We must use .map() to handle the result once the user replies.
        return ctx.elicit(
                e -> e.message("Let's complete the request. Please provide your Employee ID:"),
                MissingData.class
        ).map(result -> {
            
            // 2. Safely check if the user cancelled or declined the prompt
            if (result.action() != McpSchema.ElicitResult.Action.ACCEPT) {
                return "Request cancelled: No Employee ID was provided.";
            }

            // 3. Extract the clean data from our record and fetch from the database
            MissingData data = result.structuredContent();
            if (data != null && data.employeeId() != null) {
                return fetchFromDatabase(data.employeeId());
            } else {
                return "Error: Invalid data received.";
            }
        });
    }

    private String fetchFromDatabase(String id) {
        return repository.findByEmployeeId(id)
                .map(emp -> "PTO Balance for " + id + " is " + emp.getPtoBalance() + " days.")
                .orElse("Error: Employee ID not found in the database.");
    }
}