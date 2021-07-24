package com.projectdelta.habbit.util.lang

import android.text.Html
import com.projectdelta.habbit.data.entities.Day

const val bullet = "&#8226"

/**
 * Returns a [delimiter] separated string with [bullet] points.
 * @param delimiter default set to "\n"
 * @param maxLength maximum character length to add to single line of string
 */
fun Day.titlesToBulletList(delimiter: String = "\n" , maxLength : Int = 30 , maxLine : Int = 320) = this.tasksTitle.take(maxLine).joinToString(delimiter) {
    "${Html.fromHtml(bullet)} ${it.chop(maxLength).capitalized()}"
}