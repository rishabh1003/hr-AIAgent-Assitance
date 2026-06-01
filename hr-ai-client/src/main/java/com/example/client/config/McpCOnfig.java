package com.example.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpAsyncClient; // 1. Changed to McpAsyncClient
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;

import org.springframework.ai.mcp.AsyncMcpToolCallbackProvider; // 2. Changed to Async wrapper
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class McpCOnfig {

    /**
     * 1. Build the McpAsyncClient explicitly using non-blocking transport hooks.
     */
    @Bean(initMethod = "initialize", destroyMethod = "close")
    public McpAsyncClient mcpClient() {
        
        // Define how to start your Java MCP server process
        ServerParameters params = ServerParameters.builder("java")
                .args(
                    "-jar", 
                    "C:\\Users\\Rishabh.Gupta1\\Desktop\\aiAgent\\hr-workspace\\hr-mcp-server\\target\\hr-mcp-server-0.0.1-SNAPSHOT.jar"
                )
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        @SuppressWarnings("removal")
        JacksonMcpJsonMapper jsonMapper = new JacksonMcpJsonMapper(objectMapper);
        
        StdioClientTransport transport = new StdioClientTransport(params, jsonMapper);

        // FIXED: Swapped out .sync(transport) for .async(transport)
        return McpClient.async(transport)
                .requestTimeout(Duration.ofSeconds(20))
                .build();
    }

    /**
     * 2. Wrap the asynchronous client into the ToolCallbackProvider for your Controller.
     */
    @Bean
    public ToolCallbackProvider mcpTools(McpAsyncClient mcpClient) {
        // FIXED: Using AsyncMcpToolCallbackProvider to wrap our reactive client bean
        return new AsyncMcpToolCallbackProvider(mcpClient);
    }
}