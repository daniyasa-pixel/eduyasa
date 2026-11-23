package id.eduyasa.nativeapp

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Profil siswa disimpan di perangkat ini saja. Tidak ada perubahan pada JSON EDUYASA,
 * tidak ada akun atau data profil yang dikirim ke GitHub.
 */
data class StudentProfile(
    val id: String,
    val name: String,
    val grade: Int,
    val reportPhone: String = ""
)

class StudentProfileStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "eduyasa_student_profiles_v1", Context.MODE_PRIVATE
    )

    fun load(): List<StudentProfile> {
        val raw = preferences.getString("students", "[]") ?: "[]"
        return runCatching {
            val array = JSONArray(raw)
            val used = mutableSetOf<String>()
            buildList {
                for (index in 0 until array.length()) {
                    val obj = array.optJSONObject(index) ?: continue
                    val id = obj.optString("id").trim()
                    val name = obj.optString("name").trim()
                    val grade = obj.optInt("grade", 0)
                    // Profil lama tanpa report_phone tetap bisa dibaca tanpa menghapus riwayat.
                    val rawPhone = obj.optString("report_phone", "")
                    val reportPhone = normalizeIndonesiaWhatsapp(rawPhone).orEmpty()
                    if (id.isNotBlank() && used.add(id) && name.isNotBlank() && grade in setOf(3, 6)) {
                        add(StudentProfile(id, name, grade, reportPhone))
                    }
                }
            }
        }.getOrDefault(emptyList())
    }

    fun save(profiles: List<StudentProfile>) {
        require(profiles.all { it.name.trim().length >= 2 && it.grade in setOf(3, 6) })
        require(profiles.map { it.id }.distinct().size == profiles.size)
        require(profiles.all {
            it.reportPhone.isBlank() || normalizeIndonesiaWhatsapp(it.reportPhone) != null
        })
        val data = JSONArray()
        profiles.forEach { profile ->
            data.put(JSONObject().apply {
                put("id", profile.id)
                put("name", profile.name.trim())
                put("grade", profile.grade)
                put("report_phone", normalizeIndonesiaWhatsapp(profile.reportPhone).orEmpty())
            })
        }
        preferences.edit().putString("students", data.toString()).apply()
    }

    fun activeProfileId(): String? = preferences.getString("active_profile_id", null)

    fun setActiveProfileId(profileId: String?) {
        preferences.edit().apply {
            if (profileId == null) remove("active_profile_id")
            else putString("active_profile_id", profileId)
        }.apply()
    }

    companion object {
        fun newProfile(name: String, grade: Int, reportPhone: String) =
            StudentProfile(
                UUID.randomUUID().toString(), name.trim(), grade,
                requireNotNull(normalizeIndonesiaWhatsapp(reportPhone))
            )
    }
}
