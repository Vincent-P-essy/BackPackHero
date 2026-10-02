"""Export only the screenshots and observations of this gameplay run."""
import base64
import hashlib
import json
from pathlib import Path
import sys

root = Path(sys.argv[1])
files = sorted((root / 'docs/screenshots').glob('gameplay*.png'))
files.append(root / 'docs/gameplay.json')
assert len(files) >= 2, 'No gameplay screenshots'
report = json.loads(files[-1].read_text())
assert report['played'] is True, 'Gameplay verification failed'
for file in files:
    data = file.read_bytes()
    sha = hashlib.sha1(f'blob {len(data)}\0'.encode() + data).hexdigest()
    print('CAPTURE_BEGIN ' + json.dumps({'path': str(file.relative_to(root)), 'sha': sha, 'size': len(data)}))
    encoded = base64.b64encode(data).decode()
    for start in range(0, len(encoded), 4096):
        print('CAPTURE_CHUNK ' + encoded[start:start + 4096])
    print('CAPTURE_END')
