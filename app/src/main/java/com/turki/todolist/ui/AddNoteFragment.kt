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
import kotlinx.android.synthetic.main.fragment_add_note.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/**
 * A simple [Fragment] subclass.
 * Use the [AddNoteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddNoteFragment : Fragment() {
    // TODO: Rename and change types of parameters

    lateinit var edt_noteTitle: EditText
    lateinit var edt_noteDes: EditText
    lateinit var btn_add: Button
    lateinit var dueDateCalender: CalendarView
    lateinit var rbg_category: RadioGroup
    lateinit var rb_work: RadioButton
    lateinit var rb_personal: RadioButton
    lateinit var rb_family: RadioButton
    lateinit var rb_friends: RadioButton

    private lateinit var noteDatabase: NoteDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edt_noteTitle = view.findViewById(R.id.edt_title)
        edt_noteDes = view.findViewById(R.id.edt_description)
        btn_add = view.findViewById(R.id.btn_addTask)
        dueDateCalender = view.findViewById(R.id.calendarView)
        rbg_category = view.findViewById(R.id.rbgroup_category)
        rb_work = view.findViewById(R.id.rb_work)
        rb_family = view.findViewById(R.id.rb_family)
        rb_friends = view.findViewById(R.id.rb_friends)
        rb_personal = view.findViewById(R.id.rb_personal)

        noteDatabase = NoteDatabase.getInstance(requireContext())

        var date: Date = Date()

        dueDateCalender.minDate = date.time

        //call the extension function on a date object
        val dateString = date.dateToString("dd-MM-yyyy")

        var dueDateString: String = dateString

        calendarView.setOnDateChangeListener { _, year, month, day ->
//            dueDateString = "$day-${month+1}-$year" //getDate(calendarView.date, "dd-MM-yyyy")
            dueDateString = "" + (if (day >= 10) day else "0$day") + "-" + (if (month >= 9) month + 1 else "0${month + 1}") + "-" + year

        }


        btn_add.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO) {

                noteDatabase.noteDAO().addNote(
                    Note(
                        0,
                        edt_noteTitle.text.toString(),
                        getCategory(rbg_category.checkedRadioButtonId),
                        edt_noteDes.text.toString(),
                        dateString,
                        dueDateString,
                        false
                    )
                )
                noteDatabase.noteDAO().getNotes()

            }


            findNavController().navigate(R.id.action_addNoteFragment_to_showNotesFragment)

        }


    }

    private fun getCategory(categoryId: Int): String {
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