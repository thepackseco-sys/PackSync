"""Synthetic Android emulator tests only. Refuses to operate on a local/physical device."""
import os,sys,re,subprocess,time,tempfile,json,xml.etree.ElementTree as ET,ssl,socket,http.client
from pathlib import Path
if os.environ.get("GITHUB_ACTIONS")!="true" or os.environ.get("RUNNER_OS")!="Linux":raise RuntimeError("Only an isolated GitHub Linux test runner is supported")
APP=sys.argv[1];PACKAGE={"PackStore":"com.packseco.packstore","PackSync":"com.packseco.packsync"}[APP]
SERIAL="emulator-5554"
def run(args,check=True):
 r=subprocess.run(list(map(str,args)),stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True,encoding="utf-8",errors="replace",timeout=120)
 if check and r.returncode:raise RuntimeError("Test command failed: "+r.stdout[-900:])
 return r.stdout

def adb(*args,check=True):return run(["adb","-s",SERIAL,*args],check)
assert SERIAL in run(["adb","devices"])
assert adb("shell","getprop","ro.kernel.qemu").strip()=="1","Physical devices are forbidden"
sdk=Path(os.environ["ANDROID_HOME"]);tools=max((p for p in (sdk/"build-tools").iterdir() if re.fullmatch(r"[0-9]+[.][0-9]+[.][0-9]+",p.name)),key=lambda p:tuple(map(int,p.name.split('.'))))
apks=list(Path('pack-unsigned').glob('*.apk'));assert len(apks)==1
password=os.urandom(20).hex();os.environ['PACK_SMOKE_PASSWORD']=password
with tempfile.TemporaryDirectory(prefix="pack-emulator-") as d:
 temp=Path(d);keystore=temp/'synthetic-only.p12';signed=temp/'test.apk'
 run(['keytool','-genkeypair','-noprompt','-alias','smoke','-storetype','PKCS12','-keystore',keystore,'-storepass:env','PACK_SMOKE_PASSWORD','-keypass:env','PACK_SMOKE_PASSWORD','-keyalg','RSA','-keysize','2048','-validity','2','-dname','CN=Synthetic Emulator Test Only'])
 run([tools/'apksigner','sign','--ks',keystore,'--ks-pass','env:PACK_SMOKE_PASSWORD','--ks-key-alias','smoke','--out',signed,apks[0]])
 run([tools/'apksigner','verify','--verbose',signed])
 badging=run([tools/'aapt2','dump','badging',signed]);assert "name='"+PACKAGE+"'" in badging
 adb('install',signed);print('PASS: signed synthetic APK installed with expected package',flush=True)
 adb('logcat','-c');adb('shell','monkey','-p',PACKAGE,'-c','android.intent.category.LAUNCHER','1');time.sleep(8)
 assert adb('shell','pidof',PACKAGE).strip(),'App process did not survive launch'
 crashes=adb('logcat','-b','crash','-d');assert 'FATAL EXCEPTION' not in crashes,'App crashed during first launch'
 print('PASS: app launches without Java crash',flush=True)
 if APP=='PackSync':
  for perm in ('POST_NOTIFICATIONS','ACCESS_FINE_LOCATION','ACCESS_COARSE_LOCATION','ACCESS_BACKGROUND_LOCATION'):
   adb('shell','pm','grant',PACKAGE,'android.permission.'+perm,check=False)
  adb('shell','appops','set',PACKAGE,'MANAGE_EXTERNAL_STORAGE','allow',check=False)
  adb('shell','dumpsys','deviceidle','whitelist','+'+PACKAGE,check=False)
  adb('shell','svc','wifi','enable',check=False)
  adb('shell','am','force-stop',PACKAGE);adb('shell','monkey','-p',PACKAGE,'-c','android.intent.category.LAUNCHER','1')
  for attempt in range(40):
   adb('shell','uiautomator','dump','/sdcard/pack-ui.xml',check=False)
   raw=adb('shell','cat','/sdcard/pack-ui.xml',check=False)
   try:ui=ET.fromstring(raw)
   except ET.ParseError:time.sleep(2);continue
   button=None
   for node in ui.iter('node'):
    if node.get('enabled')=='true' and node.get('text') in ('Continue','Finish'):
     bounds=list(map(int,re.findall(r'[0-9]+',node.get('bounds',''))))
     if len(bounds)==4:button=((bounds[0]+bounds[2])//2,(bounds[1]+bounds[3])//2);break
   if button:adb('shell','input','tap',*map(str,button))
   if 'Key Generation' not in raw and 'Welcome' not in raw and 'Folders' in raw:break
   time.sleep(2)
  component=PACKAGE+'/com.nutomic.syncthingandroid.receiver.AppConfigReceiver'
  adb('shell','am','broadcast','-n',component)
  adb('shell','am','broadcast','-n',component,'-a','comXpacksecoXpacksync.action.START')
  time.sleep(2);assert 'FATAL EXCEPTION' not in adb('logcat','-b','crash','-d')
  print('PASS: malformed exported broadcasts do not crash the app',flush=True)
  # Inspect only this fresh, synthetic emulator app. No real configuration is involved.
  adb('root');adb('wait-for-device')
  config=adb('shell','cat','/data/user/0/'+PACKAGE+'/files/config.xml',check=False)
  try:xml=ET.fromstring(config)
  except ET.ParseError:raise RuntimeError('Synthetic onboarding did not produce core config; not claiming core success')
  gui=xml.find('gui');assert gui is not None;api_key=gui.findtext('apikey');assert api_key
  address=gui.findtext('address');port=int(address.rsplit(':',1)[1]);adb('forward','tcp:18384','tcp:'+str(port))
  tls=gui.get('tls')=='true'
  if tls:
   cert=adb('shell','cat','/data/user/0/'+PACKAGE+'/files/https-cert.pem');certfile=temp/'synthetic-gui-cert.pem';certfile.write_text(cert)
   decoded=ssl._ssl._test_decode_cert(str(certfile));names=[v for k,v in decoded.get('subjectAltName',[]) if k=='DNS']
   hostname=names[0] if names else next(v for group in decoded['subject'] for k,v in group if k=='commonName')
   context=ssl.create_default_context(cafile=str(certfile))
  for attempt in range(30):
   try:
    connection=http.client.HTTPConnection('127.0.0.1',18384,timeout=8)
    if tls:connection.sock=context.wrap_socket(socket.create_connection(('127.0.0.1',18384),timeout=8),server_hostname=hostname)
    connection.request('GET','/rest/system/status',headers={'X-API-Key':api_key});response=connection.getresponse();data=json.loads(response.read());connection.close()
    if response.status==200 and data.get('myID'):break
   except (OSError,ValueError,http.client.HTTPException):time.sleep(2)
  else:raise RuntimeError('Syncthing core API did not become ready')
  print('PASS: Android Syncthing core starts and authenticated local API returns device identity',flush=True)
 adb('install','-r',signed);print('PASS: same-signature reinstall succeeds (same build; not a newer-version upgrade test)',flush=True)
print('EMULATOR_SMOKE_PASSED '+APP+'; Samsung battery/boot, real backup migration and full repository/sync workflow tests remain pending',flush=True)
