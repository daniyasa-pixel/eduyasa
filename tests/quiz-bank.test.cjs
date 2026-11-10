const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const bank = require('../HTML/quiz-bank.js');
const root = path.resolve(__dirname, '..');
const subjects = ['MTK', 'BIN', 'ENG'].map(id => ({ id, name: id }));
const baseUrl = 'https://example.test/';
const localFetch = async url => {
  const name = url.slice(baseUrl.length);
  const file = path.join(root, name);
  return fs.existsSync(file)
    ? { ok: true, json: async () => JSON.parse(fs.readFileSync(file, 'utf8')) }
    : { ok: false, status: 404 };
};
async function load(chapters, fetcher = localFetch) {
  return bank.loadQuestions({ baseUrl, subjectIds: Object.keys(chapters), selectedChapters: chapters, subjects, fetcher });
}
function seeded(seed) {
  return () => ((seed = (seed * 1664525 + 1013904223) >>> 0) / 4294967296);
}

for (let chapter = 1; chapter <= 4; chapter++) {
  test(`MTK bab ${chapter}: 50 soal awal + 20 soal bergambar, aset dan ID valid`, async () => {
    const questions = await load({ MTK: new Set([chapter]) });
    assert.equal(questions.length, 70);
    assert.equal(questions.filter(bank.hasImage).length, 20);
    assert.equal(new Set(questions.map(q => q.id)).size, 70);
    assert(questions.every(q => q.chapterId === `MTK-B0${chapter}`));
    for (const q of questions.filter(bank.hasImage)) {
      assert(q.image.alt.length > 20);
      const relative = q.image.src.split('/main/')[1];
      assert(fs.existsSync(path.join(root, relative)), relative);
      const svg = fs.readFileSync(path.join(root, relative), 'utf8');
      assert(svg.includes('<svg') && svg.includes('<desc') && !svg.includes('<script'));
    }
    const baseline = JSON.parse(fs.readFileSync(path.join(root, `mtk/mtk-b0${chapter}.json`), 'utf8'));
    assert.equal(baseline.chapters[0].questions.length, 50);
    for (let seed = 1; seed <= 100; seed++) {
      const result = bank.selectQuestions(questions, { mathSelected: true, random: seeded(seed) });
      assert.equal(result.length, 20);
      assert.equal(result.filter(bank.hasImage).length, 5);
      assert.equal(result.filter(q => !bank.hasImage(q)).length, 15);
      assert.equal(new Set(result.map(q => q.id)).size, 20);
    }
    const first = bank.selectQuestions(questions, { mathSelected: true, random: seeded(8) }).map(q => q.id);
    const next = bank.selectQuestions(questions, { mathSelected: true, random: seeded(19) }).map(q => q.id);
    assert.notDeepEqual(first, next);
  });
}

test('Beberapa bab MTK hanya mengambil soal dari bab terpilih, tepat 5 gambar', async () => {
  const pool = await load({ MTK: new Set([1, 3, 4]) });
  assert.equal(pool.length, 210);
  for (let seed = 1; seed <= 100; seed++) {
    const result = bank.selectQuestions(pool, { mathSelected: true, random: seeded(seed) });
    assert.equal(result.filter(bank.hasImage).length, 5);
    assert(result.every(q => ['MTK-B01', 'MTK-B03', 'MTK-B04'].includes(q.chapterId)));
  }
});

test('MTK + ENG: kuota 25/75 berlaku untuk gabungan soal terpilih', async () => {
  const pool = await load({ MTK: new Set([2]), ENG: new Set([3]) });
  assert.equal(pool.length, 120);
  for (let seed = 1; seed <= 100; seed++) {
    const result = bank.selectQuestions(pool, { mathSelected: true, random: seeded(seed) });
    assert.equal(result.filter(bank.hasImage).length, 5);
    assert.equal(result.length, 20);
    assert(result.every(q => ['MTK-B02', 'ENG-B03'].includes(q.chapterId)));
  }
});

