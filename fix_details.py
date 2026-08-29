with open('app/src/main/java/com/example/rantu/Components/AddRoomScreen.kt', 'r') as f:
    lines = f.readlines()

out_lines = []
skip = False
for line in lines:
    if "text = \"📝 Detalles del Cuarto\"," in line:
        # We need to backtrack to remove the Card
        # The Card starts 5 lines above
        out_lines = out_lines[:-7]
        out_lines.append("""            RoomFormDetails(
                caracteristicas = addRoomViewModel.caracteristicas.value,
                onCaracteristicasChange = { addRoomViewModel.caracteristicas.value = it },
                celular = addRoomViewModel.celular.value,
                onCelularChange = { addRoomViewModel.celular.value = it }
            )
""")
        skip = True
    
    if skip and "Spacer(modifier = Modifier.height(16.dp))" in line:
        # End of the block usually
        # Wait, the end of the card is two brackets.
        pass
    
    if not skip:
        out_lines.append(line)
        
    if skip and "    }" in line:
        # We need to count brackets? Too complex.
        pass
