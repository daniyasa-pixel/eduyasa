/* Bank soal tambahan: daftar paket MTK berada di mtk/question-packs.json. */
(function (root) {
  'use strict';

  function shuffle(items, random = Math.random) {
    const result = [...items];
    for (let i = result.length - 1; i > 0; i--) {
      const j = Math.floor(random() * (i + 1));
      [result[i], result[j]] = [result[j], result[i]];
    }
    return result;
  }

  function hasImage(question) {
    const src = typeof question.image === 'string' ? question.image : question.image?.src;
    return typeof src === 'string' && src.trim().length > 0;
  }

  function selectQuestions(questions, { mathSelected = false, random = Math.random } = {}) {
    if (!mathSelected) {
      if (questions.length < 20) throw new Error('Jumlah soal yang tersedia kurang dari 20.');
      return shuffle(questions, random).slice(0, 20);
    }
    const illustrated = questions.filter(hasImage);
    const plain = questions.filter(question => !hasImage(question));
    if (illustrated.length < 5 || plain.length < 15) {
      throw new Error('Latihan memerlukan minimal 5 soal bergambar dan 15 soal tanpa gambar. Periksa kelengkapan paket bab yang dipilih.');
    }
    return shuffle([
      ...shuffle(illustrated, random).slice(0, 5),
      ...shuffle(plain, random).slice(0, 15)
    ], random);
  }

  function normalizeQuestion(question, subject, chapterNumber) {
    const text = question.text || question.question || question.pertanyaan;
    const rawOptions = question.options || question.pilihan || [];
    const options = rawOptions.map(option => {
      const value = typeof option === 'string' ? option : (option?.text || '');
      return value.replace(/^[A-D]\.\s*/i, '').trim();
    });
    const answer = question.answer !== undefined ? question.answer : question.jawaban;
    let correctText;
    if (typeof answer === 'string') {
      const cleanAnswer = answer.replace(/^[A-D]\.\s*/i, '').trim();
      // A literal answer such as "A" takes precedence over legacy letter keys.
      correctText = options.includes(cleanAnswer) ? cleanAnswer
        : (/^[A-D]$/i.test(answer) ? options[answer.toUpperCase().charCodeAt(0) - 65] : cleanAnswer);
    } else if (typeof answer === 'number') {
      correctText = options[answer];
    }
    if (!text || options.length !== 4 || options.some(option => !option)
        || new Set(options).size !== 4 || !options.includes(correctText)) {
      throw new Error(`Soal ${question.id || 'tanpa ID'} tidak memiliki pertanyaan, empat opsi unik, atau kunci jawaban yang valid.`);
    }
    return {
      id: question.id || `${subject.id}-B${chapterNumber}-${text}-${options.join('|')}`,
      text, options, correctText,
      explanation: question.discussion || question.explanation || question.pembahasan || 'Tidak ada pembahasan yang tersedia untuk soal ini.',
      image: question.image || null, audio: question.audio || null,
      subjectId: subject.id,
      chapterId: `${subject.id}-B${String(chapterNumber).padStart(2, '0')}`,
      mapel: subject.name, bab: `Bab ${chapterNumber}`
    };
  }

  async function loadQuestions({ baseUrl, subjectIds, selectedChapters, subjects, fetcher = fetch }) {
    async function readJson(path) {
      const response = await fetcher(baseUrl + path, { cache: 'no-store' });
      if (!response.ok) throw new Error(`Gagal memuat ${path} (HTTP ${response.status}). Coba lagi setelah koneksi tersedia.`);
      try { return await response.json(); }
      catch (_) { throw new Error(`Data ${path} bukan JSON yang valid.`); }
    }
    const ids = [...subjectIds];
    const manifest = ids.includes('MTK') ? await readJson('mtk/question-packs.json') : null;
    if (manifest && (manifest.subject_id !== 'MTK' || !manifest.chapters)) {
      throw new Error('Daftar paket Matematika tidak valid.');
    }
    const questions = [];
    const seen = new Set();
    for (const id of ids) {
      const subject = subjects.find(item => item.id === id);
      if (!subject) throw new Error(`Mata pelajaran ${id} tidak ditemukan.`);
      const chapters = [...(selectedChapters[id] || [])].sort((a, b) => a - b);
      if (!chapters.length) throw new Error(`Pilih minimal satu bab ${subject.name}.`);
      for (const number of chapters) {
        const chapterId = `${id}-B${String(number).padStart(2, '0')}`;
        const stem = `${id.toLowerCase()}/${chapterId.toLowerCase()}`;
        const paths = id === 'MTK' ? manifest.chapters[chapterId] : [`${stem}.json`];
        if (!Array.isArray(paths) || !paths.length || new Set(paths).size !== paths.length) {
          throw new Error(`Daftar paket ${chapterId} tidak ditemukan atau berulang.`);
        }
        for (const path of paths) {
          if (typeof path !== 'string' || (id === 'MTK' && !new RegExp(`^${stem}[a-z]*\\.json$`).test(path))) {
            throw new Error(`Nama paket ${chapterId} tidak valid.`);
          }
          const data = await readJson(path);
          if (Array.isArray(data.chapters) && data.chapters.some(chapter => chapter.chapter_id !== chapterId)) {
            throw new Error(`Paket ${path} memuat bab yang tidak sesuai.`);
          }
          const raw = (data.chapters || []).flatMap(chapter => chapter.questions || []).concat(data.bank_soal || []);
          if (!raw.length) throw new Error(`Paket ${path} tidak berisi soal.`);
          for (const item of raw) {
            const question = normalizeQuestion(item, subject, number);
            if (seen.has(question.id)) throw new Error(`ID soal berulang: ${question.id}.`);
            seen.add(question.id);
            questions.push(question);
          }
        }
      }
    }
    return questions;
  }

  const api = { shuffle, hasImage, selectQuestions, normalizeQuestion, loadQuestions };
  if (typeof module !== 'undefined' && module.exports) module.exports = api;
  else root.EduyasaQuestionBank = api;
})(typeof window !== 'undefined' ? window : globalThis);
