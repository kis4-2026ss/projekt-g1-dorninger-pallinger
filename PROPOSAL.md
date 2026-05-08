# AI-Assisted Java Legacy Migration Agent

## Project Proposal

---

# Goal of the Project

## High-Level Goal

The goal of the project is to migrate legacy Java Swing Projects to a newer Java version using JavaFX.
It should handle smaller and medium sized projects.
In addition the workflow should avoid the problem of exceeding quota limit.
---

## Validation of the Project

The success of the migration will be validated using several criteria:

### Functional Validation

* The application should still compile and run successfully

### Technical Validation

* Deprecated APIs should be replaced
* Modern Java language features should be introduced where appropriate
* Dependencies should be updated
  
### Modernization Validation

Examples:

* Migration from Java Swing to JavaFX
* Verification whether FXML is used in migrated JavaFX GUIs
* Usage of newer Java features and patterns such as:
  * Lambdas
  * Streams API
  * try-with-resources
  * `java.time`
  * patterns (for instance MVC) or newer syntax features (if applicable)

---

# System / Workflow to be Developed

## Overview

The project will develop and evaluate an AI-assisted migration workflow using a Gemini-based agent.

The agent receives old Java projects and incrementally migrates them to newer Java versions.

---

## Planned Workflow

GitHub Repository
        ↓
Scheduled GitHub Action (e.g. every 6 hours)
        ↓
Agent selects the next small migration task
        ↓
Gemini Flash is invoked
        ↓
Code is modified
        ↓
Compile/tests are executed
        ↓
Progress is committed and pushed
        ↓
Workflow terminates

---

## Token and Quota Handling

Since the project relies on free-tier AI usage with limited token budgets, the workflow is designed to tolerate interruptions and continue later.

### Planned Strategy

1. Set task status to `in_progress`
2. Invoke Gemini
3. If token/quota errors occur:

   * do not commit incomplete changes
   * set task status to:

     * `blocked_rate_limit`
     * or `retry_later`
   * save logs and progress information
   * terminate workflow safely
4. The next scheduled GitHub Action run resumes the same task

---

## Alternative Safe Workflow

```text
1. Create branch
2. Modify file
3. Execute compile/tests
4. Only if successful:
    - commit changes
    - mark task as "done"
5. If tokens run out:
    - discard changes
    - or save work-in-progress branch
    - keep task open
```

---

## Task Granularity

To reduce token usage and improve robustness, migration tasks will be intentionally small.

Examples:

* migrate a single class
* fix one compiler error
* update one dependency
* modernize one test file

After each task:

* compilation is executed
* optionally unit tests are executed
* successful changes are committed immediately

Integration tests are executed after larger migration milestones.

---

# AI Assistance in the Development Process

## Role of AI

The AI system is responsible for performing code migration tasks automatically.

Main responsibilities:

* analyzing old Java code
* modernizing syntax and APIs
* updating dependencies
* fixing compiler issues
* adapting tests
* generating migration patches

---

## AI Models and Tools

### AI Model

* Gemini 3.0 Flash / Fast Agent

### Supporting Infrastructure

* GitHub Repository
* GitHub Actions
* Maven or Gradle build system
* Java compiler and test framework
* Git branches and commits for checkpointing

---

# Development / Architecture Diagram

## Conceptual Architecture

```text
                +----------------------+
                |   Human Developer    |
                +----------+-----------+
                           |
                           | prompts / supervision
                           v
                +----------------------+
                |      AI Agent        |
                |  (Gemini Flash)      |
                +----------+-----------+
                           |
        +------------------+------------------+
        |                                     |
        v                                     v
+---------------+                  +-------------------+
| Legacy Java   |                  | Build/Test System |
| SE 8 Project  |                  | Maven / Gradle    |
+---------------+                  +-------------------+
        |
        v
+----------------------+
| Migrated Java 17/25  |
| LTS Project           |
+----------------------+
```

---

# Project Plan

## Timeline

| Date  | Milestone                                 |
| ----- | ----------------------------------------- |
| 13.05 | Submission of project proposal            |
| 20.05 | Find and analyze suitable legacy projects |
| 27.06 | Perform migration of code bases           |
| 29.06 | Evaluate results and document findings    |

---

## Planned Project Phases

### Phase 1 — Research & Analysis

* Identify suitable Java legacy projects
* Analyze build systems and dependencies
* Estimate migration complexity

### Phase 2 — Workflow Implementation

* Implement GitHub Action workflow
* Implement task management
* Integrate Gemini API

### Phase 3 — AI-Assisted Migration

* Execute incremental migrations
* Evaluate token usage and limitations
* Measure migration success

### Phase 4 — Evaluation & Documentation

* Compare before/after code bases
* Analyze AI-generated changes
* Evaluate migration quality and stability
* Document findings and limitations

---

# Teamwork and Responsibilities

## Planned Responsibilities

Possible responsibilities include:

| Area                       | Responsibility                |
| -------------------------- | ----------------------------- |
| Legacy project research    | Identify suitable code bases  |
| AI workflow implementation | GitHub Actions + automation   |
| Migration evaluation       | Analyze migration quality     |
| Testing & validation       | Execute compile/tests         |
| Documentation              | Final report and presentation |

---

# Open Questions / Research Topics

## Legacy Code Bases

* Where can sufficiently large Java legacy projects be found?
* Which projects are realistic for migration within the project timeframe?

Possible sources:

* GitHub open-source projects
* Old Swing desktop applications
* Archived university/demo projects

---

## GUI Migration Research

* Which GUI technologies are currently common?
* Which GUI migrations are realistic?

  * Swing → JavaFX
  * JavaFX modernization
  * Usage of FXML
* Are automated GUI migrations feasible using AI assistance?

---

# Expected Outcome

The project aims to evaluate:

* how effectively AI can modernize legacy Java projects
* how robust AI-assisted migration workflows are under token and quota limitations
* whether incremental autonomous migration is feasible using free-tier AI infrastructure

Additionally, the project should provide insights into:

* limitations of current AI-assisted software engineering
* optimal task granularity for code migration
* practical workflow designs for long-running AI coding agents
