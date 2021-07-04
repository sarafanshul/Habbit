package com.projectdelta.habbit.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat

/**
 * Converts a drawable to [Bitmap] object
 */
fun convertDrawableToBitmap(drawable: Drawable): Bitmap {
	val bitmap = Bitmap.createBitmap(
		drawable.intrinsicWidth,
		drawable.intrinsicHeight,
		Bitmap.Config.ARGB_8888
	)
	val canvas = Canvas(bitmap)
	drawable.setBounds(0, 0, canvas.width, canvas.height)
	drawable.draw(canvas)
	return bitmap
}

/**
 *
 */
fun ImageView.setTint(@ColorRes colorId: Int) {
	ImageViewCompat.setImageTintList(this, ColorStateList.valueOf( ContextCompat.getColor(context, colorId) ) )
}