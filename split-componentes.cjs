const fs = require('fs');
const path = require('path');

const srcPath = path.join(__dirname, 'app/src/main/java/com/example/rantu/Components/componentes.kt');
let content = fs.readFileSync(srcPath, 'utf8');

// I'm better off using a simpler regex or splitting by "fun " + name
// Wait, the easiest is to just let the user know about this "God File" and extract them into individual files using a simple string operation, but the imports are a bit complex to parse. 
// A better way is just renaming the file to `SharedComponents.kt` and leaving it, or actually extracting them.
