package com.projectdelta.habbit.util.lang

import android.text.Html
import com.projectdelta.habbit.data.model.entities.Day
import com.projectdelta.habbit.util.constant.INDENT

const val bullet = "&#8226"

/**
 * Returns a [delimiter] separated string with [bullet] points.
 * @param delimiter default set to "\n"
 * @param maxLength maximum character length to add to single line of string
 */
fun Day.titlesToBulletList(delimiter: String = "\n" , maxLength : Int = 20 , maxLine : Int = 320) = tasksTitle.take(maxLine).joinToString(delimiter) {
    "$INDENT${Html.fromHtml(bullet)} ${it.chop(maxLength).capitalized()}"
} + if( tasksTitle.size > maxLine ) "\n${INDENT}and ${tasksTitle.size - maxLine} more!" else ""