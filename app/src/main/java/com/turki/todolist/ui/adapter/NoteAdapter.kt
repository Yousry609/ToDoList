package com.turki.todolist.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.turki.todolist.R
import com.turki.todolist.model.entity.Note
import com.turki.todolist.model.entity.NoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.emptyList

class NoteAdapter() : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var notes: List<Note> = emptyList()
    var onListItemClick: OnListItemClick? = null

    private lateinit var noteDatabase: NoteDatabase

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var noteTitle: TextView = itemView.findViewById(R.id.tv_title)
        var noteDueDate: TextView = itemView.findViewById(R.id.tv_dueDate)
        var noteCategory: TextView = itemView.findViewById(R.id.tv_category)
        var noteCheckBox: CheckBox = itemView.findViewById(R.id.cb_isDone)
        var noteStatus: View = itemView.findViewById(R.id.view_status)
        var noteStatus2: View = itemView.findViewById(R.id.view_status2)
        var btn_close: ImageView = itemView.findViewById(R.id.btn_close)


        fun bind(currentNote: Note) {
            noteDatabase = NoteDatabase.getInstance(itemView.context)

            if (currentNote != null) {
                noteTitle.setText(currentNote.noteTitle.toString())
                noteCategory.setText(currentNote.noteCategory)
                noteCheckBox.isChecked = currentNote.noteIsDone
                noteDueDate.setText(currentNote.noteDueDate)

                var currentDateList: List<String> = Date().dateToString("dd-MM-yyyy").split('-')
                var dueDateList: List<String> = currentNote.noteDueDate.split('-')

                if (currentNote.noteIsDone) {
                    noteStatus.setBackgroundColor(Color.GREEN)
                    noteStatus2.setBackgroundColor(Color.GREEN)
                } else if ((dueDateList[2].toInt() < currentDateList[2].toInt())
                    || (dueDateList[1].toInt() < currentDateList[1].toInt() && dueDateList[2].toInt() == currentDateList[2].toInt())
                    || (dueDateList[0].toInt() < currentDateList[0].toInt() && dueDateList[1].toInt() == currentDateList[1].toInt() && dueDateList[2].toInt() == currentDateList[2].toInt())
                ) {
                    noteStatus.setBackgroundColor(Color.RED)
                    noteStatus2.setBackgroundColor(Color.RED)
                } else {
                    noteStatus.setBackgroundColor(Color.rgb(255, 152, 0))
                    noteStatus2.setBackgroundColor(Color.rgb(255, 152, 0))
                }

                itemView.setOnClickListener {
                    onListItemClick?.onItemClick(currentNote)
                }

                btn_close.setOnClickListener {
                    onListItemClick?.onItemDelete(currentNote)
                }

                noteCheckBox.setOnClickListener {
                    if (noteCheckBox.isChecked) {
                        currentNote.noteIsDone = true
                        noteStatus.setBackgroundColor(Color.GREEN)
                        noteStatus2.setBackgroundColor(Color.GREEN)
                        GlobalScope.launch(Dispatchers.IO) {
                            noteDatabase.noteDAO().updateNote(
                                Note(
                                    currentNote.noteId,
                                    currentNote.noteTitle,
                                    currentNote.noteCategory,
                                    currentNote.noteDescription,
                                    currentNote.noteCreationDate,
                                    currentNote.noteDueDate,
                                    true
                                )
                            )
                        }
                    } else {
                        currentNote.noteIsDone = false
                        noteStatus.setBackgroundColor(Color.rgb(255, 152, 0))
                        noteStatus2.setBackgroundColor(Color.rgb(255, 152, 0))
                        GlobalScope.launch(Dispatchers.IO) {
                            noteDatabase.noteDAO().updateNote(
                                Note(
                                    currentNote.noteId,
                                    currentNote.noteTitle,
                                    currentNote.noteCategory,
                                    currentNote.noteDescription,
                                    currentNote.noteCreationDate,
                                    currentNote.noteDueDate,
                                    false
                                )
                            )
                        }
                    }
                    notifyDataSetChanged()
                }
            }
        }


        private fun Date.dateToString(format: String): String {
            //simple date formatter
            val dateFormatter = SimpleDateFormat(format, Locale.getDefault())

            //return the formatted date string
            return dateFormatter.format(this)
        }

    }

    fun setNotesList(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.NoteViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.note_list_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteAdapter.NoteViewHolder, position: Int) {

        var currentNote: Note = notes.get(position)

        holder.bind(currentNote)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

}