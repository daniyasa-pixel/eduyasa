const test = require('node:test');
const assert = require('node:assert/strict');
const { createRenderer } = require('../HTML/math-renderer.js');
class Script extends EventTarget { constructor() { super(); this.dataset = {}; } }
const tick = () => new Promise(resolve => setImmediate(resolve));

test('Rumus dinamis menunggu script MathJax dan startup-nya, lalu ditampilkan', async () => {
  const script = new Script(); let engine = {}; let completeStartup; const calls = [];
  const renderer = createRenderer({ script, getMathJax: () => engine });
  const element = { textContent: '$4\\times5=20$' };
  const rendering = renderer.render(element); await tick(); assert.equal(calls.length, 0);
  engine = { startup: { promise: new Promise(resolve => completeStartup = resolve) }, typesetPromise: async roots => calls.push(roots) };
  script.dispatchEvent(new Event('load')); await tick(); assert.equal(calls.length, 0);
  completeStartup(); assert.equal(await rendering, true); assert.deepEqual(calls, [[element]]);
});

test('Typesetting bersamaan dijalankan berurutan dan kegagalan satu elemen tidak merusak berikutnya', async () => {
  let finishFirst; const calls = []; const errors = [];
  const engine = { typesetPromise: roots => { calls.push(roots[0].textContent); return calls.length === 1 ? new Promise((resolve, reject) => finishFirst = () => reject(new Error('bad expression'))) : Promise.resolve(); } };
  const renderer = createRenderer({ getMathJax: () => engine, onError: e => errors.push(e.message) });
  const first = renderer.render({ textContent: '$1+1$' }); const second = renderer.render({ textContent: '$2+2$' });
  await tick(); assert.deepEqual(calls, ['$1+1$']); finishFirst();
  assert.equal(await first, false); assert.equal(await second, true);
  assert.deepEqual(calls, ['$1+1$', '$2+2$']); assert.deepEqual(errors, ['bad expression']);
});

test('Membersihkan hanya elemen yang akan diganti, tidak menghapus semua materi MathJax', () => {
  const calls = []; const renderer = createRenderer({ getMathJax: () => ({ typesetClear: roots => calls.push(roots) }) });
  const target = { textContent: 'materi' }; renderer.clear(target); renderer.clear(null);
  assert.deepEqual(calls, [[target]]);
});

test('Pelajaran tanpa rumus tetap berjalan ketika script gagal; kegagalan rumus dilaporkan', async () => {
  const script = new Script(); script.dataset.mathjaxState = 'error'; const errors = [];
  const renderer = createRenderer({ script, getMathJax: () => undefined, onError: error => errors.push(error.message) });
  assert.equal(await renderer.render({ textContent: 'Aku Anak Indonesia' }), true);
  assert.equal(errors.length, 0);
  assert.equal(await renderer.render({ textContent: '$90^{\\circ}$' }), false);
  assert.equal(errors.length, 1);
});

test('Pemuatan script yang berhenti mempunyai batas waktu dan engine yang kemudian siap dapat dipakai', async () => {
  const script = new Script(); let engine; const errors = [];
  const renderer = createRenderer({ script, getMathJax: () => engine, timeoutMs: 10, onError: e => errors.push(e.message) });
  assert.equal(await renderer.render({ textContent: '$3$' }), false);
  assert.equal(errors.length, 1); engine = { typesetPromise: async () => {} };
  assert.equal(await renderer.render({ textContent: '$4$' }), true);
});
