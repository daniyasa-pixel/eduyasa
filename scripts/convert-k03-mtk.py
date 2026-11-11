#!/usr/bin/env python3
"""Convert only uploaded Kelas 3 MTK Markdown, preserving LaTeX exactly.

Run with --write to generate JSON, or --check to verify a reproducible conversion.
Only document headings, field labels, and separators between records are removed.
Teaching text, IDs, option order, answer text, explanations, and image links survive.
"""
import argparse
import hashlib
import json
import re
from pathlib import Path
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
DIRECTORY = ROOT / 'kelas-3/mtk'
SUBJECT = 'MTK'
SOURCE = {
    'title': 'Matematika untuk SD/MI Kelas III',
    'year': 2022,
    'authors': ['Susanto', 'Arika Indah Kristiana', 'Arif Fatahillah', 'Eko Waluyo', 'Ridho Alfarisi', 'Hobri'],
    'isbn_pdf': '978-602-427-935-6',
    'catalog_url': 'https://buku.kemendikdasmen.go.id/katalog/matematika-untuk-sdmi-kelas-iii',
    'metadata_note': 'Identitas buku mengikuti README unggahan. Tujuan dirangkum dari cakupan sumber; teks dan rumus asli dipertahankan.'
}
GOALS = {
    1: ['Membaca dan menulis bilangan cacah sampai 1.000 serta memahami nilai tempat.',
        'Membandingkan dan mengurutkan bilangan.',
        'Memahami penjumlahan, pengurangan, perkalian, dan pembagian dalam cakupan sampai 100.'],
    2: ['Menyusun kalimat matematika penjumlahan dan pengurangan dari cerita.',
        'Menentukan bilangan yang belum diketahui dengan hubungan penjumlahan dan pengurangan.',
        'Memeriksa hasil dengan memasukkan jawaban kembali ke kalimat matematika.'],
    3: ['Memilih alat dan satuan baku untuk mengukur panjang dan massa benda.',
        'Membaca pengukuran dan memahami hubungan cm, m, km serta g dan kg.',
        'Membandingkan dan menghitung pengukuran setelah menyamakan satuan.'],
    4: ['Mengenali sisi dan titik sudut bangun datar serta ruas garis, sinar garis, dan garis lurus.',
        'Membedakan sudut lancip, siku-siku, tumpul, dan lurus.',
        'Mengenali pasangan garis sejajar dan tegak lurus.'],
    5: ['Membaca judul, kategori, dan banyak data dalam tabel.',
        'Membandingkan, mengurutkan, menjumlahkan, dan menghitung selisih data.',
        'Mengumpulkan data sederhana dan menyajikannya dengan jujur dalam tabel.']
}

IMAGE = re.compile(r'^!\[([^\]]*)\]\(([^)]+)\)[ \t]*$', re.MULTILINE)
QUESTION = re.compile(r'^## Soal (\d+) — (K03-MTK-B\d{2}-M01-\d{3})[ \t]*$', re.MULTILINE)
FIELD = re.compile(r'^\*\*(Pertanyaan|Pilihan jawaban|Kunci jawaban|Pembahasan):\*\*[ \t]*', re.MULTILINE | re.IGNORECASE)
MODULE = re.compile(r'^## (K03-MTK-B\d{2}-MOD-\d{2})[ \t]*$', re.MULTILINE)

def require(condition, message):
    if not condition:
        raise ValueError(message)

def body(text):
    # The standalone --- immediately before the next record is a file separator.
    return re.sub(r'\n---\s*\Z', '', text.strip()).strip()

def provenance(file):
    return {'path': file.relative_to(ROOT).as_posix(), 'sha256': hashlib.sha256(file.read_bytes()).hexdigest()}

def validate_image(src, alt):
    prefix = 'https://raw.githubusercontent.com/daniyasa-pixel/eduyasa/main/'
    require(src.startswith(prefix), f'Unexpected image host: {src}')
    relative = src[len(prefix):]
    require(relative.startswith('kelas-3/mtk/assets/') and '..' not in Path(relative).parts,
            f'Image outside MTK: {relative}')
    require(alt.strip(), f'Missing alternative text: {src}')
    file = ROOT / relative
    require(file.is_file(), f'Image missing: {relative}')
    tree = ET.fromstring(file.read_text())
    require(tree.tag == '{http://www.w3.org/2000/svg}svg', f'Not an SVG: {relative}')
    require(not any(el.tag.rsplit('}', 1)[-1] == 'script' for el in tree.iter()), f'Script in SVG: {relative}')
    return {'src': src, 'alt': alt}

