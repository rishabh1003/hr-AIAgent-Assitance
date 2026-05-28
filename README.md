HR-Assistance: Enterprise AI HR Agent
HR-Assitance is a secure, production-ready AI-powered Human Resources agent designed to intelligently handle employee inquiries. Built with a decoupled multi-project architecture, it bridges the gap between unstructured HR policy documents and structured, real-time employee data (like PTO balances) using the Model Context Protocol (MCP).

🚀 Key Features
Advanced RAG Pipeline: Utilizes pgvector to store, index, and retrieve unstructured HR policy documents and employee handbooks.

Live Data Integration via MCP: Employs the Model Context Protocol to securely interface with live PostgreSQL databases for dynamic employee queries (e.g., "How much PTO do I have left?").

Client-Side Elicitation Handlers: Intelligently detects when an AI model is missing necessary context (like an employee ID or specific date) and dynamically prompts the user for missing information before executing the tool.

Modular Tool Registry: Highly scalable and decoupled tool registry pattern, allowing new HR capabilities (tools) to be registered and exposed to the LLM without modifying core agent logic.

Enterprise-Grade Security: Strict separation of concerns between the LLM orchestration layer and the data execution layer, ensuring sensitive employee data is never exposed unnecessarily.

🛠️ Tech Stack
Backend Framework: Java, Spring Boot 3.x

AI Integration: Spring AI, OpenAI API (or local LLM alternatives)

Tooling/Interoperability: Model Context Protocol (MCP)

Databases: PostgreSQL (Relational employee data), pgvector (Vector embeddings for RAG)

Architecture: Multi-module Maven/Gradle setup, Event-driven integrations

📐 Architecture Overview
The system is broken down into distinct modules to ensure scalability and security:

Agent Orchestrator: The brain of the system, powered by Spring AI. It receives user inputs, maintains conversation memory, and decides which MCP tools to invoke.

RAG Engine: Processes semantic search queries against the pgvector database to retrieve relevant company policies.

MCP Server / Tool Registry: Exposes secure API endpoints that perform actual database operations. It validates permissions before returning live data.

Elicitation Gateway: Intercepts LLM tool-call requests. If the LLM lacks required parameters, the gateway halts execution and asks the frontend to elicit the missing data from the user.

⚙️ Getting Started
Prerequisites
Java 17 or higher

Docker (for running PostgreSQL & pgvector)

An OpenAI API Key (or equivalent LLM provider)
