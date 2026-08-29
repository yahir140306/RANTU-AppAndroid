import re

with open('app/src/main/java/com/example/rantu/Components/AddRoomScreen.kt', 'r') as f:
    add_content = f.read()

# Using regex to find the '📝 Detalles del Cuarto' card.
# Card\([\s\S]*?📝 Detalles del Cuarto[\s\S]*?\}\s*\n\s*\}\s*\n
add_regex = r'Card\(\s*modifier = Modifier\.fillMaxWidth\(\),\s*colors = CardDefaults\.cardColors\(containerColor = Color\.White\),\s*elevation = CardDefaults\.cardElevation\(defaultElevation = 2\.dp\)\s*\)\s*\{\s*Column\(\s*modifier = Modifier\.padding\(16\.dp\)\s*\)\s*\{\s*Text\(\s*text = "📝 Detalles del Cuarto"[\s\S]*?\}\s*\)\s*\}\s*\}'

add_replace = """RoomFormDetails(
                caracteristicas = addRoomViewModel.caracteristicas.value,
                onCaracteristicasChange = { addRoomViewModel.caracteristicas.value = it },
                celular = addRoomViewModel.celular.value,
                onCelularChange = { addRoomViewModel.celular.value = it }
            )"""

add_content = re.sub(add_regex, add_replace, add_content)
with open('app/src/main/java/com/example/rantu/Components/AddRoomScreen.kt', 'w') as f:
    f.write(add_content)


with open('app/src/main/java/com/example/rantu/Components/EditRoomScreen.kt', 'r') as f:
    edit_content = f.read()

edit_replace = """RoomFormDetails(
                caracteristicas = editRoomViewModel.caracteristicas.value,
                onCaracteristicasChange = { editRoomViewModel.caracteristicas.value = it },
                celular = editRoomViewModel.celular.value,
                onCelularChange = { editRoomViewModel.celular.value = it }
            )"""

# Wait, in EditRoomScreen it might be slightly different. But they were identical before.
# Let's try replacing it if it matches.
edit_regex = r'Card\(\s*modifier = Modifier\.fillMaxWidth\(\),\s*colors = CardDefaults\.cardColors\(containerColor = Color\.White\),\s*elevation = CardDefaults\.cardElevation\(defaultElevation = 2\.dp\)\s*\)\s*\{\s*Column\(\s*modifier = Modifier\.padding\(16\.dp\)\s*\)\s*\{\s*Text\(\s*text = "📝 Detalles del Cuarto"[\s\S]*?\}\s*\)\s*\}\s*\}'

# The regex doesn't capture it if it's formatted slightly differently or has extra fields in EditRoom.
# Wait, EditRoom had `precio` and `celular` inside `Informacion Basica` in the first check?! No, I moved them or they were there.
