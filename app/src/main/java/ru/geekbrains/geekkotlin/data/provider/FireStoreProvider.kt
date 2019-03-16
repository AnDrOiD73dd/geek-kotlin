package ru.geekbrains.geekkotlin.data.provider

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.model.NoteResult

class FireStoreProvider : RemoteDataProvider {

    companion object {
        private const val NOTES_COLLECTION = "notes"
    }

    private val store by lazy { FirebaseFirestore.getInstance() }

    private val notesReference by lazy { store.collection(NOTES_COLLECTION) }

    override fun getNoteById(id: String): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()
        notesReference.document(id).get()
                .addOnSuccessListener { documentSnapshot ->
                    val note = documentSnapshot.toObject(Note::class.java)
                    Timber.d { "Note was loaded: $note" }
                    result.value = NoteResult.Success(note)
                }
                .addOnFailureListener {
                    Timber.e(it) { "Error reading note with id $id" }
                    result.value = NoteResult.Error(it)
                }
        return result
    }

    override fun saveNote(note: Note): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()
        notesReference.document(note.id).set(note)
                .addOnSuccessListener {
                    Timber.d { "Note was saved: $note" }
                    result.value = NoteResult.Success(note)
                }
                .addOnFailureListener {
                    Timber.e(it) { "Error saving note $note" }
                    result.value = NoteResult.Error(it)
                }
        return result
    }

    override fun subscribeToAllNotes(): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()
        notesReference.addSnapshotListener {snapshot, exception ->
            exception?.let {
                result.value = NoteResult.Error(exception)
            } ?: let {
                snapshot?.let {
                    val notes = mutableListOf<Note>()
                    for (doc : QueryDocumentSnapshot in snapshot) {
                        val note = doc.toObject(Note::class.java)
                        notes.add(note)
                    }
                    result.value = NoteResult.Success(notes)
                }
            }
        }
        return result
    }

}