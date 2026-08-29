import os

with open('app/src/main/java/com/example/rantu/Components/componentes.kt', 'r') as f:
    content = f.read()

# The file contains imports at the top, then several functions.
# Let's extract the imports first.
import_end_idx = content.find('\n@Composable')
if import_end_idx == -1:
    import_end_idx = content.find('\nfun ')

imports = content[:import_end_idx].strip()
body = content[import_end_idx:]

functions = body.split('\n@Composable')
# The first element might be empty or just whitespace
functions = [f for f in functions if f.strip()]

for func in functions:
    # Ensure it starts with @Composable if it was split
    if not func.startswith('@Composable') and 'fun ' in func:
        func = '@Composable\n' + func
    
    # Extract function name
    lines = func.split('\n')
    fun_line = next((l for l in lines if l.startswith('fun ')), None)
    if fun_line:
        name = fun_line.split('fun ')[1].split('(')[0].split('<')[0].strip()
        filename = f"app/src/main/java/com/example/rantu/Components/{name}.kt"
        
        with open(filename, 'w') as out:
            out.write(imports + "\n\n" + func.strip() + "\n")
        print(f"Created {filename}")

os.remove('app/src/main/java/com/example/rantu/Components/componentes.kt')
print("Deleted componentes.kt")
