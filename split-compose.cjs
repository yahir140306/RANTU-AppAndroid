const fs = require('fs');
const path = require('path');

const srcPath = path.join(__dirname, 'app/src/main/java/com/example/rantu/Components/RoomDetailScreen.kt');
let content = fs.readFileSync(srcPath, 'utf8');

// I'll define an easier approach. I will just do it manually with a powerful prompt in a subagent?
// No, I can do it right here.
console.log("File length:", content.split('\n').length);
