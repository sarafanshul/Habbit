package com.projectdelta.habbit.util.lang

// https://stackoverflow.com/questions/44580702/android-room-persistent-library-how-to-insert-class-that-has-a-list-object-fie?noredirect=1&lq=1
// https://stackoverflow.com/questions/44582397/android-room-persistent-library-typeconverter-error-of-error-cannot-figure-ou

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

	@TypeConverter
	fun fromStringList(value: List<String>): String {
		val gson = Gson()
		val type = object : TypeToken<List<String>>() {}.type
		return gson.toJson(value, type)
	}

	@TypeConverter
	fun toStringList(value: String): List<String> {
		val gson = Gson()
		val type = object : TypeToken<List<String>>() {}.type
		return gson.fromJson(value, type)
	}

	@TypeConverter
	fun fromFloatList(value: List<Float>): String {
		val gson = Gson()
		val type = object : TypeToken<List<Float>>() {}.type
		return gson.toJson(value, type)
	}

	@TypeConverter
	fun toFloatList(value: String): List<Float> {
		val gson = Gson()
		val type = object : TypeToken<List<Float>>() {}.type
		return gson.fromJson(value, type)
	}

	@TypeConverter
	fun fromIntList(value: List<Int>): String {
		val gson = Gson()
		val type = object : TypeToken<List<Int>>() {}.type
		return gson.toJson(value, type)
	}

	@TypeConverter
	fun toIntList(value: String): List<Int> {
		val gson = Gson()
		val type = object : TypeToken<List<Int>>() {}.type
		return gson.fromJson(value, type)
	}

	@TypeConverter
	fun fromLongList( value : List<Long> ) : String {
		val gson = Gson()
		val type = object : TypeToken<List<Long>>() {}.type
		return gson.toJson(value, type)
	}

	@TypeConverter
	fun toLongList(value: String): List<Long>{
		val gson = Gson()
		val type = object : TypeToken<List<Long>>() {}.type
		return gson.fromJson(value, type)
	}

	@TypeConverter
	fun fromPairList( value : List<Pair< String , Long>> ) : String {
		val gson = Gson()
		val type = object : TypeToken< List< Pair<String , Long> > >() {}.type
		return gson.toJson(value, type)
	}

	@TypeConverter
	fun toPairList(value: String): List<Pair< String , Long>>{
		val gson = Gson()
		val type = object : TypeToken< List< Pair<String , Long> > >() {}.type
		return gson.fromJson(value, type)
	}

}