import re
import glob

files = glob.glob('app/src/main/java/com/example/rantu/Components/*.kt')
files.append('app/src/main/java/com/example/rantu/MainActivity.kt')

for filepath in files:
    with open(filepath, 'r') as f:
        content = f.read()

    # Add import
    import_stmt = "\nimport com.example.rantu.di.ViewModelFactory"
    if "viewModel(" in content and "ViewModelFactory" not in content:
        content = content.replace("import androidx.lifecycle.viewmodel.compose.viewModel", "import androidx.lifecycle.viewmodel.compose.viewModel" + import_stmt)
    
    # Replace injections
    content = re.sub(r'viewModel\(\)', 'viewModel(factory = ViewModelFactory)', content)
    
    with open(filepath, 'w') as f:
        f.write(content)

print("DI updated in Compose files!")
