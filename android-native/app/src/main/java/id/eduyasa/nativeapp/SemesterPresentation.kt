package id.eduyasa.nativeapp

/**
 * Hanya metadata tampilan Android; tidak memindahkan/mengubah JSON, ID soal,
 * maupun file materi.
 *
 * OFFICIAL: pemetaan yang sebelumnya terkonfirmasi dari buku/panduan resmi.
 * PROVISIONAL: berdasarkan contoh program semester, masih perlu cek LKS sekolah.
 * null: pemetaan ditahan sampai referensi LKS dipastikan.
 */
enum class SemesterEvidence { OFFICIAL, PROVISIONAL }

data class SemesterSplit(
    val lastChapterFirstSemester: Int,
    val evidence: SemesterEvidence
) {
    fun semesterFor(chapterNumber: Int): Int =
        if (chapterNumber <= lastChapterFirstSemester) 1 else 2
}

object SemesterPresentation {
    private val splits: Map<Pair<Int, String>, SemesterSplit> = mapOf(
        // Kelas 3: Seni Rupa (SBD) ditahan, menunggu kecocokan bab dengan LKS.
        (3 to "BIN") to SemesterSplit(2, SemesterEvidence.PROVISIONAL),
        (3 to "PKN") to SemesterSplit(2, SemesterEvidence.OFFICIAL),
        (3 to "MTK") to SemesterSplit(3, SemesterEvidence.PROVISIONAL),
        (3 to "IPAS") to SemesterSplit(4, SemesterEvidence.PROVISIONAL),
        (3 to "PAI") to SemesterSplit(5, SemesterEvidence.OFFICIAL),
        (3 to "ENG") to SemesterSplit(5, SemesterEvidence.PROVISIONAL),
        (3 to "PJK") to SemesterSplit(4, SemesterEvidence.PROVISIONAL),

        // Kelas 6: Bahasa Indonesia (BIN) ditahan, menunggu konfirmasi edisi LKS.
        (6 to "MTK") to SemesterSplit(2, SemesterEvidence.PROVISIONAL),
        (6 to "IPAS") to SemesterSplit(3, SemesterEvidence.PROVISIONAL),
        (6 to "PKN") to SemesterSplit(4, SemesterEvidence.PROVISIONAL),
        (6 to "ENG") to SemesterSplit(3, SemesterEvidence.PROVISIONAL),
        (6 to "PAI") to SemesterSplit(5, SemesterEvidence.OFFICIAL),
        (6 to "SBD") to SemesterSplit(3, SemesterEvidence.PROVISIONAL),
        (6 to "PJK") to SemesterSplit(3, SemesterEvidence.PROVISIONAL)
    )

    fun forSubject(grade: Int, subjectId: String, chapterCount: Int): SemesterSplit? {
        val result = splits[grade to subjectId] ?: return null
        return result.takeIf { it.lastChapterFirstSemester in 1 until chapterCount }
    }

    fun pendingReason(grade: Int, subjectId: String): String? = when (grade to subjectId) {
        (3 to "SBD") -> "Pembagian semester Seni Rupa Kelas 3 menunggu pemeriksaan bab dengan LKS."
        (6 to "BIN") -> "Pembagian semester Bahasa Indonesia Kelas 6 menunggu konfirmasi edisi LKS."
        else -> null
    }
}
