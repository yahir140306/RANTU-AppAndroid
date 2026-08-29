import re

with open('app/src/main/java/com/example/rantu/Components/RoomDetailScreen.kt', 'r') as f:
    content = f.read()

def extract_block(text, marker):
    start = text.find(marker)
    if start == -1: return None, None
    first_brace = text.find('{', start)
    if first_brace == -1: return None, None
    count = 1
    idx = first_brace + 1
    while idx < len(text) and count > 0:
        if text[idx] == '{': count += 1
        elif text[idx] == '}': count -= 1
        idx += 1
    if count == 0:
        return start, idx
    return None, None

# 1. Download Dialog
start_dl, end_dl = extract_block(content, "// Diálogo de descarga de tarjeta")
dl_block = content[start_dl:end_dl]

# Replace in content
content = content[:start_dl] + """// Diálogo de descarga de tarjeta
    DownloadCardDialog(
        room = room,
        showDownloadDialog = showDownloadDialog,
        onDismiss = { showDownloadDialog = false }
    )""" + content[end_dl:]

# 2. Add Comment Dialog
start_com, end_com = extract_block(content, "// Diálogo para agregar comentario")
com_block = content[start_com:end_com]

content = content[:start_com] + """// Diálogo para agregar comentario
    AddCommentDialog(
        showDialog = showCommentDialog,
        onDismiss = { showCommentDialog = false },
        selectedRating = selectedRating,
        onRatingChange = { selectedRating = it },
        commentText = commentText,
        onCommentChange = { commentText = it },
        submitError = submitError,
        onSubmit = {
            if (selectedRating == 0) {
                submitError = "Por favor, selecciona una calificación"
                return@AddCommentDialog
            }
            if (commentText.trim().length < 10) {
                submitError = "El comentario debe tener al menos 10 caracteres"
                return@AddCommentDialog
            }
            
            viewModel.addComment(room.id, commentText, selectedRating)
            showCommentDialog = false
            commentText = ""
            selectedRating = 0
            submitError = null
        }
    )""" + content[end_com:]

# Need to append the new functions at the end of the file.
new_functions = f"""

@Composable
fun DownloadCardDialog(
    room: Room,
    showDownloadDialog: Boolean,
    onDismiss: () -> Unit
) {{
    val context = LocalContext.current
    val captureController = rememberCaptureController()
    var isDownloading by remember {{ mutableStateOf(false) }}
    
    // Removing the 'if (showDownloadDialog)' from the block because we handle it inside?
    // Actually the block ALREADY has 'if (showDownloadDialog) {{ Dialog ... }}'
    // But we need to replace the variables: 'showDownloadDialog = false' with 'onDismiss()'
    
{dl_block.replace("showDownloadDialog = false", "onDismiss()")}
}}

@Composable
fun AddCommentDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    selectedRating: Int,
    onRatingChange: (Int) -> Unit,
    commentText: String,
    onCommentChange: (String) -> Unit,
    submitError: String?,
    onSubmit: () -> Unit
) {{
    // The original block has 'if (showCommentDialog)'
{com_block.replace("showCommentDialog = false", "onDismiss()").replace("showCommentDialog", "showDialog")}
}}
"""

# Let's remove the variables from the top of RoomDetailScreen
content = content.replace("val captureController = rememberCaptureController()", "")
content = content.replace("var isDownloading by remember { mutableStateOf(false) }", "")
content = content.replace("val context = LocalContext.current", "val context = LocalContext.current") # Keep this as it might be used by WhatsApp intent

content += new_functions

with open('app/src/main/java/com/example/rantu/Components/RoomDetailScreen.kt', 'w') as f:
    f.write(content)

print("Refactored!")
