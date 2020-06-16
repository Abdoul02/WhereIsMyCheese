package whereismytransport.whereismycheese

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

/**
 * We need some way to add a Cheezy Note. We will also require some way to show the user the cheezy note..
 * Feel free to just use AlertDialog if time is an issue.
 */
class CheesyDialog(var context: Activity, var listener: INoteDialogListener) : Dialog(context), View.OnClickListener {
    private lateinit var saveButton: Button
    private lateinit var exitButton: Button
    private lateinit var noteEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_note)
        noteEditText = findViewById<View>(R.id.noteText) as EditText
        saveButton = findViewById<View>(R.id.saveCheeseButton) as Button
        exitButton = findViewById<View>(R.id.exitDialogButton) as Button
        saveButton.setOnClickListener(this)
        exitButton.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.saveCheeseButton -> {
                listener.onNoteAdded(noteEditText.text.toString())
                dismiss()
            }
            R.id.exitDialogButton -> dismiss()
            else -> {
            }
        }
    }

    interface INoteDialogListener {
        fun onNoteAdded(note: String)
    }

}