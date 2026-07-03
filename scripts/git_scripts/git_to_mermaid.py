import subprocess
import re

def run_cmd(cmd):
    try:
        return subprocess.check_output(cmd, text=True, encoding='utf-8').strip().split('\n')
    except subprocess.CalledProcessError:
        return []

def clean_msg(text):
    if not text: return ""
    text = text.replace('"', "'").replace(':', '-')
    return re.sub(r'[^\x20-\x7E]', '', text).strip()

def clean_branch(text):
    if not text: return "unknown"
    # Mermaid branch names break if they contain quotes or special characters
    return re.sub(r'[^a-zA-Z0-9_\-]', '-', text).strip('-')

def format_branch(b_name):
    # Wrap in quotes so branches starting with numbers don't break the lexer
    if b_name in ["main", "master"]:
        return b_name
    return f'"{b_name}"'

def generate_mermaid():
    # Get all local branches
    branches_out = run_cmd(["git", "for-each-ref", "--format=%(refname:short)", "refs/heads/"])
    branches = [b.strip() for b in branches_out if b.strip()]
    
    if not branches:
        # Using string multiplication to prevent UI parser glitches
        print("`" * 3 + "mermaid\ngitGraph\n    %% Error: No local branches found.\n" + "`" * 3)
        return

    # Prioritize main lines to claim baseline commits first
    sorted_branches = []
    for b in ["main", "master", "develop", "dev", "staging"]:
        if b in branches:
            sorted_branches.append(b)
            branches.remove(b)
    sorted_branches.extend(branches)

    # 1. Map commits to their true originating branches using first-parent traversal
    commit_to_branch = {}
    for branch in sorted_branches:
        log_out = run_cmd(["git", "log", branch, "--first-parent", "--format=%h"])
        for commit in log_out:
            if commit and commit not in commit_to_branch:
                commit_to_branch[commit] = branch

    # 2. Grab full history strictly for the branches we just mapped
    cmd = ["git", "log"] + sorted_branches + ["--reverse", "--topo-order", "--format=%h|%p|%s"]
    output = run_cmd(cmd)

    print("`" * 3 + "mermaid")
    print("gitGraph")
    
    def norm_b(b_name):
        if b_name == "master" and "main" not in sorted_branches: 
            return "main"
        return clean_branch(b_name)

    n_current = "main"
    last_target = "main"
    declared_branches = {"main", "master"}
    has_commits = False

    for line in output:
        if not line or '|' not in line: 
            continue
            
        parts = line.split('|', 2)
        if len(parts) < 3: 
            continue
        
        hash_val, parents_str, msg = parts
        parents = parents_str.split()
        
        # Determine the target branch, fallback to the last used branch if it was deleted
        target_branch = commit_to_branch.get(hash_val, last_target)
        last_target = target_branch
        n_target = norm_b(target_branch)

        # Lazy branch initialization from the parent branch
        if n_target not in declared_branches:
            if not has_commits:
                # Prevent branching before the initial commit
                print('    commit id: "root" msg: "Repository Root"')
                has_commits = True

            parent_hash = parents[0] if parents else None
            parent_branch = commit_to_branch.get(parent_hash, last_target) if parent_hash else last_target
            n_parent = norm_b(parent_branch)
            
            if n_parent not in declared_branches:
                n_parent = n_current
                
            if n_current != n_parent:
                print(f'    checkout {format_branch(n_parent)}')
                n_current = n_parent

            print(f'    branch {format_branch(n_target)}')
            declared_branches.add(n_target)

        # Switch lanes natively
        if n_target != n_current:
            print(f'    checkout {format_branch(n_target)}')
            n_current = n_target
            
        safe_msg = clean_msg(msg)
        safe_hash = clean_msg(hash_val)
        
        if len(parents) > 1:
            # Handle standard Git merges
            merged_from_branch = commit_to_branch.get(parents[1])
            if merged_from_branch:
                n_merged = norm_b(merged_from_branch)
                if n_merged != n_current and n_merged in declared_branches:
                    print(f'    merge {format_branch(n_merged)} id: "merge-{safe_hash}" type: HIGHLIGHT')
                    has_commits = True
                    continue
            
            # Fallback if merged branch was deleted and couldn't be scanned
            print(f'    commit id: "id-{safe_hash}" msg: "Merge"')
        else:
            # Standard Commit
            print(f'    commit id: "id-{safe_hash}" msg: "{safe_msg}"')

        has_commits = True

    print("`" * 3)

if __name__ == "__main__":
    generate_mermaid()