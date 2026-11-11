"""Structural checks only; passing does not establish academic correctness."""
from pathlib import Path
import re,sys
ROOT=Path(__file__).resolve().parents[1];seen=set();total=0;errors=[]
plans={'ipas':(8,[("biasa",50),("bergambar",20)]),'pai':(10,[(None,30)]),'eng':(10,[("biasa",35),("bergambar",10),("listening",5)]),'pjk':(8,[("biasa",35),("bergambar",10)]),'sbd':(5,[("biasa",35),("bergambar",10)])}
for folder,(chapters,packs) in plans.items():
 count=0
 for ch in range(1,chapters+1):
  for suffix,expected in packs:
   file=ROOT/f'kelas-3/{folder}/{folder}-b{ch:02d}-soal{("-"+suffix) if suffix else ""}.md'
   if not file.exists():errors.append(f'Missing {file.relative_to(ROOT)}');continue
   txt=file.read_text();heads=list(re.finditer(r'^## Soal (\d+).*?(K03-[A-Z]+-B\d{2}-M01-\d{3})\s*$',txt,re.M))
   if len(heads)!=expected:errors.append(f'{file.name}: expected {expected}, got {len(heads)}')
   for i,h in enumerate(heads):
    ident=h[2];count+=1
    if ident in seen:errors.append('Duplicate ID '+ident)
    seen.add(ident);block=txt[h.end():heads[i+1].start() if i+1<len(heads) else len(txt)]
    options=re.findall(r'^- (.+)$',block,re.M);key=re.search(r'^\*\*(?:Kunci Jawaban|Kunci|Jawaban):\*\*\s*([^\n]+)',block,re.M|re.I)
    if len(options)!=4 or len(set(options))!=4:errors.append(ident+': four distinct options required')
    if not key or key[1].strip() not in options:errors.append(ident+': key not in options')
    for alt,src in re.findall(r'!\[([^\]]*)\]\(([^)]+)\)',block):
     target=ROOT/src.split('/main/')[1] if '/main/' in src else file.parent/src
     if not alt.strip() or not target.is_file():errors.append(ident+': image/alt missing '+src)
 print(folder,count)
 total+=count
print('TOTAL',total,'ERRORS',len(errors))
for error in errors:print(error)
sys.exit(bool(errors))