test('Mapel tanpa MTK tidak membaca daftar paket atau diberi kuota baru', async () => {
  const requests = [];
  const pool = await load({ BIN: new Set([1]) }, async url => { requests.push(url); return localFetch(url); });
  assert.deepEqual(requests, [baseUrl + 'bin/bin-b01.json']);
  const result = bank.selectQuestions(pool, { random: seeded(1) });
  assert.equal(result.length, 20);
  assert.equal(result.filter(bank.hasImage).length, 0);
});

test('Paket B cukup didaftarkan dalam manifest tanpa perubahan kode frontend', async () => {
  const pool = await load({ MTK: new Set([1]) }, async url => {
    if (url.endsWith('question-packs.json')) return {
      ok: true,
      json: async () => ({ subject_id: 'MTK', chapters: { 'MTK-B01': ['mtk/mtk-b01.json', 'mtk/mtk-b01a.json', 'mtk/mtk-b01b.json'] } })
    };
    if (url.endsWith('mtk-b01b.json')) {
      const data = JSON.parse(fs.readFileSync(path.join(root, 'mtk/mtk-b01a.json')));
      data.chapters[0].questions = [ { ...data.chapters[0].questions[0], id: 'MTK-B01-M01-071' } ];
      return { ok: true, json: async () => data };
    }
    return localFetch(url);
  });
  assert.equal(pool.length, 71);
  assert(pool.some(q => q.id === 'MTK-B01-M01-071'));
});

test('Paket A gagal tidak menghasilkan latihan dengan proporsi yang salah; retry berhasil', async () => {
  await assert.rejects(load({ MTK: new Set([1]) }, url => url.endsWith('mtk-b01a.json')
    ? Promise.resolve({ ok: false, status: 404 }) : localFetch(url)), /mtk-b01a.json/);
  assert.equal((await load({ MTK: new Set([1]) })).length, 70);
});

test('Manifest gagal / JSON rusak menghasilkan pesan yang jelas', async () => {
  await assert.rejects(load({ MTK: new Set([1]) }, async () => ({ ok: false, status: 503 })), /HTTP 503/);
  await assert.rejects(load({ MTK: new Set([1]) }, async () => ({ ok: true, json: async () => { throw new SyntaxError(); } })), /bukan JSON/);
});

test('Kuota tidak boleh diisi dengan duplikasi atau diam-diam mengganti kategori', () => {
  const plain = Array.from({ length: 20 }, (_, id) => ({ id }));
  assert.throws(() => bank.selectQuestions(plain, { mathSelected: true }), /5 soal bergambar/);
  assert.throws(() => bank.selectQuestions(plain.slice(0, 10)), /kurang dari 20/);
  assert.equal(bank.hasImage({ image: { alt: 'tanpa sumber' } }), false);
});

test('ID duplikat dan bab paket yang tidak sesuai ditolak', async () => {
  const corrupt = mode => async url => {
    const response = await localFetch(url);
    if (!url.endsWith('mtk-b01a.json')) return response;
    const data = await response.json();
    if (mode === 'id') data.chapters[0].questions[0].id = 'MTK-B01-M01-001';
    else data.chapters[0].chapter_id = 'MTK-B02';
    return { ok: true, json: async () => data };
  };
  await assert.rejects(load({ MTK: new Set([1]) }, corrupt('id')), /ID soal berulang/);
  await assert.rejects(load({ MTK: new Set([1]) }, corrupt('chapter')), /bab yang tidak sesuai/);
});

test('Pengacakan opsi mempertahankan teks kunci; huruf literal bukan indeks', () => {
  const question = { id: 'sample', text: 'Huruf?', options: ['B', 'A', 'C', 'D'], answer: 'A' };
  const normalized = bank.normalizeQuestion(question, subjects[0], 1);
  assert.equal(normalized.correctText, 'A');
  const options = bank.shuffle(normalized.options, seeded(10));
  assert(options.includes(normalized.correctText));
  assert.deepEqual(question.options, ['B', 'A', 'C', 'D']);
});
