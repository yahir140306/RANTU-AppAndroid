import re

def extract_block(text, marker):
    start = text.find(marker)
    if start == -1: return None, None
    
    # find the first '{' after the marker
    first_brace = text.find('{', start)
    if first_brace == -1: return None, None
    
    # balance braces
    count = 1
    idx = first_brace + 1
    while idx < len(text) and count > 0:
        if text[idx] == '{': count += 1
        elif text[idx] == '}': count -= 1
        idx += 1
        
    if count == 0:
        # Include the enclosing function call? Wait, the marker is inside an `if` or just before `Dialog {`
        # Let's find the word before the first brace. Usually it's `if (showDownloadDialog) { Dialog {`
        return start, idx
    return None, None

with open('app/src/main/java/com/example/rantu/Components/RoomDetailScreen.kt', 'r') as f:
    content = f.read()

# 1. Download Dialog
marker_download = "// Diálogo de descarga de tarjeta"
start, end = extract_block(content, marker_download)

if start:
    # Actually, the marker is before `if (showDownloadDialog) { Dialog {`
    # Let's see what is exactly at the marker
    block = content[start:end]
    print("Found download dialog block of length", len(block))
else:
    print("Download dialog not found")


if start:
    print(block[:100])
    print("...")
    print(block[-50:])
