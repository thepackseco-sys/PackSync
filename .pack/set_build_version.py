import os,re
from pathlib import Path
run=int(os.environ["PACK_RUN_NUMBER"]);attempt=int(os.environ["PACK_RUN_ATTEMPT"])
if run<1 or not 1<=attempt<=9:raise RuntimeError("Build counter outside supported range")
number=run*10+attempt
if number>999999:raise RuntimeError("Build version range exhausted; review version policy")
name="1.0.0."+str(number);code=1000000+number
catalog=Path("gradle/libs.versions.toml")
if 'com.packseco.packsync' in Path('app/build.gradle.kts').read_text():
 text=catalog.read_text();text,n=re.subn(r'^version-code = "[0-9]+"',f'version-code = "{code}"',text,flags=re.M);assert n==1
 text,n=re.subn(r'^version-name = "[^"]+"',f'version-name = "{name}"',text,flags=re.M);assert n==1
 catalog.write_text(text)
else:
 p=Path('app/build.gradle.kts');text=p.read_text()
 text,n=re.subn(r'versionCode = [0-9]+',f'versionCode = {code}',text,count=1);assert n==1
 text,n=re.subn(r'versionName = "[^"]+"',f'versionName = "{name}"',text,count=1);assert n==1
 p.write_text(text)
print('Pack development version '+name+' code '+str(code))