def questions(file, chapter, illustrated):
    text = file.read_text()
    matches = list(QUESTION.finditer(text))
    require(len(matches) == (20 if illustrated else 50), f'{file.name}: wrong question count')
    result = []
    for index, heading in enumerate(matches):
        number = int(heading[1])
        expected = index + (51 if illustrated else 1)
        require(number == expected and heading[2] == f'{chapter}-M01-{expected:03d}',
                f'{file.name}: question ID/order mismatch at {heading[2]}')
        block = body(text[heading.end(): matches[index+1].start() if index+1 < len(matches) else len(text)])
        fields = list(FIELD.finditer(block))
        require([f[1].lower() for f in fields] == ['pertanyaan', 'pilihan jawaban', 'kunci jawaban', 'pembahasan'],
                f'{heading[2]}: missing, duplicate, or unordered fields')
        values = [block[f.end(): fields[i+1].start() if i+1 < len(fields) else len(block)].strip()
                  for i, f in enumerate(fields)]
        options = re.split(r'^- ', values[1], flags=re.MULTILINE)
        require(not options[0].strip(), f'{heading[2]}: text outside option bullets')
        options = [option.strip() for option in options[1:]]
        require(len(options) == len(set(options)) == 4 and all(options), f'{heading[2]}: four unique options required')
        require(values[2] in options, f'{heading[2]}: answer does not match an option')
        require(values[0] and values[3], f'{heading[2]}: missing question/discussion')
        images = list(IMAGE.finditer(block[:fields[0].start()]))
        prefix = IMAGE.sub('', block[:fields[0].start()]).strip()
        require(not prefix, f'{heading[2]}: unexpected text before question')
        require(len(images) == int(illustrated), f'{heading[2]}: unexpected image count')
        item = {'id': heading[2], 'type': 'PG', 'text': values[0], 'options': options,
                'answer': values[2], 'discussion': values[3]}
        if illustrated:
            item['image'] = validate_image(images[0][2], images[0][1])
        result.append(item)
    return result

def materials(file, chapter):
    text = file.read_text()
    matches = list(MODULE.finditer(text))
    require(len(matches) == 2, f'{file.name}: two modules required')
    title = text.splitlines()[0].removeprefix('# ').split(' — Dua Modul')[0]
    header = text[:matches[0].start()]
    goals = GOALS[int(chapter[-2:])]
    result = []
    for index, match in enumerate(matches):
        require(match[1] == f'{chapter}-MOD-{index+1:02d}', f'{file.name}: module ID mismatch')
        content = body(text[match.end():matches[index+1].start() if index+1 < len(matches) else len(text)])
        module_title = re.match(r'# (.+)\n', content)
        require(module_title, f'{match[1]}: title missing')
        images = list(IMAGE.finditer(content))
        require(len(images) == 2, f'{match[1]}: two material illustrations required')
        for image in images:
            validate_image(image[2], image[1])
            require(image.start() > 30 and image.end() < len(content)-30, f'{match[1]}: images must remain within teaching text')
        result.append({'id': match[1], 'version': f'MOD-{index+1:02d}', 'title': module_title[1],
                       'learning_objectives': goals, 'content': content})
    return title, goals, result

def build():
    outputs = {}
    manifest = {'subject_id': SUBJECT, 'grade': 3, 'chapters': {}}
    seen = set()
    for number in range(1, 6):
        stem = f'mtk-b{number:02d}'
        chapter = f'K03-MTK-B{number:02d}'
        material_file = DIRECTORY / f'{stem}-materi.md'
        title, goals, modules = materials(material_file, chapter)
        meta = {'subject_id': SUBJECT, 'subject_name': 'Matematika', 'grade': 3,
                'curriculum': 'Kurikulum Merdeka', 'source': SOURCE}
        outputs[DIRECTORY/f'{stem}-materi.json'] = {
            **meta, 'chapter_id': chapter, 'chapter_title': title, 'learning_objectives': goals,
            'source_markdown': provenance(material_file), 'modules': modules}
        for illustrated, suffix, source_suffix in [(False, '', 'biasa'), (True, 'a', 'bergambar')]:
            file = DIRECTORY/f'{stem}-soal-{source_suffix}.md'
            items = questions(file, chapter, illustrated)
            for item in items:
                require(item['id'] not in seen, f'Duplicate ID: {item["id"]}')
                seen.add(item['id'])
            outputs[DIRECTORY/f'{stem}{suffix}.json'] = {
                **meta, 'source_markdown': provenance(file),
                'chapters': [{'chapter_id': chapter, 'chapter_title': title, 'questions': items}]}
        manifest['chapters'][chapter] = [f'mtk/{stem}.json', f'mtk/{stem}a.json']
    outputs[DIRECTORY/'question-packs.json'] = manifest
    return outputs

def validate_math_round_trip(data):
    encoded = json.dumps(data, ensure_ascii=False)
    require(json.loads(encoded) == data, 'LaTeX/text JSON round trip failed')
    def walk(value):
        if isinstance(value, dict):
            for item in value.values():
                walk(item)
        elif isinstance(value, list):
            for item in value:
                walk(item)
        elif isinstance(value, str):
            require(not any(ord(c) < 32 and c not in '\n\r\t' for c in value), 'Unexpected control character')
            require('${' not in value, 'Unresolved template expression')
            # Uploaded content uses dollar delimiters. A literal escaped dollar is excluded.
            require(len(re.findall(r'(?<!\\)\$', value)) % 2 == 0, 'Unpaired math delimiter')
    walk(data)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument('--write', action='store_true')
    group.add_argument('--check', action='store_true')
    args = parser.parse_args()
    # Validate every source first, so a malformed later chapter produces no partial conversion.
    outputs = build()
    for file, data in outputs.items():
        validate_math_round_trip(data)
        encoded = json.dumps(data, ensure_ascii=False, indent=2) + '\n'
        if args.write:
            file.write_text(encoded)
        else:
            require(file.is_file() and file.read_text() == encoded, f'JSON differs from source: {file.relative_to(ROOT)}')
    print(f'MTK only: {len(outputs)} JSON files, 5 chapters, 10 modules, 250 ordinary + 100 illustrated questions; '+
          ('written.' if args.write else 'source fidelity verified.'))

if __name__ == '__main__':
    main()
