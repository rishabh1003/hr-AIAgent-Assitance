package com.example.hrmcp.config;


import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.hrmcp.tool.HrTools;

@Configuration
public class MCPConfig {

    private final HrTools hrTool;

    public MCPConfig(HrTools hrTool) {
        this.hrTool = hrTool;
    }

    @Bean
    ToolCallbackProvider hrTool() {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(this.hrTool)
                .build();
    }

}