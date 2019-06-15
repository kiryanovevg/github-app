package com.kiryanov.githubapp.ui.user_list

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.kiryanov.githubapp.R
import com.kiryanov.githubapp.data.LocalData
import kotlinx.android.synthetic.main.dialog_settings.view.*
import org.koin.android.ext.android.inject

class SettingsDialogFragment : DialogFragment() {

    interface ClickListener {
        fun onPositiveButtonClick()
    }

    companion object {
        fun create(listener: ClickListener? = null) = SettingsDialogFragment().apply {
            onPositiveButtonClickListener = listener
        }
    }

    private val localData: LocalData by inject()

    private var onPositiveButtonClickListener: ClickListener? = null

    override fun setupDialog(dialog: Dialog, style: Int) {
        isCancelable = false
        dialog.setOnShowListener {
            val alertDialog = it as AlertDialog
            alertDialog.findViewById<ConstraintLayout>(R.id.root)?.also { view ->
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val fromIdStr = view.et_from.text.toString()
                    val toIdStr = view.et_to.text.toString()
                    val initialSizeStr = view.et_initial_size.text.toString()
                    val pageSizeStr = view.et_page_size.text.toString()

                    if (fromIdStr.isEmpty() || toIdStr.isEmpty()
                        || initialSizeStr.isEmpty() || pageSizeStr.isEmpty()) {
                        showErrorMessage("Fields mustn't be empty")
                        return@setOnClickListener
                    }

                    val fromId = fromIdStr.toLong()
                    val toId = toIdStr.toLong()
                    val initialSize = initialSizeStr.toInt()
                    val pageSize = pageSizeStr.toInt()

                    if (fromId < 0) {
                        showErrorMessage("From id must be greater than 0 or equal")
                        return@setOnClickListener
                    }

                    if (toId <= fromId) {
                        showErrorMessage("To id must be greater than from id")
                        return@setOnClickListener
                    }

                    if (initialSize <= 0) {
                        showErrorMessage("Page size must be a positive number")
                        return@setOnClickListener
                    }

                    if (pageSize <= 0) {
                        showErrorMessage("Page size must be a positive number")
                        return@setOnClickListener
                    }

                    localData.fromUserId = fromId
                    localData.toUserId = toId
                    localData.initialSize = initialSize
                    localData.pageSize = pageSize

                    onPositiveButtonClickListener?.onPositiveButtonClick()
                    dialog.dismiss()
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = context?.let {
        val view = LayoutInflater.from(it).inflate(R.layout.dialog_settings, null)

        view.et_from.setText(localData.fromUserId.toString(), TextView.BufferType.EDITABLE)
        view.et_to.setText(localData.toUserId.toString(), TextView.BufferType.EDITABLE)
        view.et_initial_size.setText(localData.initialSize.toString(), TextView.BufferType.EDITABLE)
        view.et_page_size.setText(localData.pageSize.toString(), TextView.BufferType.EDITABLE)

        AlertDialog.Builder(it)
            .setTitle(R.string.settings)
            .setView(view)
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .create()
    } ?: throw RuntimeException("Context error")

    private fun showErrorMessage(msg: String) {
        context?.also { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
    }
}