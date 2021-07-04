package com.projectdelta.habbit.util.lang

import android.annotation.SuppressLint
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TimeUtil {
	private fun fromMilliSecondsToMinutes(milli : Long ) : Long = TimeUnit.MINUTES.convert(milli , TimeUnit.MILLISECONDS)

	@Suppress("unused")
	fun fromMinutesToMilliSeconds( min : Long ) : Long = TimeUnit.MILLISECONDS.convert(min , TimeUnit.MINUTES)

	@Suppress("unused")
	fun fromMilliSecondsToString( milli : Long ) : String{
		val timeMilli = fromMilliSecondsToMinutes(milli)
		return "${ if(timeMilli / 60 < 10) "0"+(timeMilli / 60).toString() else timeMilli / 60 }:${ if(timeMilli % 60 < 10) "0" + (timeMilli % 60).toString() else timeMilli %60 }"
	}

	@Suppress("unused")
	fun getDate() : Int{
		val c = Calendar.getInstance()
		return c.get(Calendar.DAY_OF_MONTH)
	}

	private fun getMonth(month : Int ) = DateFormatSymbols().months[ month - 1 ]

	/**
	* Returns Date-Format(dd MMMM) offset to X days
	*
	* @param offset -> days offset to (from today)
	* @return -> String formatted by "DD MMMM"
	* */
	@SuppressLint("SimpleDateFormat")
	fun getDateFromOffset(offset : Int) : String {
		val c = Calendar.getInstance()
		c.add(Calendar.DAY_OF_YEAR , offset)
		return "${SimpleDateFormat("dd").format( Date( c.timeInMillis ) )} ${getMonth(SimpleDateFormat("MM").format( Date( c.timeInMillis ) ).toInt())}"
	}

	/**
	 * Returns number of days from epoch
	 */
	fun getTodayFromEpoch( ) : Long  {
		val calender = Calendar.getInstance()
		return (
				calender.timeInMillis + calender.get( Calendar.ZONE_OFFSET ) +
				calender.get( Calendar.DST_OFFSET )
				) / (1000 * 60 * 60 *24)
	}

	@Suppress("SpellCheckingInspection")
	fun getMSfromEpoch( ) : Long {
		val calender = Calendar.getInstance()
		return calender.timeInMillis + calender.get( Calendar.ZONE_OFFSET ) + calender.get( Calendar.DST_OFFSET )
	}

	fun getMSfromMidnight():Long = getMSfromEpoch() % (24 * 60 * 60 * 1000)

}