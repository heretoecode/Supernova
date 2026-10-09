#!/usr/bin/env python3
"""Deterministic platform composition from approved, unchanged source artwork."""
import argparse
import hashlib
import json
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont, ImageOps
ROOT=Path(__file__).resolve().parents[1]

def export(font_path):
    source=ROOT/'assets/branding'
    background=Image.open(source/'Supernova_Space_Black_Blend_4K_UHD.png').convert('RGBA')
    ring=Image.open(source/'Supernova_Double_Ring_Final_Visual_Reference.png').convert('RGBA')
    if ring.getchannel('A').getextrema() != (0,254): raise ValueError('Approved ring alpha changed; review source before exporting')
    out=ROOT/'res/drawable-nodpi';out.mkdir(exist_ok=True)
    # Composite only: no painting, transparency reconstruction or glow/colour edits.
    def canvas(size): return ImageOps.fit(background,size,method=Image.Resampling.LANCZOS)
    def symbol(image,size,x,y):
        fitted=ImageOps.contain(ring,(size,size),Image.Resampling.LANCZOS)
        image.alpha_composite(fitted,(round(x+(size-fitted.width)/2),round(y+(size-fitted.height)/2)))
    icon=canvas((512,512));symbol(icon,400,56,56);icon.convert('RGB').save(out/'foundation_icon.png',optimize=True)
    for density,pixels in [('mdpi',48),('hdpi',72),('xhdpi',96),('xxhdpi',144),('xxxhdpi',192)]:
        folder=ROOT/('res/mipmap-'+density);folder.mkdir(exist_ok=True)
        icon.convert('RGB').resize((pixels,pixels),Image.Resampling.LANCZOS).save(folder/'foundation_launcher.png',optimize=True)
    canvas((512,512)).convert('RGB').save(out/'foundation_icon_background.png',optimize=True)
    adaptive=Image.new('RGBA',(1024,1024));symbol(adaptive,540,242,242)
    adaptive.save(out/'foundation_adaptive_foreground.png',optimize=True)
    banner=canvas((3840,2160));font=ImageFont.truetype(str(font_path),288)
    text='SUPERNOVA';draw=ImageDraw.Draw(banner);box=draw.textbbox((0,0),text,font=font)
    width=box[2]-box[0];size=864;gap=288;left=(3840-size-gap-width)/2
    symbol(banner,size,left,(2160-size)/2)
    draw.text((left+size+gap-box[0],(2160-(box[3]-box[1]))/2-box[1]),text,font=font,fill='white')
    banner.convert('RGB').resize((320,180),Image.Resampling.LANCZOS).save(out/'foundation_banner.png',optimize=True)
    splash=canvas((3840,2160));draw=ImageDraw.Draw(splash);font=ImageFont.truetype(str(font_path),180)
    box=draw.textbbox((0,0),text,font=font);size=540;gap=132;height=size+gap+box[3]-box[1];top=(2160-height)/2
    symbol(splash,size,(3840-size)/2,top)
    draw.text(((3840-(box[2]-box[0]))/2-box[0],top+size+gap-box[1]),text,font=font,fill='white')
    splash.convert('RGB').resize((1920,1080),Image.Resampling.LANCZOS).save(out/'foundation_splash_image.png',optimize=True)
    records={}
    for p in [source/'Supernova_Space_Black_Blend_4K_UHD.png',source/'Supernova_Double_Ring_Final_Visual_Reference.png',Path(font_path),*sorted(out.glob('foundation_*.png')),*sorted((ROOT/'res').glob('mipmap-*/foundation_launcher.png'))]:
        records[str(p.relative_to(ROOT)) if p.is_relative_to(ROOT) else p.name]={'sha256':hashlib.sha256(p.read_bytes()).hexdigest()}
        if p.suffix=='.png':
            im=Image.open(p);records[str(p.relative_to(ROOT)) if p.is_relative_to(ROOT) else p.name].update(size=list(im.size),mode=im.mode)
    (ROOT/'docs/foundation/BRANDING_EXPORTS.json').write_text(json.dumps({'method':'Unchanged approved artwork, resized/composited; Roboto Light, natural tracking','banner_ring_pixels':72,'banner_gap_pixels':24,'banner_font_pixels':24,'files':records},indent=2)+'\n')

if __name__=='__main__':
    parser=argparse.ArgumentParser();parser.add_argument('--roboto-light',type=Path,required=True)
    export(parser.parse_args().roboto_light)
