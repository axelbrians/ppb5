package com.machina.ppb5.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.machina.ppb5.data.model.Contact
import com.machina.ppb5.databinding.DialogFormContactBinding

class DialogContactForm(
    private val title: String,
    private val hint1: String,
    private val hint2: String,
    private val positive: String,
    private val negative: String,
    private val isHint2Visible: Boolean,
    private val passedListener: DialogAddItemListener
) : DialogFragment() {
    internal lateinit var listener: DialogAddItemListener

    interface DialogAddItemListener {
        fun onDialogPositiveClick(dialog: DialogFragment, contact: Contact)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            val binding = DialogFormContactBinding.inflate(layoutInflater)
//            val kelompok = resources.getStringArray(R.array.kelompok)
//            val kelompokAdapter = ArrayAdapter(requireContext(), R.layout.item_list_dropdown, kelompok)
//            (binding.dialogAddItemKelompok.editText as? AutoCompleteTextView)?.setAdapter(kelompokAdapter)
            if (!isHint2Visible) {
                binding.dialogAddContactPhone.visibility = View.GONE
            }
            binding.dialogAddContactName.hint = hint1
            binding.dialogAddContactPhone.hint = hint2
            builder.setView(binding.root)
                .setTitle(title)
                .setPositiveButton(positive) { _, _ ->
                    val name = binding.dialogAddContactName.editText?.text.toString()
                    val phone = binding.dialogAddContactPhone.editText?.text.toString()
                    if (isHint2Visible) {
                        if (name.isBlank() || phone.isBlank()) {
                            listener.onDialogNegativeClick(this)
                        } else {
                            val contact = Contact(name, phone)
                            listener.onDialogPositiveClick(this, contact)
                        }
                    } else {
                        if (name.isBlank()) {
                            listener.onDialogNegativeClick(this)
                        } else {
                            val contact = Contact(name, phone)
                            listener.onDialogPositiveClick(this, contact)
                        }
                    }
                }
                .setNegativeButton(negative) { _, _ ->
                    listener.onDialogNegativeClick(this)
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = passedListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement DialogAddItemListener"))
        }
    }
}