package id.eduyasa.nativeapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Form Native untuk tambah/ubah/hapus profil siswa.
 * Menyimpan lewat callback ke penyimpanan lokal HP, bukan ke GitHub.
 */
@Composable
fun StudentProfileEditor(
    existing: StudentProfile?,
    onDismiss: () -> Unit,
    onSave: (StudentProfile) -> Unit,
    onDelete: (StudentProfile) -> Unit
) {
    var name by remember(existing?.id) { mutableStateOf(existing?.name.orEmpty()) }
    var grade by remember(existing?.id) { mutableIntStateOf(existing?.grade ?: 3) }
    var askDelete by remember(existing?.id) { mutableStateOf(false) }
    val valid = name.trim().length in 2..60 && grade in setOf(3, 6)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Tambah Profil Siswa" else "Ubah Profil Siswa") },
        text = {
            Column {
                Text("Nama siswa", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(7.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(60) },
                    placeholder = { Text("Contoh: Icai") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("student-profile-name")
                )
                Spacer(Modifier.height(16.dp))
                Text("Kelas", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(3, 6).forEach { number ->
                        FilterChip(
                            selected = grade == number,
                            onClick = { grade = number },
                            label = { Text("Kelas " + number) },
                            modifier = Modifier.testTag("profile-grade-" + number)
                        )
                    }
                }
                Text(
                    "Saat ini EDUYASA menyediakan materi untuk Kelas 3 dan Kelas 6.",
                    style = MaterialTheme.typography.bodySmall
                )
                if (existing != null) {
                    Spacer(Modifier.height(14.dp))
                    TextButton(
                        onClick = { askDelete = true },
                        modifier = Modifier.testTag("delete-profile")
                    ) { Text("HAPUS PROFIL", color = MaterialTheme.colorScheme.error) }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = valid,
                onClick = {
                    val value = existing?.copy(name = name.trim(), grade = grade)
                        ?: StudentProfileStore.newProfile(name, grade)
                    onSave(value)
                },
                modifier = Modifier.testTag("save-profile")
            ) { Text("SIMPAN") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.testTag("cancel-profile")) {
                Text("BATAL")
            }
        }
    )

    if (askDelete && existing != null) {
        AlertDialog(
            onDismissRequest = { askDelete = false },
            title = { Text("Hapus profil " + existing.name + "?") },
            text = { Text("Nama dan kelas akan dihapus dari HP ini. Bank soal EDUYASA tidak terpengaruh.") },
            confirmButton = {
                TextButton(
                    onClick = { askDelete = false; onDelete(existing) },
                    modifier = Modifier.testTag("confirm-delete-profile")
                ) { Text("YA, HAPUS") }
            },
            dismissButton = {
                TextButton(onClick = { askDelete = false }) { Text("BATAL") }
            }
        )
    }
}
