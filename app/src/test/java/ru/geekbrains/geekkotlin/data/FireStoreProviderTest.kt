package ru.geekbrains.geekkotlin.data

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.geekbrains.geekkotlin.data.entity.Note
import ru.geekbrains.geekkotlin.data.errors.NoAuthException
import ru.geekbrains.geekkotlin.data.provider.FireStoreProvider
import ru.geekbrains.geekkotlin.model.NoteResult

class FireStoreProviderTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val mockDb = mockk<FirebaseFirestore>()
    private val mockAuth = mockk<FirebaseAuth>()
    private val mockCollection = mockk<CollectionReference>()
    private val mockUser = mockk<FirebaseUser>()
    private val mockDocument1 = mockk<DocumentSnapshot>()
    private val mockDocument2 = mockk<DocumentSnapshot>()
    private val mockDocument3 = mockk<DocumentSnapshot>()
    private val testNotes = listOf(Note(id = "1"), Note(id = "2"), Note(id = "3"))
    private val provider: FireStoreProvider = FireStoreProvider(mockAuth, mockDb)

    @Before
    fun setUp() {
        clearMocks(mockCollection, mockDocument1, mockDocument2, mockDocument3)
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns ""
        every { mockDb.collection(any()).document(any()).collection(any()) } returns mockCollection
        every { mockDocument1.toObject(Note::class.java) } returns testNotes[0]
        every { mockDocument2.toObject(Note::class.java) } returns testNotes[1]
        every { mockDocument3.toObject(Note::class.java) } returns testNotes[2]
    }

    @Test
    fun `should throw if no auth`() {
        var result: Any? = null
        every { mockAuth.currentUser } returns null
        provider.subscribeToAllNotes().observeForever {
            result = (it as? NoteResult.Error)?.error
        }
        assertTrue(result is NoAuthException)
    }
}