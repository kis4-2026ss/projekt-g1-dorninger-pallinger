# Gemini Migration Rules (Automated Workflow)

PROPOSAL.md and README.md must not be modified.

Main goal:
Develop and execute an AI-assisted automated workflow for migrating Java Swing to JavaFX.

Required Workflow (Automated):
1. **Analysis Phase:**
   - Run `scripts/analyze_dependencies.py [SRC_DIR]` to generate an initial plan in `agent_state/migration_plans/[PROJECT]_in_progress.json`.
   - **AI Review:** The migration script automatically invokes Gemini to double-check and optimize the migration sequence before execution.

2. **Transformation Phase:**
   - Use `agent_state/migration_plans/[PROJECT]_in_progress.json` as the primary source of truth.
   - The migration script (`scripts/migrate.py`) will automatically initialize and update this JSON with execution state.
   - Iterate through the `migration_order` defined in the JSON.
   - For each class, transform from Swing to JavaFX.
   - Target Directory: `ResultProjects/[PROJECT]/src/main/java`.
   - The script updates `status` and `history` in the plan JSON in real-time.
   - Once completed, the plan is renamed to `[PROJECT]_completed.json`.

3. **Validation & Error-Handling Phase:**
   - Execute the project-specific build command defined in the JSON.
   - If build fails, capture logs and feed them back to Gemini for automated fixes.

4. **Evaluation:**
   - Update `Evaluation/ProjectEvaluation.md` after each project run.

Rules:
- Focus on automation: Prefer updating `agent_state/agent_state.json` over manual Markdown updates.
- Scripts and instructions must work for any Java Swing project provided.