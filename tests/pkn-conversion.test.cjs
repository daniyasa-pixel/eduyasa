const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');
const { execFileSync } = require('node:child_process');
const bank = require('../HTML/quiz-bank.js');
const config = require('../HTML/grade-config.js').resolve('?kelas=3');
const root = path.resolve(__dirname, '..');
const base = 'https://example.test/kelas-3/';
const fetcher = async url => {
  const file = path.join(root, 'kelas-3', url.slice(base.length));
  return fs.existsSync(file) ? { ok: true, json: async () => JSON.parse(fs.readFileSync(file)) } : { ok: false, status: 404 };
};
const load = (selection, fetchFn = fetcher) => bank.loadQuestions({
  baseUrl: base, subjectIds: Object.keys(selection), selectedChapters: selection,
  subjects: config.subjects, chapterPrefix: config.chapterPrefix,
  manifestSubjects: config.manifestSubjects, grade: config.grade, fetcher: fetchFn
});
const read = name => JSON.parse(fs.readFileSync(path.join(root, 'kelas-3/pkn', name)));
function seeded(seed) { return () => ((seed = (seed * 1664525 + 1013904223) >>> 0) / 4294967296); }

for (let ch = 1; ch <= 4; ch++) {
  test(`PKN Kelas 3 Bab ${ch}: konversi mempertahankan setiap teks, opsi, kunci, pembahasan, dan media sumber`, async () => {
    for (const [suffix, kind, count] of [['', 'biasa', 50], ['a', 'bergambar', 20]]) {
      const d = read(`pkn-b0${ch}${suffix}.json`);
      const source = fs.readFileSync(path.join(root, `kelas-3/pkn/pkn-b0${ch}-soal-${kind}.md`), 'utf8');
      assert.equal(d.source_markdown.sha256, crypto.createHash('sha256').update(source).digest('hex'));
      assert.equal(d.chapters[0].chapter_id, `K03-PKN-B0${ch}`);
      const questions = d.chapters[0].questions; assert.equal(questions.length, count);
      for (const q of questions) {
        const heading = source.indexOf(' — ' + q.id + '\n'); assert(heading >= 0);
        const next = source.indexOf('\n## Soal ', heading);
        const block = source.slice(heading, next < 0 ? undefined : next);
        assert(block.includes('**Pertanyaan:** ' + q.text));
        assert(block.includes('**Pilihan jawaban:**\n' + q.options.map(o => '- ' + o).join('\n')));
        assert(block.includes('**Kunci jawaban:** ' + q.answer));
        assert(block.includes('**Pembahasan:** ' + q.discussion));
        assert.equal(q.options.length, 4); assert.equal(new Set(q.options).size, 4); assert(q.options.includes(q.answer));
        assert.equal(bank.hasImage(q), kind === 'bergambar');
        if (q.image) {
          assert(block.includes(`![${q.image.alt}](${q.image.src})`));
          assert(fs.existsSync(path.join(root, q.image.src.split('/main/')[1])));
        }
      }
    }
    const pool = await load({ PKN: new Set([ch]) }); assert.equal(pool.length, 70);
    assert.equal(pool.filter(bank.hasImage).length, 20); assert.equal(new Set(pool.map(q => q.id)).size, 70);
    assert(pool.every(q => q.chapterId === `K03-PKN-B0${ch}`));
    for (let seed = 1; seed <= 100; seed++) {
      const quiz = bank.selectQuestions(pool, { fixedImageRatio: true, random: seeded(seed) });
      assert.equal(quiz.length, 20); assert.equal(quiz.filter(bank.hasImage).length, 5);
      assert.equal(new Set(quiz.map(q => q.id)).size, 20);
    }
  });
}

test('Delapan modul PKN mempertahankan konten Markdown lengkap dan 48 aset asli', () => {
  const referenced = new Set();
  for (let ch = 1; ch <= 4; ch++) {
    const d = read(`pkn-b0${ch}-materi.json`);
    const source = fs.readFileSync(path.join(root, `kelas-3/pkn/pkn-b0${ch}-materi.md`), 'utf8');
    assert.equal(d.source_markdown.sha256, crypto.createHash('sha256').update(source).digest('hex'));
    assert.equal(d.grade, 3); assert.equal(d.modules.length, 2);
    assert.deepEqual(d.modules.map(m => m.id), [1, 2].map(v => `K03-PKN-B0${ch}-MOD-0${v}`));
    for (const m of d.modules) {
      assert(source.includes(m.content)); assert.deepEqual(m.learning_objectives, d.learning_objectives);
      const images = [...m.content.matchAll(/!\[[^\]]*\]\(([^)]+)\)/g)]; assert.equal(images.length, 2);
      for (const image of images) {
        assert(image.index > 30 && image.index + image[0].length < m.content.length - 30);
        referenced.add(image[1]);
      }
    }
    for (const q of read(`pkn-b0${ch}a.json`).chapters[0].questions) referenced.add(q.image.src);
  }
  assert.equal(referenced.size, 48);
  for (const src of referenced) {
    const svg = fs.readFileSync(path.join(root, src.split('/main/')[1]), 'utf8');
    assert(svg.includes('<svg') && svg.includes('<title') && svg.includes('<desc')); assert(!svg.includes('<script'));
  }
});

test('PKN multi-bab dan gabungan BIN+PKN mengikuti pilihan Kelas 3 dengan kuota sesi 5/15', async () => {
  for (const selection of [{ PKN: new Set([1, 2, 3, 4]) }, { BIN: new Set([1]), PKN: new Set([2]) }]) {
    const pool = await load(selection);
    assert.equal(pool.length, Object.values(selection).reduce((n, chapters) => n + chapters.size, 0) * 70);
    for (let seed = 1; seed <= 100; seed++) {
      const quiz = bank.selectQuestions(pool, { fixedImageRatio: true, random: seeded(seed) });
      assert.equal(quiz.length, 20); assert.equal(quiz.filter(bank.hasImage).length, 5);
      assert(quiz.every(q => q.id.startsWith('K03-') && selection[q.subjectId]));
    }
  }
});

test('Paket A PKN gagal: kuis diblokir, tidak diganti dengan soal biasa; retry memuat kedua paket', async () => {
  await assert.rejects(load({ PKN: new Set([1]) }, async url => url.endsWith('pkn-b01a.json') ? { ok: false, status: 503 } : fetcher(url)), /pkn-b01a.json.*HTTP 503/);
  const requests = [];
  const pool = await load({ PKN: new Set([1]) }, async url => { requests.push(url); return fetcher(url); });
  assert.equal(pool.length, 70);
  assert.deepEqual(requests, ['pkn/question-packs.json', 'pkn/pkn-b01.json', 'pkn/pkn-b01a.json'].map(p => base + p));
});

test('Konverter PKN dapat memverifikasi ulang JSON terhadap seluruh sumber tanpa menulis file', () => {
  const output = execFileSync('python', ['scripts/convert-k03-pkn.py', '--check'], { cwd: root, encoding: 'utf8' });
  assert(output.includes('source fidelity verified'));
});
