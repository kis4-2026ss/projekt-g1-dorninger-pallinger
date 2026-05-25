import os
import json
import subprocess
import argparse
import re
import shlex
import shutil
from datetime import datetime
from typing import List, Dict, Tuple, Optional

class MigrationOrchestrator:
    """
    Orchestrates the migration of Java Swing projects to JavaFX using AI.
    """

    def __init__(self, project_name: str):
        self.project_name = project_name
        self.source_dir = f"SampleProjects/{project_name}/src"
        self.target_project_dir = f"ResultProjects/{project_name}"
        self.target_src_dir = f"{self.target_project_dir}/src/main/java"
        
        # State tracking files
        self.in_progress_plan_path = f"agent_state/migration_plans/{project_name}_in_progress.json"
        self.completed_plan_path = f"agent_state/migration_plans/{project_name}_completed.json"
        self.current_plan_file = self.in_progress_plan_path

    def execute_migration_workflow(self):
        """
        Executes the end-to-end migration process.
        """
        self._print_banner()
        self._check_environment()
        
        migration_sequence = self._prepare_migration_plan()

        # Step 2: Transformation Phase
        self._run_transformation_phase(migration_sequence)

        # Step 3: Validation Phase
        self._run_validation_phase()
        
        print(f"\n{'='*60}")
        print(f"✅ Migration of '{self.project_name}' successfully completed!")
        print(f"{'='*60}\n")

    def _check_environment(self):
        """Ensures necessary tools are installed."""
        rc, _, _ = self._execute_shell_command("mvn -version")
        if rc != 0:
            print("⚠️ Warning: Maven (mvn) is not installed or not in PATH. Build phase will fail.")

    def _print_banner(self):
        print(f"\n{'='*60}")
        print(f"🚀 Starting Migration for Project: {self.project_name}")
        print(f"{'='*60}")

    def _prepare_migration_plan(self) -> List[str]:
        """
        Determines if we should resume or start a new migration.
        Returns the sequence of files to migrate.
        """
        if os.path.exists(self.completed_plan_path):
            print(f"🏁 Project '{self.project_name}' was already migrated. Restarting fresh...")
            self._cleanup_previous_results()
        elif os.path.exists(self.in_progress_plan_path):
            print(f"♻️  Found existing plan. Resuming migration...\n")
            return self._load_current_plan()["migration_sequence"]

        # Fresh start: Analysis
        self._cleanup_previous_results()
        self._run_dependency_analysis()
        
        # AI Review for cycles
        migration_sequence = self._run_ai_plan_review()
        self._initialize_new_plan(migration_sequence)
        
        return migration_sequence

    def _run_dependency_analysis(self):
        print(f"\n--- Phase 1: Dependency Analysis ---")
        analysis_cmd = f"python3 scripts/analyze_dependencies.py {self.source_dir}"
        return_code, stdout, stderr = self._execute_shell_command(analysis_cmd)
        
        if stdout:
            print(stdout.strip())
        
        if return_code != 0:
            raise RuntimeError(f"Analysis failed: {stderr}")
        print("✅ Analysis phase completed.")

    def _run_ai_plan_review(self) -> List[str]:
        """
        Uses AI to resolve circular dependencies if found by the static analyzer.
        """
        print(f"\n--- Phase 1.5: AI Plan Review ---")
        plan = self._load_current_plan()
        initial_sequence = plan["migration_sequence"]
        cycles = plan.get("circular_dependencies", [])
        
        if not cycles:
            print("✅ No cycles detected. Skipping AI review.")
            return initial_sequence

        print(f"⚠️ Circular dependencies found: {cycles}")
        print("🤖 Asking Gemini to optimize migration order...")
        
        context = self._collect_file_context(initial_sequence)
        prompt = (
            f"The Java project has circular dependencies: {cycles}.\n"
            f"Initial sequence: {initial_sequence}.\n"
            f"File Context:\n{context}\n"
            "Task: Determine the best migration order to JavaFX. Return ONLY a JSON list of file paths."
        )
        
        rc, stdout, _ = self._call_gemini_ai(prompt)
        if rc == 0:
            match = re.search(r'\[.*\]', stdout.strip(), re.DOTALL)
            if match:
                return json.loads(match.group(0))
        
        return initial_sequence

    def _collect_file_context(self, file_list: List[str]) -> str:
        snippets = []
        for java_file in file_list:
            path = os.path.join(self.source_dir, java_file)
            if os.path.exists(path):
                with open(path, 'r', encoding='utf-8') as f:
                    content = f.read()
                    snippet = "\n".join(content.splitlines()[:30])
                    snippets.append(f"File: {java_file}\n{snippet}\n")
        return "---".join(snippets)

    def _run_transformation_phase(self, migration_sequence: List[str]):
        print(f"\n--- Phase 2: Transformation (Swing -> JavaFX) ---")
        os.makedirs(self.target_src_dir, exist_ok=True)
        self._ensure_maven_setup()

        total_files = len(migration_sequence)
        for index, java_file in enumerate(migration_sequence, 1):
            if self._is_file_already_migrated(java_file):
                print(f"[{index}/{total_files}] ⏭️  Skipping {java_file} (already completed)")
                continue

            self._migrate_single_file(java_file, index, total_files)

    def _migrate_single_file(self, java_file: str, index: int, total: int):
        print(f"\n[{index}/{total}] 🛠️  Migrating: {java_file} ...")
        source_path = os.path.join(self.source_dir, java_file)
        target_path = os.path.join(self.target_src_dir, java_file)
        
        os.makedirs(os.path.dirname(target_path), exist_ok=True)
        
        prompt = (
            f"Migrate the Swing class '{source_path}' to JavaFX. "
            f"Save result to '{target_path}'.\n"
            "Rules: Use JavaFX, modern syntax (lambdas), keep package declaration, do not change logic."
        )
        
        rc, _, stderr = self._call_gemini_ai(prompt)
        if rc == 0:
            print(f"✅ {java_file} migrated.")
            self._update_file_status(java_file, "completed")
        else:
            print(f"❌ Error migrating {java_file}: {stderr}")
            self._update_file_status(java_file, "failed")

    def _run_validation_phase(self):
        print(f"\n--- Phase 3: Validation & Build ---")
        max_fix_attempts = 3
        
        for attempt in range(1, max_fix_attempts + 2):
            build_cmd = f"mvn -f {self.target_project_dir}/pom.xml compile"
            rc, stdout, stderr = self._execute_shell_command(build_cmd)
            
            if rc == 0:
                print("✅ Build successful!")
                self._finalize_migration_status("Completed")
                return
            
            if attempt <= max_fix_attempts:
                print(f"❌ Build failed (Attempt {attempt}/{max_fix_attempts}). Invoking AI Auto-Fix...")
                self._apply_ai_build_fix(stdout + "\n" + stderr)
            else:
                print("🛑 Max retry attempts reached for build fixes.")
                self._finalize_migration_status("Build Failed")

    def _apply_ai_build_fix(self, error_log: str):
        error_info = self._extract_error_details(error_log)
        prompt = (
            f"The build for '{self.target_project_dir}' failed.\n"
            f"Errors: {error_log[:1000]}\n{error_info}\n"
            "Fix the code to resolve these build errors."
        )
        rc, _, stderr = self._call_gemini_ai(prompt)
        if rc != 0:
            print(f"⚠️ Warning: AI build fix command failed: {stderr}")

    def _extract_error_details(self, log: str) -> str:
        match = re.search(r'(/[^ ]+\.java):\[(\d+),\d+\] error:', log)
        if match:
            file_path, line_num = match.group(1), match.group(2)
            if os.path.exists(file_path):
                with open(file_path, 'r', encoding='utf-8') as f:
                    return f"\nFile: {file_path} (Line {line_num})\nContent:\n{f.read()}"
        return ""

    # --- Utility & State Management ---

    def _execute_shell_command(self, cmd: str) -> Tuple[int, str, str]:
        print(f"CMD: {cmd}")
        process = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        stdout, stderr = process.communicate()
        return process.returncode, stdout, stderr

    def _call_gemini_ai(self, prompt: str) -> Tuple[int, str, str]:
        return self._execute_shell_command(f"gemini --prompt {shlex.quote(prompt)} --yolo")

    def _cleanup_previous_results(self):
        if os.path.exists(self.target_project_dir):
            shutil.rmtree(self.target_project_dir)
        for plan in [self.in_progress_plan_path, self.completed_plan_path]:
            if os.path.exists(plan):
                os.remove(plan)
        os.makedirs(os.path.dirname(self.in_progress_plan_path), exist_ok=True)

    def _ensure_maven_setup(self):
        pom_path = f"{self.target_project_dir}/pom.xml"
        if not os.path.exists(pom_path):
            os.makedirs(os.path.dirname(pom_path), exist_ok=True)
            plan = self._load_current_plan()
            maven_deps = plan.get("maven_dependencies", [])
            with open(pom_path, "w", encoding='utf-8') as f:
                f.write(self._get_default_pom_content(maven_deps))

    def _get_default_pom_content(self, additional_deps: List[Dict] = None) -> str:
        dep_xml = ""
        if additional_deps:
            for dep in additional_deps:
                dep_xml += f"""
        <dependency>
            <groupId>{dep.get('groupId')}</groupId>
            <artifactId>{dep.get('artifactId')}</artifactId>
            <version>{dep.get('version', 'LATEST')}</version>
        </dependency>"""

        return f"""<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>{self.project_name}</artifactId>
    <version>1.0</version>
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>21.0.1</version>
        </dependency>{dep_xml}
    </dependencies>
</project>"""

    def _initialize_new_plan(self, sequence: List[str]):
        state = {
            "project": self.project_name,
            "status": "In Progress",
            "migration_sequence": sequence,
            "file_status": {f: "pending" for f in sequence},
            "history": []
        }
        self._save_plan(state)

    def _update_file_status(self, filename: str, status: str):
        state = self._load_current_plan()
        state["file_status"][filename] = status
        state["history"].append(f"{datetime.now().isoformat()}: {filename} -> {status}")
        self._save_plan(state)

    def _is_file_already_migrated(self, filename: str) -> bool:
        plan = self._load_current_plan()
        return plan.get("file_status", {}).get(filename) == "completed"

    def _finalize_migration_status(self, status: str):
        state = self._load_current_plan()
        state["status"] = status
        self._save_plan(state)
        
        if status == "Completed" and os.path.exists(self.in_progress_plan_path):
            if os.path.exists(self.completed_plan_path):
                os.remove(self.completed_plan_path)
            os.rename(self.in_progress_plan_path, self.completed_plan_path)
            self.current_plan_file = self.completed_plan_path

    def _load_current_plan(self) -> Dict:
        path = self.in_progress_plan_path if os.path.exists(self.in_progress_plan_path) else self.completed_plan_path
        with open(path, "r", encoding='utf-8') as f:
            return json.load(f)

    def _save_plan(self, state: Dict):
        with open(self.current_plan_file, "w", encoding='utf-8') as f:
            json.dump(state, f, indent=4)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Migrate Java Swing projects to JavaFX.")
    parser.add_argument("project", help="Name of the project folder in SampleProjects")
    args = parser.parse_args()
    
    orchestrator = MigrationOrchestrator(args.project)
    orchestrator.execute_migration_workflow()
