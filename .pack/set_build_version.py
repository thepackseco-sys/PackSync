import os,re
from pathlib import Path
run=int(os.environ["PACK_RUN_NUMBER"])
if not 1<=run<=999999:raise RuntimeError("Build version range exhausted; review version policy")
if 'com.packseco.packsync' in Path('app/build.gradle.kts').read_text():
 catalog=Path('gradle/libs.versions.toml');text=catalog.read_text()
 old=re.search(r'^version-name = "([0-9]+)[.]([0-9]+)[.]([0-9]+)[.]([0-9]+)"',text,re.M)
 if not old:raise RuntimeError('Unknown upstream version format')
 major,minor,patch,wrapper=map(int,old.groups());wrapper+=run
 name=f"{major}.{minor}.{patch}.{wrapper}";code=major*1000000+minor*10000+patch*100+wrapper
 if code>=2100000000:raise RuntimeError('Android versionCode limit reached')
 text,n=re.subn(r'^version-code = "[0-9]+"',f'version-code = "{code}"',text,flags=re.M);assert n==1
 text,n=re.subn(r'^version-name = "[^"]+"',f'version-name = "{name}"',text,flags=re.M);assert n==1
 catalog.write_text(text)
else:
 name="1.0.0."+str(run);code=1000000+run*100+50
 p=Path('app/build.gradle.kts');text=p.read_text()
 text,n=re.subn(r'versionCode = [0-9]+',f'versionCode = {code}',text,count=1);assert n==1
 text,n=re.subn(r'versionName = "[^"]+"',f'versionName = "{name}"',text,count=1);assert n==1
 p.write_text(text)
print('Pack development version '+name+' code '+str(code))
