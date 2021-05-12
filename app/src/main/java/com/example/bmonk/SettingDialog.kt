package com.example.bmonk

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.setting_dialog.*
import java.lang.IllegalStateException

class SettingDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val alertDialog = AlertDialog.Builder(it)
            alertDialog.setView(requireActivity().layoutInflater.inflate(R.layout.setting_dialog, null))

            alertDialog.create()

        }?:throw IllegalStateException("Activity is null !!")

    }

}