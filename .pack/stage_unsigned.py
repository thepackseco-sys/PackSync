import hashlib,json,re,shutil
from pathlib import Path
source=Path('app/build/outputs/apk');files=[]
for p in source.rglob('*.apk'):
 if 'com.packseco.packsync' in Path('app/build.gradle.kts').read_text():
  if not re.fullmatch(r'com[.]packseco[.]packsync_release_v[0-9.]+[.]apk',p.name):continue
 files.append(p)
if len(files)!=1:raise RuntimeError('Expected exactly one universal/full APK, got '+str(len(files)))
out=Path('pack-unsigned');out.mkdir(exist_ok=True)
for p in files:shutil.copyfile(p,out/p.name)
manifest={p.name:hashlib.sha256(p.read_bytes()).hexdigest() for p in out.glob('*.apk')}
(out/'SHA256SUMS.json').write_text(json.dumps(manifest,indent=2))
print('Staged unsigned universal/full development APK')
