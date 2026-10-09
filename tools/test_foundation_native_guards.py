import hashlib
import importlib.util
import io
import json
from pathlib import Path
import tarfile
import tempfile
import unittest
from unittest.mock import patch
import zipfile

ROOT=Path(__file__).resolve().parents[1]
def module(name,path):
    spec=importlib.util.spec_from_file_location(name,ROOT/path)
    result=importlib.util.module_from_spec(spec);spec.loader.exec_module(result);return result
builder=module('foundation_native_builder','.github/build/build-foundation-ffmpeg.py')
apk_guard=module('foundation_native_apk','.github/build/verify-foundation-native-apk.py')

class FoundationNativeGuardTests(unittest.TestCase):
    def test_corrupted_cached_source_refused_without_network(self):
        with tempfile.TemporaryDirectory() as d:
            p=Path(d)/'source.tar.gz';p.write_bytes(b'changed source')
            with patch.object(builder.subprocess,'run') as network:
                with self.assertRaisesRegex(ValueError,'hash differs'):builder.download({'sha256':'0'*64},p)
                network.assert_not_called()

    def test_build_path_normalization_preserves_feature_guard(self):
        with tempfile.TemporaryDirectory() as d:
            p=Path(d)/'config.h'
            p.write_text('#define FFMPEG_CONFIGURATION "path-a"\n#define FFMPEG_DATADIR "path-a"\n#define CONFIG_GPL 0\n')
            old=builder.config_digest(p)
            p.write_text('#define FFMPEG_CONFIGURATION "path-b"\n#define FFMPEG_DATADIR "path-b"\n#define CONFIG_GPL 0\n')
            self.assertEqual(old,builder.config_digest(p))
            p.write_text('#define CONFIG_GPL 1\n')
            self.assertNotEqual(old,builder.config_digest(p))

    def test_extra_source_modification_refused_before_abi_check(self):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d)/'root';work=Path(d)/'work';(root/'Video/.github/build').mkdir(parents=True);(work/'downloads').mkdir(parents=True);(work/'build/test').mkdir(parents=True)
            archive=work/'downloads/ffmpeg.tar.gz'
            with tarfile.open(archive,'w:gz') as packed:
                data=b'original implementation';info=tarfile.TarInfo('FFmpeg/codec.c');info.size=len(data);packed.addfile(info,io.BytesIO(data))
            lock={'inputs':{'ffmpeg':{'sha256':builder.digest(archive)}},'patched_source_sha256':{}}
            (root/'Video/.github/build/ffmpeg-foundation.lock.json').write_text(json.dumps(lock));(work/'build/test/codec.c').write_bytes(b'unreviewed implementation');(work/'build/test/config.h').write_text('')
            with patch.object(builder,'ABIS',['test']):
                with self.assertRaisesRegex(ValueError,'source differs beyond'):builder.check_candidate(root,work,{})

    def fixture(self,path):
        evidence={'source_commit':'894da5ca7d742e4429ffb2af534fcda0103ef593','abis':{abi:{'libraries':[{'file':name,'sha256':hashlib.sha256(b'candidate-'+name.encode()).hexdigest()} for name in sorted(apk_guard.LIBRARIES)]} for abi in apk_guard.ABIS}}
        archive=path/'app.apk';record=path/'native.json';record.write_text(json.dumps(evidence))
        with zipfile.ZipFile(archive,'w') as packed:
            for abi in apk_guard.ABIS:
                for name in apk_guard.LIBRARIES:packed.writestr('lib/'+abi+'/'+name,b'candidate-'+name.encode())
        return archive,record,evidence

    def test_actual_apk_covers_all_24_rebuilt_inputs(self):
        with tempfile.TemporaryDirectory() as d:
            archive,record,_=self.fixture(Path(d));self.assertEqual(24,apk_guard.verify(archive,record))

    def test_stale_apk_byte_refused(self):
        with tempfile.TemporaryDirectory() as d:
            archive,record,_=self.fixture(Path(d))
            data={}
            with zipfile.ZipFile(archive) as packed:
                data={name:packed.read(name) for name in packed.namelist()}
            data['lib/arm64-v8a/libavcodec.so']=b'old prebuilt'
            with zipfile.ZipFile(archive,'w') as packed:
                for name,value in data.items():packed.writestr(name,value)
            with self.assertRaisesRegex(ValueError,'stale or changed'):apk_guard.verify(archive,record)

    def test_missing_abi_evidence_refused(self):
        with tempfile.TemporaryDirectory() as d:
            archive,record,evidence=self.fixture(Path(d));evidence['abis'].pop('x86');record.write_text(json.dumps(evidence))
            with self.assertRaisesRegex(ValueError,'four-ABI'):apk_guard.verify(archive,record)

    def test_installation_refused_without_matching_runtime_bytes(self):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d)/'root';work=Path(d)/'work';(root/'Video/docs/foundation').mkdir(parents=True);work.mkdir()
            (root/'Video/docs/foundation/FFMPEG_REBUILD_REVIEWED.json').write_text('{"abis": {}}')
            report={'abis':{abi:{'libraries':[{'sha256':'new bytes'}]} for abi in builder.ABIS}}
            regression={'all_comparisons_passed':True,'total_decode_cases':64,'abis':[{'abi':abi,'all_comparisons_passed':True} for abi in builder.ABIS],'candidate_libraries':{abi:[{'sha256':'different tested bytes'}] for abi in builder.ABIS}}
            (work/'native-runtime-regression.json').write_text(json.dumps(regression))
            with patch('sys.argv',['builder','--root',str(root),'--workspace',str(work),'--verify-existing','--install']),patch.object(builder,'check_candidate',return_value=report),patch.object(builder.shutil,'copytree') as copy:
                with self.assertRaisesRegex(ValueError,'exact candidate bytes'):builder.main()
                copy.assert_not_called()

if __name__=='__main__':unittest.main()
