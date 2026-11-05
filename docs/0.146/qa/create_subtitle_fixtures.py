from pathlib import Path
import struct
r=Path('/workspace/native146-runtime/corpus');r.joinpath('qa.srt').write_text('1\n00:00:01,000 --> 00:00:03,000\nSupernova subtitle ABC\n\n');r.joinpath('qa.vtt').write_text('WEBVTT\n\n00:01.000 --> 00:03.000\nSupernova subtitle ABC\n\n');r.joinpath('qa.ass').write_text('[Script Info]\nScriptType: v4.00+\nPlayResX: 640\nPlayResY: 360\n[V4+ Styles]\nFormat: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding\nStyle: Default,Roboto Flex,32,&H00FFFFFF,&H00FFFFFF,&H00000000,&H80000000,0,0,0,0,100,100,0,0,1,2,0,2,10,10,15,1\n[Events]\nFormat: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\nDialogue: 0,0:00:01.00,0:00:03.00,Default,,0,0,0,,Supernova subtitle ABC\n')
def packet(pts,kind,data):return b'PG'+struct.pack('>IIBH',pts,pts,kind,len(data))+data
pcs=struct.pack('>HHBHBBBBHBBHH',640,360,0x10,0,0x80,0,0,1,0,0,0,100,200)
wds=struct.pack('>BBHHHH',1,0,100,200,20,10)
pds=bytes([0,0,0,16,128,128,0,1,235,128,128,255])
rle=(bytes([1])*20+b'\0\0')*10;payload=struct.pack('>HBB',0,0,0xc0)+(len(rle)+4).to_bytes(3,'big')+struct.pack('>HH',20,10)+rle
clear=struct.pack('>HHBHBBBB',640,360,0x10,1,0,0,0,0)
r.joinpath('qa.sup').write_bytes(b''.join(packet(90000,k,d) for k,d in [(0x16,pcs),(0x17,wds),(0x14,pds),(0x15,payload),(0x80,b'')])+packet(270000,0x16,clear)+packet(270000,0x80,b''))
