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

//    @Tool(name = "get_pto_balance", description = "Retrieves the live PTO (Paid Time Off) balance in days for a specific employee ID.")
// public Mono<String> getPtoBalance(
//         @ToolParam(description = "The exact Employee ID (e.g., 'EMP-992')", required = false) String employeeId, 
//         McpAsyncRequestContext ctx) {
    
//     // PATH A: The LLM already has the ID (No elicitation needed)
//     if (employeeId != null && !employeeId.isBlank()) {
//         return Mono.just(fetchFromDatabase(employeeId));
//     }

//     // PATH B & C: Check if elicitation is enabled reactively
//     return ctx.elicitEnabled().flatMap(isEnabled -> {
        
//         // Unwrap the Mono: 'isEnabled' is now a standard primitive boolean
//         if (!isEnabled) {
//             return Mono.just("Error: Missing Employee ID. Please ask the user to provide their exact Employee ID.");
//         }

//         // PATH C: Elicitation - Pause the server and ask the user for the missing ID
//         return ctx.elicit(
//                 e -> e.message("Let's complete the request. Please provide your Employee ID:"),
//                 MissingData.class
//         ).map(result -> {
            
//             // Safely check if the user cancelled or declined the prompt
//             if (result.action() != McpSchema.ElicitResult.Action.ACCEPT) {
//                 return "Request cancelled: No Employee ID was provided.";
//             }

//             // Extract the clean data from our record and fetch from the database
//             MissingData data = result.structuredContent();
//             if (data != null && data.employeeId() != null) {
//                 return fetchFromDatabase(data.employeeId());
//             } else {
//                 return "Error: Invalid data received.";
//             }
//         });
//     });
// }
 
@Tool(name = "ping_test", description = "A simple test tool to check if tools are registering.")
public String pingTest() {
    return "The tool system is working!";
}

// private String fetchFromDatabase(String id) {
//         return "return database response";
//     }
}