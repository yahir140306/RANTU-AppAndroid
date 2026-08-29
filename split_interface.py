import re

with open('app/src/main/java/com/example/rantu/Components/InterfaceFirst.kt', 'r') as f:
    content = f.read()

# Let's check how the UI is structured
import sys
if "Scaffold" not in content:
    print("No Scaffold found")
    sys.exit(0)

