/* EDUYASA Grade 3 English listening — Web Speech API, no MP3 required. */
(function (root) {
  'use strict';
  function supported() { return typeof root.speechSynthesis !== 'undefined' && typeof root.SpeechSynthesisUtterance === 'function'; }
  function englishVoices() {
    if (!supported()) return [];
    return root.speechSynthesis.getVoices().filter(v => /^en(?:-|_)/i.test(v.lang));
  }
  function speak(input, handlers = {}) {
    if (!supported()) { handlers.onError?.('Perangkat ini belum mendukung pembaca suara.'); return false; }
    const text = typeof input === 'string' ? input : input?.text;
    if (typeof text !== 'string' || !text.trim()) { handlers.onError?.('Teks listening tidak tersedia.'); return false; }
    const voices = englishVoices();
    if (!voices.length) { handlers.onError?.('Suara bahasa Inggris belum tersedia di perangkat. Periksa pengaturan Text-to-Speech atau coba browser lain.'); return false; }
    root.speechSynthesis.cancel();
    const utterance = new root.SpeechSynthesisUtterance(text);
    utterance.lang = input?.lang || 'en-US';
    utterance.rate = Number.isFinite(input?.rate) ? Math.max(0.5, Math.min(1.2, input.rate)) : 0.85;
    utterance.voice = voices.find(v => v.lang.toLowerCase() === utterance.lang.toLowerCase()) || voices[0];
    utterance.onstart = () => handlers.onStart?.();
    utterance.onend = () => handlers.onEnd?.();
    utterance.onerror = e => handlers.onError?.('Gagal memutar suara: ' + (e.error || 'unknown'));
    root.speechSynthesis.speak(utterance);
    return true;
  }
  function stop() { if (supported()) root.speechSynthesis.cancel(); }
  const api = { supported, englishVoices, speak, stop };
  if (typeof module !== 'undefined' && module.exports) module.exports = api;
  else root.EduyasaEnglishSpeech = api;
})(typeof window !== 'undefined' ? window : globalThis);
