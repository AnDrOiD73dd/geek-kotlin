package ru.geekbrains.geekkotlin.data.provider

import com.github.ajalt.timberkt.Timber
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.Channel
import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.data.entity.User
import ru.geekbrains.geekkotlin.data.errors.NoAuthException
import ru.geekbrains.geekkotlin.model.NoteResult
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FireStoreProvider(private val firebaseAuth: FirebaseAuth, private val store: FirebaseFirestore) : RemoteDataProvider {

    companion object {
        private const val NOTES_COLLECTION = "notes"
        private const val USERS_COLLECTION = "users"
    }

    private val currentUser
        get() = firebaseAuth.currentUser

    override suspend fun getCurrentUser(): User? = suspendCoroutine { continuation ->
        continuation.resume(currentUser?.let { User(it.displayName ?: "", it.email ?: "") })
    }

    private fun getUserNotesCollection() = currentUser?.let {
        store.collection(USERS_COLLECTION).document(it.uid).collection(NOTES_COLLECTION)
    } ?: throw NoAuthException()

    override suspend fun getNoteById(id: String): Note =
            suspendCoroutine { continuation ->
                try {
                    getUserNotesCollection().document(id).get()
                            .addOnSuccessListener {
                                val note = it.toObject(Note::class.java)
                                Timber.d { "Note was loaded: $note" }
                                continuation.resume(note!!)
                            }.addOnFailureListener {
                                Timber.e(it) { "Error reading note with id $id" }
                                continuation.resumeWithException(it)
                            }
                } catch (e: Throwable) {
                    continuation.resumeWithException(e)
                }
            }

    override suspend fun saveNote(note: Note): Note = suspendCoroutine { continuation ->
        try {
            getUserNotesCollection().document(note.id)
                    .set(note).addOnSuccessListener {
                        Timber.d { "Note was saved: $note" }
                        continuation.resume(note)
                    }.addOnFailureListener {
                        Timber.e(it) { "Error saving note $note" }
                        continuation.resumeWithException(it)
                    }
        } catch (e: Throwable) {
            continuation.resumeWithException(e)
        }
    }

    override fun subscribeToAllNotes() = Channel<NoteResult>(Channel.CONFLATED).apply {
        var registration: ListenerRegistration? = null
        try {
            registration = getUserNotesCollection().addSnapshotListener { snapshot, e ->
                val value = e?.let { NoteResult.Error(e) }
                        ?: snapshot?.let {
                            val notes = it.documents.map { it.toObject(Note::class.java) }
                            NoteResult.Success(notes)
                        }
                value?.let { offer(it) }
            }
        } catch (e: Throwable) {
            offer(NoteResult.Error(e))
        }

        invokeOnClose { registration?.remove() }
    }

    override suspend fun deleteNote(noteId: String): Unit = suspendCoroutine { continuation ->
        getUserNotesCollection().document(noteId).delete()
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
    }


}