import re

# UPDATE ADD ROOM VIEW MODEL
with open('app/src/main/java/com/example/rantu/ui/AddRoomViewModel.kt', 'r') as f:
    content = f.read()

import_statement = "\nimport com.example.rantu.utils.RoomFormValidator\n"
if "import com.example.rantu.utils.RoomFormValidator" not in content:
    content = content.replace("import androidx.lifecycle.ViewModel", "import androidx.lifecycle.ViewModel" + import_statement)

new_validate = """    fun validateForm(): String? {
        return RoomFormValidator.validateForm(
            titulo = titulo.value,
            descripcion = descripcion.value,
            precio = precio.value,
            celular = celular.value,
            caracteristicas = caracteristicas.value,
            ubicacion = ubicacion.value,
            hasImages = imagen1Uri.value != null
        )
    }"""

content = re.sub(r'    fun validateForm\(\): String\? \{.*?    \}', new_validate, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/rantu/ui/AddRoomViewModel.kt', 'w') as f:
    f.write(content)

# UPDATE EDIT ROOM VIEW MODEL
with open('app/src/main/java/com/example/rantu/ui/EditRoomViewModel.kt', 'r') as f:
    content = f.read()

if "import com.example.rantu.utils.RoomFormValidator" not in content:
    content = content.replace("import androidx.lifecycle.ViewModel", "import androidx.lifecycle.ViewModel" + import_statement)

new_validate_edit = """    fun validateForm(): String? {
        return RoomFormValidator.validateForm(
            titulo = titulo.value,
            descripcion = descripcion.value,
            precio = precio.value,
            celular = celular.value,
            caracteristicas = caracteristicas.value,
            ubicacion = ubicacion.value,
            hasImages = imagen1UrlExisting.value != null || imagen1UriNew.value != null
        )
    }"""

content = re.sub(r'    fun validateForm\(\): String\? \{.*?    \}', new_validate_edit, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/rantu/ui/EditRoomViewModel.kt', 'w') as f:
    f.write(content)

print("ViewModels updated!")
