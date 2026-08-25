package com.studymate.sm.cid.ui.common

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner

object SpinnerUtil {
    fun <T> isi(context: Context, spinner: Spinner, items: List<T>, labelOf: (T) -> String) {
        val labels = items.map { labelOf(it) }
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, labels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}
