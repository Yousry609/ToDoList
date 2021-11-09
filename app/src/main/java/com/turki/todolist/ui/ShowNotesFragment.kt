package com.turki.todolist.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.turki.todolist.R
import com.turki.todolist.model.entity.Note
import com.turki.todolist.model.entity.NoteDatabase
import com.turki.todolist.ui.adapter.NoteAdapter
import com.turki.todolist.ui.adapter.OnListItemClick
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [ShowNotesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ShowNotesFragment : Fragment(), OnListItemClick {
    // TODO: Rename and change types of parameters


    private var notes: ArrayList<Note> = ArrayList<Note>()

    private lateinit var notesRecyclerView: RecyclerView

    private lateinit var addButton: FloatingActionButton

    private var noteAdapter: NoteAdapter = NoteAdapter()

    private lateinit var noteDatabase: NoteDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notesRecyclerView = view.findViewById(R.id.rv_notes)
        addButton = view.findViewById(R.id.fbtn_addNote)

        noteDatabase = NoteDatabase.getInstance(requireContext())

        var date: Date = Date()

        //call the extension function on a date object
        val dateString = date.dateToString("dd-MM-yyyy")

        getAllNotes()

        notesRecyclerView.adapter = noteAdapter

        noteAdapter.onListItemClick = this

        addButton.setOnClickListener {

            Snackbar.make(view, "Add Screen", Snackbar.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_showNotesFragment_to_addNoteFragment)
        }

    }

    private fun getAllNotes(){
        GlobalScope.launch {

            var returnedNotes = async(Dispatchers.IO) { noteDatabase.noteDAO().getNotes() }

            withContext(Dispatchers.Main) {
                addButton.setBackgroundColor(Color.YELLOW)
                notes = returnedNotes.await() as ArrayList<Note>
                noteAdapter.setNotesList(notes)

            }
        }

    }

    private fun Date.dateToString(format: String): String {
        //simple date formatter
        val dateFormatter = SimpleDateFormat(format, Locale.getDefault())

        //return the formatted date string
        return dateFormatter.format(this)
    }

    override fun onItemClick(note: Note) {
        Toast.makeText(requireContext(), note.noteTitle + " Clicked", Toast.LENGTH_LONG).show()

        var action = ShowNotesFragmentDirections.actionShowNotesFragmentToEditNoteFragment(note)
        findNavController().navigate(action)

    }

    override fun onItemDelete(note: Note) {
        GlobalScope.launch(Dispatchers.IO) {
            noteDatabase.noteDAO().deleteNote(note)
        }
        Toast.makeText(
            requireContext(),
            note.noteTitle + " is deleted Successfully",
            Toast.LENGTH_SHORT
        ).show()

        getAllNotes()
    }

}