package com.projectdelta.habbit.util.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.projectdelta.habbit.R

class CustomTextPreference: Preference {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context, attrs: AttributeSet,
        defStyle: Int
    ) : super(context, attrs, defStyle)

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        context?.let {
            (holder?.findViewById(android.R.id.title) as? TextView)?.setTextColor(
                ContextCompat.getColor(
                    it,
                    R.color.danger
                )
            )
        }
    }
}