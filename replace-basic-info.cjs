const fs = require('fs');
const path = require('path');

const addPath = path.join(__dirname, 'app/src/main/java/com/example/rantu/Components/AddRoomScreen.kt');
let addContent = fs.readFileSync(addPath, 'utf8');

const regexAdd = /Card\(\s*modifier = Modifier\.fillMaxWidth\(\),\s*colors = CardDefaults\.cardColors\(containerColor = Color\.White\),\s*elevation = CardDefaults\.cardElevation\(defaultElevation = 2\.dp\)\s*\)\s*\{\s*Column\(\s*modifier = Modifier\.padding\(16\.dp\)\s*\)\s*\{\s*Text\(\s*text = "📋 Información Básica"[\s\S]*?singleLine = true\s*\)\s*\}\s*\}/;

const replaceAdd = `RoomFormBasicInfo(
                titulo = addRoomViewModel.titulo.value,
                onTituloChange = { addRoomViewModel.titulo.value = it },
                descripcion = addRoomViewModel.descripcion.value,
                onDescripcionChange = { addRoomViewModel.descripcion.value = it },
                precio = addRoomViewModel.precio.value,
                onPrecioChange = { addRoomViewModel.precio.value = it }
            )`;

addContent = addContent.replace(regexAdd, replaceAdd);
fs.writeFileSync(addPath, addContent);
console.log("AddRoomScreen updated");

const editPath = path.join(__dirname, 'app/src/main/java/com/example/rantu/Components/EditRoomScreen.kt');
let editContent = fs.readFileSync(editPath, 'utf8');

const regexEdit = /Card\(\s*modifier = Modifier\.fillMaxWidth\(\),\s*colors = CardDefaults\.cardColors\(containerColor = Color\.White\),\s*elevation = CardDefaults\.cardElevation\(defaultElevation = 2\.dp\)\s*\)\s*\{\s*Column\(\s*modifier = Modifier\.padding\(16\.dp\)\s*\)\s*\{\s*Text\(\s*text = "📋 Información Básica"[\s\S]*?singleLine = true\s*\)\s*\}\s*\}/;

const replaceEdit = `RoomFormBasicInfo(
                titulo = editRoomViewModel.titulo.value,
                onTituloChange = { editRoomViewModel.titulo.value = it },
                descripcion = editRoomViewModel.descripcion.value,
                onDescripcionChange = { editRoomViewModel.descripcion.value = it },
                precio = editRoomViewModel.precio.value,
                onPrecioChange = { editRoomViewModel.precio.value = it }
            )`;

editContent = editContent.replace(regexEdit, replaceEdit);
fs.writeFileSync(editPath, editContent);
console.log("EditRoomScreen updated");

