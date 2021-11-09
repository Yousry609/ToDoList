package com.turki.todolist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.turki.todolist.R
import com.turki.todolist.model.entity.Note
import com.turki.todolist.model.entity.NoteDatabase
import kotlinx.android.synthetic.main.fragment_edit_note.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [EditNoteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
lateinit var edt_noteTitle: EditText
lateinit var edt_noteDes: EditText
lateinit var btn_update: Button
lateinit var dueDateCalender: CalendarView
lateinit var creationDate: TextView
lateinit var rbg_category: RadioGroup
lateinit var rb_work: RadioButton
lateinit var rb_personal: RadioButton
lateinit var rb_family: RadioButton
lateinit var rb_friends: RadioButton

private lateinit var noteDatabase: NoteDatabase


class EditNoteFragment : Fragment() {
    // TODO: Rename and change types of parameters


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edt_noteTitle = view.findViewById(R.id.edt_title_edit)
        edt_noteDes = view.findViewById(R.id.edt_description_edit)
        btn_update = view.findViewById(R.id.btn_updateTask)
        dueDateCalender = view.findViewById(R.id.calendarView_edit)
        creationDate = view.findViewById(R.id.tv_creationDate_edit)
        rbg_category = view.findViewById(R.id.rbgroup_category_edit)
        rb_work = view.findViewById(R.id.rb_work_edit)
        rb_family = view.findViewById(R.id.rb_family_edit)
        rb_friends = view.findViewById(R.id.rb_friends_edit)
        rb_personal = view.findViewById(R.id.rb_personal_edit)

        var note = arguments?.get("note") as Note

        noteDatabase = NoteDatabase.getInstance(requireContext())

        var date: Date = Date()

        dueDateCalender.minDate = date.time
        edt_noteTitle.setText(note.noteTitle.toString())
        edt_noteDes.setText(note.noteDescription.toString())
        creationDate.setText(note.noteCreationDate)
        dueDateCalender.date = getDateFromString(note.noteDueDate)
        setCategory(note.noteCategory)
        var dueDateString: String = note.noteDueDate

        dueDateCalender.setOnDateChangeListener { calendarView, year, month, day ->
            dueDateString = "" + (if (day >= 10) day else "0$day") + "-" + (if (month >= 9) month + 1 else "0${month + 1}") + "-" + year
        }

        btn_update.setOnClickListener {


            GlobalScope.launch(Dispatchers.IO) {
                noteDatabase.noteDAO().updateNote(
                    Note(
                        note.noteId,
                        noteTitle = edt_noteTitle.text.toString(),
                        noteCategory = getCategory(rbg_category.checkedRadioButtonId),
                        noteDescription = edt_noteDes.text.toString(),
                        noteCreationDate = note.noteCreationDate,
                        noteDueDate = dueDateString,
                        noteIsDone = note.noteIsDone
                    )
                )
                noteDatabase.noteDAO().getNotes()

            }
            findNavController().navigate(R.id.action_editNoteFragment_to_showNotesFragment)

        }

    }

    private fun setCategory(noteCategory: String) {

        when (noteCategory) {
            rb_work.text -> {
                rb_work.isChecked = true
            }
            rb_family.text -> {
                rb_family.isChecked = true
            }
            rb_personal.text -> {
                rb_personal.isChecked = true
            }
            rb_friends.text -> {
                rb_friends.isChecked = true
            }
        }

    }

    fun getCategory(categoryId: Int): String {
        when (categoryId) {
            rb_work.id -> {
                return rb_work.text.toString()
            }
            rb_personal.id -> {
                return rb_personal.text.toString()
            }
            rb_family.id -> {
                return rb_family.text.toString()
            }
            rb_friends.id -> {
                return rb_friends.text.toString()
            }
            else -> {
                return "None"
            }

        }
    }

    private fun getDateFromString(dateString: String): Long {
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val localDate: LocalDate = LocalDate.parse(dateString, formatter)
        val ldt = LocalDateTime.of(localDate, LocalTime.of(0, 0, 0))
        return ldt.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    private fun Date.dateToString(format: String): String {
        //simple date formatter
        val dateFormatter = SimpleDateFormat(format, Locale.getDefault())

        //return the formatted date string
        return dateFormatter.format(this)
    }

    fun getDate(milliSeconds: Long, dateFormat: String): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

}