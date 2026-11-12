/* Lokasi data ditentukan oleh kelas; bank dan ID Kelas 6 tetap di akar. */
(function (root) {
  'use strict';
  const grades = {
    6: { grade: 6, dataPath: '', chapterPrefix: '', manifestSubjects: ['MTK', 'SBD', 'PJK'], imageQuotaSubjects: ['MTK', 'SBD', 'PJK'] },
    3: {
  "grade": 3,
  "dataPath": "kelas-3/",
  "chapterPrefix": "K03-",
  "manifestSubjects": [
    "BIN",
    "PKN",
    "MTK",
    "SBD",
    "IPAS",
    "PAI",
    "ENG",
    "PJK"
  ],
  "imageQuotaSubjects": [
    "BIN",
    "PKN",
    "MTK",
    "SBD",
    "IPAS",
    "ENG",
    "PJK"
  ],
  "subjects": [
    {
      "id": "BIN",
      "name": "Bahasa Indonesia",
      "icon": "📚",
      "chapters": [
        "Kawan Seiring",
        "Yuk, Berpendapat!",
        "Pribadi yang Hebat",
        "Menyelamatkan Lingkungan"
      ]
    },
    {
      "id": "PKN",
      "name": "Pendidikan Pancasila",
      "icon": "🇮🇩",
      "chapters": [
        "Aku Anak Indonesia",
        "Aku Patuh Aturan",
        "Berbeda Itu Indah",
        "Ayo Mengenal Pancasila"
      ]
    },
    {
      "id": "MTK",
      "name": "Matematika",
      "icon": "🔢",
      "chapters": [
        "Bilangan Cacah sampai 1.000",
        "Kalimat Matematika",
        "Pengukuran Panjang dan Berat",
        "Unsur-Unsur Bangun Datar",
        "Penyajian Data dalam Tabel"
      ]
    },
    {
      "id": "SBD",
      "name": "Seni Rupa",
      "icon": "🎨",
      "chapters": [
        "Menemukan Keindahan di Sekitar Kita",
        "Belajar dari Karya Orang Lain",
        "Ideku, Gagasanku",
        "Karya yang Mewakili Diriku",
        "Aku Seniman Cilik"
      ]
    },
    {
      "id": "IPAS",
      "name": "IPAS",
      "icon": "🌏",
      "chapters": [
        "Keajaiban Tubuhku",
        "Dahulu, Kini, dan Nanti",
        "Peduli dan Berbagi",
        "Siklus Hidup yang Menakjubkan",
        "Bijak Berbelanja Kebutuhan",
        "Energi, sang Pemberi Kekuatan",
        "Jejak Penjelajah",
        "Rahasia Tiga Wujud Zat"
      ]
    },
    {
      "id": "PAI",
      "name": "Pendidikan Agama Islam",
      "icon": "📖",
      "chapters": [
        "Asyiknya Belajar Surah Al-'Alaq",
        "Ayo Mengenal Tuhan Kita",
        "Perilaku Terpuji Adalah Kepribadianku",
        "Aku Bangga Mampu Berpuasa",
        "Aku Suka Belajar Sejarah Islam",
        "Senangnya Belajar Hadis",
        "Ayo Beriman Kepada Kitab-Kitab Allah Swt.",
        "Aku Senang Berkalimah Tayyibah",
        "Senangnya Salat Rawatib",
        "Nabi Muhammad saw. Rasulku"
      ]
    },
    {
      "id": "ENG",
      "name": "Bahasa Inggris",
      "icon": "🌍",
      "chapters": [
        "I Like Mi Aceh",
        "I Don't Like Rice",
        "I Have Fried Chicken for Breakfast",
        "Do You Like Swimming?",
        "I Like Riding a Bike on Sunday",
        "Is It the Canteen?",
        "My Class Is Behind the Office",
        "I Drink Orange Juice in the Canteen",
        "My Classroom Is Clean",
        "There Are Twenty Books on the Shelf"
      ]
    },
    {
      "id": "PJK",
      "name": "PJOK",
      "icon": "⚽",
      "chapters": [
        "Aktivitas Pola Gerak Dasar Lokomotor",
        "Aktivitas Pola Gerak Dasar Nonlokomotor",
        "Aktivitas Pola Gerak Dasar Manipulatif",
        "Aktivitas Gerak Dominan Senam",
        "Aktivitas Gerak Berirama",
        "Aktivitas Permainan dan Olahraga di Air",
        "Aktivitas Kebugaran untuk Kesehatan",
        "Aktivitas Fisik dan Memilih Makanan Bergizi"
      ],
      "materialsAvailable": false
    }
  ]
}

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
