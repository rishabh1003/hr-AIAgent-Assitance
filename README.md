# 🤖 HR-Assistance: Enterprise AI HR Agent

[![Java Version](https://img.shields.io/badge/Java-21%20%2B-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.x-blue?logo=spring)](https://spring.io/projects/spring-ai)
[![Protocol](https://img.shields.io/badge/Protocol-MCP-purple?logo=modelcontextprotocol)](https://modelcontextprotocol.io/)
[![Database](https://img.shields.io/badge/Database-PostgreSQL%20%2B%20pgvector-blue?logo=postgresql)](https://www.postgresql.org/)

**HR-Assistance** is a secure, production-ready AI-powered Human Resources agent designed to intelligently handle employee inquiries. Built with a decoupled multi-project architecture, it bridges the gap between unstructured HR policy documents and structured, real-time employee data (like transactional database records) using the **Model Context Protocol (MCP)**.

---

## 🚀 Key Features

* **Advanced RAG Pipeline:** Utilizes PostgreSQL with the `pgvector` extension to store, index, and conduct semantic similarity searches across unstructured employee handbooks and markdown policy files.
* **Live Data Integration via MCP:** Employs an isolated `ASYNC` MCP Server to securely interface with live transactional databases for dynamic employee queries (e.g., *"How much PTO do I have left?"*).
* **Protocol-Level Elicitation Handlers:** Intelligently halts execution using non-blocking reactive chains (`Mono`/`Flux`) when the LLM is missing required parameters (like an employee ID) and dynamically prompts the user for missing details before executing backend database calls.
* **Modular Extensible Tool Registry:** A clean, decoupled tool registry pattern allowing new enterprise capabilities (tools) to be dynamically registered and exposed to the LLM via standard MCP JSON-RPC layers without modifying core agent orchestration logic.
* **Enterprise-Grade Security:** Strict architectural separation of concerns between the internet-facing LLM Orchestration client layer and the protected Core Data execution layer, ensuring sensitive employee data is never exposed unnecessarily.

---

## 📐 Architecture Overview

The system is broken down into distinct modules to ensure high-scale distributed system scalability and runtime security:

* **Agent Orchestrator (`hr-ai-client`):** The brain of the system, powered by Spring AI. It receives user inputs, maintains conversation memory, maps prompt parameters, and decides which MCP tools to invoke.
* **RAG Engine:** Processes semantic search queries against the `pgvector` database to extract relevant company policies and append them into the contextual prompt frame sent to Gemini.
* **MCP Server / Tool Registry (`hr-mcp-server`):** A headless, non-blocking asynchronous module that exposes secure, protected API endpoints. It validates data signatures and executes database lookups before returning live data to the stream.

### System Execution Data Flow

+---------------------------------------+
                  |           Postman / Frontend          |
                  +-------------------+-------------------+
                                      |
                                (HTTP GET / POST)
                                      v
+-----------------------------------------+-----------------------------------------+
| HR-AI-CLIENT (Orchestration Layer)                                                |
|                                                                                   |
|  +--------------------+      +-------------------------+      +----------------+  |
|  |   ChatClient Engine| ---> |  QuestionAnswerAdvisor  | ---> |  pgvector DB   |  |
|  |   (Spring AI)      |      |  (Semantic Search Context|      |  (HR Policies) |  |
|  +---------+----------+      +-------------------------+      +----------------+  |
|            |                                                                      |
+------------|----------------------------------------------------------------------+
| (Bi-directional JSON-RPC over STDIO)
v
+------------|----------------------------------------------------------------------+
| HR-MCP-SERVER (Core Tool Execution Layer)                                         |
|            |                                                                      |
|            v                                                                      |
|  +---------------------------+      +-----------------------+      +-----------+  |
|  | McpAsyncServer Dispatcher | ---> |  ParameterElicitation | ---> | PostgreSQL|  |
|  | (@McpTool Registry)       |      |  (ctx.elicit Workflow)|      | (Employee)|  |
|  +---------------------------+      +-----------------------+      +-----------+  |
+-----------------------------------------------------------------------------------+


### The Request Lifecycle
1. **System Setup:** At boot-up, the client app's `PolicyDataLoader` parses local policy documents, requests 768-dimension vector coordinates from Gemini, and stores them in PostgreSQL. Simultaneously, the client spawns the `hr-mcp-server` JAR as a headless, clean background sub-process over a standard input/output (STDIO) pipe.
2. **Context Assembly (RAG):** When an employee submits a query, the `QuestionAnswerAdvisor` uses vector geometry (Cosine Distance) to find matching rules from your DB context and attaches them to your fixed system prompt.
3. **Execution Decision:** Gemini evaluates the request. If a specific balance lookup is triggered, Gemini instructs the client to invoke the remote MCP server tool. If data parameters (like an employee ID) are missing, the `ASYNC` server uses protocol-level elicitation to pause execution and prompt the user seamlessly.

---

## 🛠️ Tech Stack

* **Backend Framework:** Java 21, Spring Boot 3.x
* **AI Orchestration Engine:** Spring AI (Google GenAI / Gemini Ecosystem)
* **Tooling & Interoperability:** Model Context Protocol (MCP) Java SDK (Version 0.15.0+)
* **Databases:** PostgreSQL 16+ featuring the `pgvector` extension (for vector embeddings)

---

## ⚙️ Configuration Setup

### 1. `hr-ai-client` Properties (`src/main/resources/application.properties`)
```properties
server.port=8090

# Spring AI Model Configurations
spring.ai.google.genai.api-key=YOUR_FRESH_GOOGLE_AI_STUDIO_KEY
spring.ai.google.genai.chat.options.model=gemini-2.0-flash

# Core Safety Override: Disable immediate aggressive retry loops
spring.ai.retry.client.enabled=false

# Vector Store Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/hr_vector_db
spring.datasource.username=postgres
spring.datasource.password=yoursecurepassword
```
### 2. `hr-mcp-server` Properties (`src/main/resources/application.properties`)
```Properties
# Force completely headless execution (No Tomcat port bindings)
spring.main.web-application-type=none
spring.main.banner-mode=off

# Completely blank out console text layouts to prevent JSON-RPC stream corruption
logging.pattern.console=

# Turn on Asynchronous Processing and Discovery Scanner hooks
spring.ai.mcp.server.type=ASYNC
spring.ai.mcp.server.stdio=true
spring.ai.mcp.server.annotation-scanner.enabled=true

# Transactional Employee Database Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/hr_transactional_db
spring.datasource.username=postgres
spring.datasource.password=yoursecurepassword
```


docker run -d \
  --name hr-postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=yoursecurepassword \
  -p 5432:5432 \
  pgvector/pgvector:pg16
