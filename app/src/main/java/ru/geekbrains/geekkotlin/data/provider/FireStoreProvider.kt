package ru.geekbrains.geekkotlin.data.provider

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.data.entity.User
import ru.geekbrains.geekkotlin.data.errors.NoAuthException
import ru.geekbrains.geekkotlin.model.NoteResult

class FireStoreProvider(private val firebaseAuth: FirebaseAuth, private val store: FirebaseFirestore) : RemoteDataProvider {

    companion object {
        private const val NOTES_COLLECTION = "notes"
        private const val USERS_COLLECTION = "users"
    }

    private val currentUser
        get() = firebaseAuth.currentUser

    override fun getCurrentUser(): LiveData<User?> = MutableLiveData<User?>().apply {
        value = currentUser?.let { User(it.displayName ?: "", it.email ?: "") }
    }

    private fun getUserNotesCollection() = currentUser?.let {
        store.collection(USERS_COLLECTION).document(it.uid).collection(NOTES_COLLECTION)
    } ?: throw NoAuthException()

    override fun getNoteById(id: String) = MutableLiveData<NoteResult>().apply {
        try {
            getUserNotesCollection().document(id).get()
                    .addOnSuccessListener {
                        val note = it.toObject(Note::class.java)
                        Timber.d { "Note was loaded: $note" }
                        value = NoteResult.Success(note)
                    }.addOnFailureListener {
                        Timber.e(it) { "Error reading note with id $id" }
                        throw it
                    }
        } catch (e: Throwable) {
            value = NoteResult.Error(e)
        }
    }

    override fun saveNote(note: Note) = MutableLiveData<NoteResult>().apply {
        try {
            getUserNotesCollection().document(note.id)
                    .set(note).addOnSuccessListener {
                        Timber.d { "Note was saved: $note" }
                        value = NoteResult.Success(note)
                    }.addOnFailureListener {
                        Timber.e(it) { "Error saving note $note" }
                        throw it
                    }
        } catch (e: Throwable) {
            value = NoteResult.Error(e)
        }
    }

    override fun subscribeToAllNotes() = MutableLiveData<NoteResult>().apply {
        try {
            getUserNotesCollection().addSnapshotListener { snapshot, e ->
                value = e?.let { NoteResult.Error(e) }
                        ?: snapshot?.let {
                            val notes = it.documents.map { it.toObject(Note::class.java) }
                            NoteResult.Success(notes)
                        }
            }
        } catch (e: Throwable) {
            value = NoteResult.Error(e)
        }
    }

    override fun deleteNote(noteId: String): LiveData<NoteResult> = MutableLiveData<NoteResult>().apply {
        getUserNotesCollection().document(noteId).delete()
                .addOnSuccessListener {
                    value = NoteResult.Success(null)
                }
                .addOnCompleteListener {
                    value = NoteResult.Success(null)
                }
                .addOnFailureListener {
                    value = NoteResult.Error(it)
                }
    }


}