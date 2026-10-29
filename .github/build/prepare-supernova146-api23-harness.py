#!/usr/bin/env python3
"""Build a separate test-only APK for actual API23 transport/trust execution.

This harness has its own QA application ID/debug key. It never signs or replaces
Supernova and never receives the protected production signing environment.
"""
import argparse,json,hashlib,shutil,zipfile
from pathlib import Path
p=argparse.ArgumentParser(description=__doc__)
for name in ['video','medialib','review','output']:p.add_argument('--'+name,type=Path,required=True)
a=p.parse_args();video=a.video.resolve();media=a.medialib.resolve();review=a.review.resolve();out=a.output.resolve();out.mkdir(parents=True,exist_ok=True)
for name in ['gradlew','gradle']: 
 source=video/name
 if source.is_dir():shutil.copytree(source,out/name,dirs_exist_ok=True)
 else:shutil.copyfile(source,out/name)
(out/'settings.gradle').write_text("rootProject.name = 'Supernova146Api23TransportHarness'\n")
(out/'build.gradle').write_text('''buildscript {
 repositories { google(); mavenCentral() }
 dependencies { classpath 'com.android.tools.build:gradle:9.3.2' }
}
apply plugin: 'com.android.application'
repositories { google(); mavenCentral() }
android {
 namespace 'app.supernova.qa.transport0146'
 compileSdk 37
 defaultConfig {
  applicationId 'app.supernova.qa.transport0146'
  minSdk 23
  targetSdk 37
  testInstrumentationRunner 'androidx.test.runner.AndroidJUnitRunner'
 }
 compileOptions {
  sourceCompatibility JavaVersion.VERSION_17
  targetCompatibility JavaVersion.VERSION_17
  coreLibraryDesugaringEnabled true
 }
 packaging { resources.excludes += ['META-INF/LICENSE*','META-INF/NOTICE*','META-INF/DEPENDENCIES','META-INF/beans.xml'] }
}
// Verified runtime Kotlin/annotations jars are supplied in libs. Avoid adding
// AGP/test-runner Maven copies with a second version of the same classes/resources.
configurations.configureEach {
 exclude group:'org.jetbrains.kotlin',module:'kotlin-stdlib'
 exclude group:'org.jetbrains',module:'annotations'
}
dependencies {
 coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs_nio:2.1.5'
 implementation fileTree(dir:'libs',include:['*.jar'])
 androidTestImplementation 'androidx.test:runner:1.7.0'
 androidTestImplementation 'androidx.test:rules:1.7.0'
 androidTestImplementation 'junit:junit:4.13.2'
}
''')
(out/'gradle.properties').write_text('android.useAndroidX=true\norg.gradle.jvmargs=-Xmx3g\n')
main=out/'src/main';test=out/'src/androidTest/java/com/archos/mediacenter/filecoreextension/upnp2';test.mkdir(parents=True,exist_ok=True)
main.mkdir(parents=True,exist_ok=True);(main/'AndroidManifest.xml').write_text('<manifest xmlns:android="http://schemas.android.com/apk/res/android"><uses-permission android:name="android.permission.INTERNET"/><application android:label="Supernova transport QA" android:usesCleartextTraffic="true"/></manifest>\n')
libs=out/'libs';libs.mkdir(exist_ok=True);inventory=json.loads((review/'inventory.json').read_text());selected=[]
for item in inventory:
 coordinate=item['coordinate'];group,module,version=coordinate.split(':')
 if group=='org.eclipse.jetty' or group=='javax.servlet' or group=='org.slf4j' or (group=='com.github.nova-video-player.jupnp' and module=='org.jupnp') or group in ['commons-net','com.squareup.okhttp3','com.squareup.okio','org.jetbrains.kotlin','org.jetbrains'] :
  source=review/item['file']
  if hashlib.sha256(source.read_bytes()).hexdigest()!=item['sha256']:raise ValueError('Runtime archive changed: '+coordinate)
  if source.suffix=='.aar':
   with zipfile.ZipFile(source) as archive:(libs/(module+'-'+version+'.jar')).write_bytes(archive.read('classes.jar'))
  else:shutil.copyfile(source,libs/source.name)
  selected.append({'coordinate':coordinate,'sha256':item['sha256']})
if len([x for x in selected if x['coordinate'].startswith('org.eclipse.jetty:')])!=8:raise ValueError('All eight resolved Jetty modules are required')
for name in ['NovaJettyServletContainer','ValidatingJettyStreamClient']:
 source=media/('src/com/archos/mediacenter/filecoreextension/upnp2/'+name+'.java');dest=main/'java/com/archos/mediacenter/filecoreextension/upnp2'/source.name;dest.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(source,dest)
