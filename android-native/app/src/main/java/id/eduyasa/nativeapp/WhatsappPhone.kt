package id.eduyasa.nativeapp

/**
 * Siswa/orang tua memasukkan 08..., sementara wa.me memerlukan 62... tanpa '+'.
 * Data disimpan dalam bentuk 62... dan tidak pernah dikirim otomatis.
 */
private val indonesiaWhatsappPattern = Regex("^8[1-9][0-9]{7,11}$")

fun normalizeIndonesiaWhatsapp(raw: String): String? {
    val digits = raw.filter(Char::isDigit)
    val local = when {
        digits.startsWith("62") -> digits.drop(2)
        digits.startsWith("0") -> digits.drop(1)
        else -> digits
    }
    return if (indonesiaWhatsappPattern.matches(local)) "62" + local else null
}

fun toLocalIndonesiaWhatsapp(raw: String): String =
    normalizeIndonesiaWhatsapp(raw)?.let { "0" + it.drop(2) }.orEmpty()
