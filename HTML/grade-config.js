/* Lokasi data ditentukan oleh kelas; bank dan ID Kelas 6 tetap di akar. */
(function (root) {
  'use strict';
  const grades = {
    6: { grade: 6, dataPath: '', chapterPrefix: '', manifestSubjects: ['MTK', 'SBD', 'PJK'], imageQuotaSubjects: ['MTK', 'SBD', 'PJK'] },
    3: { grade: 3, dataPath: 'kelas-3/', chapterPrefix: 'K03-', manifestSubjects: ['BIN', 'PKN'], imageQuotaSubjects: ['BIN', 'PKN'], subjects: [
      { id: 'BIN', name: 'Bahasa Indonesia', icon: '📚', chapters: ['Kawan Seiring', 'Yuk, Berpendapat!', 'Pribadi yang Hebat', 'Menyelamatkan Lingkungan'] },
      { id: 'PKN', name: 'Pendidikan Pancasila', icon: '🇮🇩', chapters: ['Aku Anak Indonesia', 'Aku Patuh Aturan', 'Berbeda Itu Indah', 'Ayo Mengenal Pancasila'] }
    ] }
  };
  function resolve(search = '') {
    const value = new URLSearchParams(search).get('kelas');
    const grade = value === null ? 6 : Number(value);
    if (!Object.prototype.hasOwnProperty.call(grades, grade)) throw new Error('Kelas belum tersedia. Silakan pilih kelas di halaman utama.');
    return grades[grade];
  }
  const api = { grades, resolve };
  if (typeof module !== 'undefined' && module.exports) module.exports = api;
  else root.EduyasaGrades = api;
})(typeof window !== 'undefined' ? window : globalThis);
