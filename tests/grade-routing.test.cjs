const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const bank = require('../HTML/quiz-bank.js');
const grades = require('../HTML/grade-config.js');
const root = path.resolve(__dirname, '..');
const raw = 'https://example.test/';
const config = grades.resolve('?kelas=3');
const fetcher = async url => {
  const file = path.join(root, url.slice(raw.length));
  return fs.existsSync(file) ? { ok: true, json: async () => JSON.parse(fs.readFileSync(file, 'utf8')) } : { ok: false, status: 404 };
};
const load = (chapters, fetchFn = fetcher) => bank.loadQuestions({
  baseUrl: raw + config.dataPath, subjects: config.subjects, subjectIds: ['BIN'],
  selectedChapters: { BIN: new Set(chapters) }, chapterPrefix: config.chapterPrefix,
  manifestSubjects: config.manifestSubjects, grade: config.grade, fetcher: fetchFn
});
function seeded(seed) { return () => ((seed = (seed * 1664525 + 1013904223) >>> 0) / 4294967296); }

test('URL lama tetap Kelas 6; Kelas 3 memakai folder dan awalan terpisah; kelas belum tersedia ditolak', () => {
  assert.equal(grades.resolve('').grade, 6);
  assert.equal(grades.resolve('?v=old').dataPath, '');
  assert.equal(grades.resolve('?kelas=6').chapterPrefix, '');
  assert.equal(config.grade, 3);
  assert.equal(config.dataPath, 'kelas-3/');
  assert.equal(config.chapterPrefix, 'K03-');
  assert.deepEqual(config.subjects.map(s => s.id), ['BIN', 'PKN']);
  assert.equal(config.subjects[0].chapters.length, 4);
  for (const query of ['?kelas=1', '?kelas=2', '?kelas=4', '?kelas=5', '?kelas=0', '?kelas=xyz']) {
    assert.throws(() => grades.resolve(query), /Kelas belum tersedia/);
  }
});
for (let chapter = 1; chapter <= 4; chapter++) {
  test(`BIN Kelas 3 Bab ${chapter}: tepat dua paket dari kelas-3, 70 ID terpisah, 100 kali acak 5/15`, async () => {
    const requests = [];
    const pool = await load([chapter], async url => { requests.push(url); return fetcher(url); });
    assert.deepEqual(requests, ['bin/question-packs.json', `bin/bin-b0${chapter}.json`, `bin/bin-b0${chapter}a.json`].map(p => raw + 'kelas-3/' + p));
    assert.equal(pool.length, 70);
    assert.equal(pool.filter(bank.hasImage).length, 20);
    assert.equal(new Set(pool.map(q => q.id)).size, 70);
    assert(pool.every(q => q.chapterId === `K03-BIN-B0${chapter}` && q.id.startsWith(`K03-BIN-B0${chapter}-`)));
    for (const q of pool) {
      assert.equal(q.options.length, 4); assert.equal(new Set(q.options).size, 4);
      assert(q.options.includes(q.correctText)); assert(q.explanation.length > 25);
      if (q.image) {
        assert(q.image.src.includes('/main/kelas-3/bin/assets/'));
        assert(q.image.alt.length > 40);
        assert(fs.existsSync(path.join(root, q.image.src.split('/main/')[1])));
      }
    }
    for (let seed = 1; seed <= 100; seed++) {
      const quiz = bank.selectQuestions(pool, { fixedImageRatio: true, random: seeded(seed) });
      assert.equal(quiz.length, 20); assert.equal(quiz.filter(bank.hasImage).length, 5);
      assert.equal(new Set(quiz.map(q => q.id)).size, 20);
      const shuffled = bank.shuffle(quiz[0].options, seeded(seed));
      assert(shuffled.includes(quiz[0].correctText));
    }
    const runs = [2, 9, 17].map(seed => bank.selectQuestions(pool, { fixedImageRatio: true, random: seeded(seed) }));
    assert.notDeepEqual(runs[0].map(q => q.id), runs[1].map(q => q.id));
    assert(runs.some(run => run.map(bank.hasImage).join('') !== 'truetruetruetruetruefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalsefalse'));
  });
}

test('Gabungan empat bab Kelas 3 tetap 20 soal tanpa mengambil bank BIN Kelas 6', async () => {
  const pool = await load([1, 2, 3, 4]);
  assert.equal(pool.length, 280);
  const legacy = await bank.loadQuestions({ baseUrl: raw, subjects: [{ id: 'BIN', name: 'Bahasa Indonesia' }], subjectIds: ['BIN'], selectedChapters: { BIN: new Set([1]) }, fetcher });
  assert.equal(legacy.length, 50);
  const legacyIds = new Set(legacy.map(q => q.id));
  assert(pool.every(q => !legacyIds.has(q.id)));
  for (let seed = 1; seed <= 100; seed++) {
    const quiz = bank.selectQuestions(pool, { fixedImageRatio: true, random: seeded(seed) });
    assert.equal(quiz.length, 20); assert.equal(quiz.filter(bank.hasImage).length, 5);
    assert(quiz.every(q => q.id.startsWith('K03-BIN-')));
  }
});

test('Data kelas/bab salah, manifest salah, dan paket A gagal harus menghentikan kuis', async () => {
  for (const mode of ['grade', 'chapter', 'manifest', 'missing']) {
    await assert.rejects(load([1], async url => {
      if (mode === 'missing' && url.endsWith('bin-b01a.json')) return { ok: false, status: 503 };
      const response = await fetcher(url);
      const data = await response.json();
      if (mode === 'manifest' && url.endsWith('question-packs.json')) data.chapters['K03-BIN-B01'] = ['../bin/bin-b01.json'];
      if (url.endsWith('bin-b01a.json')) {
        if (mode === 'grade') data.grade = 6;
        if (mode === 'chapter') data.chapters[0].chapter_id = 'BIN-B01';
      }
      return { ok: true, json: async () => data };
    }), /kelas yang tidak sesuai|bab yang tidak sesuai|Nama paket|HTTP 503/);
  }
  assert.equal((await load([1])).length, 70);
});

test('Delapan modul Kelas 3: dua versi setara, teks berbeda, gambar di tengah, sumber dan ID sesuai', () => {
  const ids = new Set();
  for (let ch = 1; ch <= 4; ch++) {
    const d = JSON.parse(fs.readFileSync(path.join(root, `kelas-3/bin/bin-b0${ch}-materi.json`)));
    assert.equal(d.grade, 3); assert.equal(d.chapter_id, `K03-BIN-B0${ch}`);
    assert.equal(d.source.year, 2025); assert.equal(d.modules.length, 2);
    assert.notEqual(d.modules[0].content, d.modules[1].content);
    for (const m of d.modules) {
      assert(!ids.has(m.id)); ids.add(m.id);
      assert.deepEqual(m.learning_objectives, d.learning_objectives);
      const images = [...m.content.matchAll(/!\[[^\]]*\]\(([^)]+)\)/g)]; assert.equal(images.length, 2);
      for (const image of images) {
        assert(fs.existsSync(path.join(root, image[1].split('/main/')[1])));
        assert(image.index > 50 && image.index < m.content.length - 100);
      }
      const words = m.content.replace(/!\[[^\]]*\]\([^)]+\)/g, '').split(/\s+/).length;
      assert(words >= 200 && words <= 350, `${m.id}: ${words}`);
    }
  }
});