source=media/'src/com/archos/medialib/NativeTrustRoots.java';dest=main/'java/com/archos/medialib/NativeTrustRoots.java';dest.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(source,dest)
for name in ['Jetty145SecurityTest','Jetty146BackportTest']:
 source=video/('src/test/java/com/archos/mediacenter/filecoreextension/upnp2/'+name+'.java');text=source.read_text()
 if name=='Jetty145SecurityTest':
  text=text.replace('req.getInputStream().readAllBytes()','readAll(req.getInputStream())').replace('socket.getInputStream().readAllBytes()','readAll(socket.getInputStream())')
  text=text.replace('    private static final String SECRET', '''    private static byte[] readAll(java.io.InputStream in) throws java.io.IOException {
        java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream();byte[] buffer=new byte[4096];int n;while((n=in.read(buffer))!=-1)out.write(buffer,0,n);return out.toByteArray();
    }
    private static final String SECRET''')
 (test/source.name).write_text(text)
filecore=media.parent/'FileCoreLibrary'
for source,base in [(filecore/'src/com/archos/filecorelibrary/ftp/FtpsTlsPolicy.java',main/'java'),(filecore/'testCommon/java/com/archos/filecorelibrary/ftp/FtpsTlsPolicyCases.java',out/'src/androidTest/java')]:
 dest=base/'com/archos/filecorelibrary/ftp'/source.name;dest.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(source,dest)
(out/'src/androidTest/java/com/archos/filecorelibrary/ftp/FtpsTlsPolicyAndroid23Test.java').write_text('package com.archos.filecorelibrary.ftp;\n@org.junit.runner.RunWith(androidx.test.runner.AndroidJUnit4.class)\npublic class FtpsTlsPolicyAndroid23Test extends FtpsTlsPolicyCases { }\n')
raw=main/'res/raw';raw.mkdir(parents=True,exist_ok=True)
for source in (video/'res/raw').glob('isrg_root_x*'):shutil.copyfile(source,raw/source.name)
(test/'NativeTrustRootsAndroid23Test.java').write_text('''package com.archos.mediacenter.filecoreextension.upnp2;
import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import com.archos.medialib.NativeTrustRoots;
import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.cert.*;
import org.junit.Test;
import static org.junit.Assert.*;
public class NativeTrustRootsAndroid23Test {
 @Test public void appTrustBundleAndScopedJavaFactoryUseActualAndroid23Apis()throws Exception {
  assertEquals(23,android.os.Build.VERSION.SDK_INT);
  Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
  SSLSocketFactory before=HttpsURLConnection.getDefaultSSLSocketFactory();HostnameVerifier verifier=HttpsURLConnection.getDefaultHostnameVerifier();
  NativeTrustRoots.initialize(context);
  File bundle=new File(context.getNoBackupFilesDir(),"native-ca/anchors.pem");assertTrue(bundle.isFile());
  assertEquals(bundle.getAbsolutePath(),android.system.Os.getenv("SSL_CERT_FILE"));
  java.util.Collection<? extends Certificate> certs;
  try(InputStream in=new FileInputStream(bundle)){certs=CertificateFactory.getInstance("X.509").generateCertificates(in);}
  assertTrue("System and explicit bundled roots are required",certs.size()>2);
  for(String name:new String[]{"isrg_root_x1","isrg_root_x2"}){
   int id=context.getResources().getIdentifier(name,"raw",context.getPackageName());assertTrue(id!=0);
   Certificate expected;try(InputStream in=context.getResources().openRawResource(id)){expected=CertificateFactory.getInstance("X.509").generateCertificate(in);}
   assertTrue(certs.contains(expected));
  }
  HttpsURLConnection connection=(HttpsURLConnection)new URL("https://127.0.0.1/").openConnection();
  NativeTrustRoots.configure(connection);
  assertSame(before,HttpsURLConnection.getDefaultSSLSocketFactory());assertSame(verifier,HttpsURLConnection.getDefaultHostnameVerifier());assertSame(verifier,connection.getHostnameVerifier());assertNotSame(before,connection.getSSLSocketFactory());connection.disconnect();
 }
}
''')
(out/'harness-inputs.json').write_text(json.dumps({'purpose':'Test-only API23 harness; separate app/debug identity, no Supernova signing secrets','libraries':selected,'production_sources':[str(p.relative_to(media)) for p in [media/'src/com/archos/medialib/NativeTrustRoots.java']], 'filecore_patch_sha256':hashlib.sha256((video/'.github/build/filecorelibrary-supernova146-upstream.patch').read_bytes()).hexdigest()},indent=2)+'\n');print('Prepared separate API23 transport/trust test harness with',len(selected),'verified runtime archives')
