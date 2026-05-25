import os
import json
import argparse
import javalang
import subprocess
import shlex
import re
from typing import List, Dict, Optional, Set, Tuple

class DependencyAnalyzer:
    """
    Analyzes Java source files to determine their dependency graph
    and suggest an optimal migration order.
    """

    def __init__(self, source_directory: str):
        self.source_directory = os.path.abspath(source_directory)
        self.file_metadata = {}  # { rel_path: { "class_name": ..., "dependencies": ... } }
        self.class_to_file_map = {}  # { class_name: rel_path }

    def run_analysis(self) -> Optional[Dict]:
        """
        Executes the full project analysis workflow.
        """
        if not os.path.exists(self.source_directory):
            print(f"Error: Directory {self.source_directory} does not exist.")
            return None

        print(f"🔍 Scanning directory: {self.source_directory}")
        java_files = self._discover_java_files()
        print(f"📂 Found {len(java_files)} Java files.")

        print("🏗️  Parsing dependencies (AST)...")
        for i, rel_path in enumerate(java_files, 1):
            print(f"   [{i}/{len(java_files)}] Processing: {rel_path}")
            self._analyze_file_metadata(rel_path)
            
        print("⚖️  Calculating optimal migration sequence...")
        sequence, cycles = self._calculate_topological_sort()
        
        return {
            "migration_sequence": sequence,
            "circular_dependencies": cycles
        }

    def _discover_java_files(self) -> List[str]:
        java_files = []
        for root, _, files in os.walk(self.source_directory):
            for filename in files:
                if filename.endswith('.java'):
                    full_path = os.path.join(root, filename)
                    java_files.append(os.path.relpath(full_path, self.source_directory))
        return java_files

    def _analyze_file_metadata(self, rel_path: str):
        """
        Extracts class name, package, imports, and referenced types using javalang.
        """
        full_path = os.path.join(self.source_directory, rel_path)
        try:
            with open(full_path, 'r', encoding='utf-8') as f:
                content = f.read()
                tree = javalang.parse.parse(content)
                
                class_name = os.path.basename(rel_path)[:-5]
                referenced_types = self._extract_referenced_types(tree)
                imports = [imp.path for imp in tree.imports]
                
                self.file_metadata[rel_path] = {
                    "class_name": class_name,
                    "referenced_types": referenced_types,
                    "imports": imports
                }
                self.class_to_file_map[class_name] = rel_path
                
        except Exception as e:
            print(f"Warning: Could not parse {rel_path}: {e}")

    def _extract_referenced_types(self, tree) -> Set[str]:
        found_types = set()
        for _, node in tree.filter(javalang.tree.ReferenceType):
            found_types.add(node.name)
        for _, node in tree.filter(javalang.tree.ClassDeclaration):
            if node.extends:
                found_types.add(node.extends.name)
            if node.implements:
                for interface in node.implements:
                    found_types.add(interface.name)
        return found_types

    def get_external_libraries(self) -> Set[str]:
        """
        Identifies third-party libraries by looking at imports.
        Filters out standard Java packages and project-internal packages.
        """
        external_libs = set()
        # Collect all project-internal class names for filtering
        internal_class_names = {meta.get("class_name") for meta in self.file_metadata.values()}
        
        for meta in self.file_metadata.values():
            for imp in meta.get("imports", []):
                # Filter out standard java/javax (unless they are specific ones like swing/awt)
                if imp.startswith("java.") or imp.startswith("javax."):
                    if not (imp.startswith("javax.swing") or imp.startswith("java.awt")):
                        continue
                
                parts = imp.split('.')
                if not parts:
                    continue
                    
                # Heuristic: If the last part is a known internal class name, it's likely internal
                if parts[-1] in internal_class_names:
                    continue
                
                # Identify library prefix (e.g., org.apache.commons)
                if len(parts) >= 3:
                    lib_id = ".".join(parts[:3])
                elif len(parts) >= 2:
                    lib_id = ".".join(parts[:2])
                else:
                    lib_id = parts[0]
                    
                external_libs.add(lib_id)
        return external_libs

    def _calculate_topological_sort(self) -> Tuple[List[str], List[List[str]]]:
        final_order = []
        visited = set()
        currently_visiting = set()
        cycles = []
        
        def dfs(file_path, stack):
            if file_path in currently_visiting:
                try:
                    start_idx = stack.index(file_path)
                    cycles.append(stack[start_idx:] + [file_path])
                except ValueError:
                    pass
                return
            if file_path in visited:
                return
            currently_visiting.add(file_path)
            stack.append(file_path)
            metadata = self.file_metadata.get(file_path, {})
            for ref_type in metadata.get("referenced_types", []):
                dep_path = self.class_to_file_map.get(ref_type)
                if dep_path and dep_path != file_path:
                    dfs(dep_path, stack)
            stack.pop()
            currently_visiting.remove(file_path)
            visited.add(file_path)
            final_order.append(file_path)

        for file_path in self.file_metadata.keys():
            dfs(file_path, [])
        return final_order, cycles

def resolve_maven_coordinates(libraries: Set[str]) -> List[Dict]:
    """
    Uses Gemini to find Maven coordinates for a set of library package identifiers.
    """
    if not libraries:
        return []
        
    print(f"🤖 Consulting AI to resolve Maven coordinates for: {libraries}")
    prompt = (
        f"Identify the Maven coordinates (groupId, artifactId) for these Java package prefixes: {list(libraries)}. "
        "Return the result ONLY as a JSON list of objects with keys: 'groupId', 'artifactId', 'version' (use 'LATEST' if version is unknown)."
    )
    
    try:
        cmd = f"gemini --prompt {shlex.quote(prompt)} --yolo"
        process = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        stdout, _ = process.communicate()
        
        match = re.search(r'\[.*\]', stdout, re.DOTALL)
        if match:
            return json.loads(match.group(0))
    except Exception as e:
        print(f"⚠️ Error resolving Maven coordinates: {e}")
    return []

def main():
    parser = argparse.ArgumentParser(description="Analyze Java dependencies for migration.")
    parser.add_argument("source_dir", help="Directory containing Java source files")
    args = parser.parse_args()
    
    analyzer = DependencyAnalyzer(args.source_dir)
    result = analyzer.run_analysis()
    
    if result:
        project_name = os.path.basename(os.path.dirname(os.path.abspath(args.source_dir)))
        external_libs = analyzer.get_external_libraries()
        maven_deps = resolve_maven_coordinates(external_libs)
        
        plan_data = {
            "project_name": project_name,
            "source_directory": args.source_dir,
            "migration_sequence": result["migration_sequence"],
            "circular_dependencies": result["circular_dependencies"],
            "detected_libraries": list(external_libs),
            "maven_dependencies": maven_deps
        }
        
        output_dir = "agent_state/migration_plans"
        os.makedirs(output_dir, exist_ok=True)
        plan_path = os.path.join(output_dir, f"{project_name}_in_progress.json")
        
        with open(plan_path, "w", encoding='utf-8') as f:
            json.dump(plan_data, f, indent=4)
            
        print(f"Success: Analysis for '{project_name}' completed.")
        if maven_deps:
            print(f"📦 Added {len(maven_deps)} external Maven dependencies to plan.")
        if result["circular_dependencies"]:
            print(f"⚠️ Warning: Circular dependencies found: {result['circular_dependencies']}")

if __name__ == "__main__":
    main()
ular_dependencies']}")

if __name__ == "__main__":
    main()
