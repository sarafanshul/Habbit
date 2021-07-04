package com.projectdelta.habbit.util

import kotlin.random.Random

object NotFound {

		private val TextOnlyEmoticons = listOf<String>( "(='X'=)" , "^o^" , "(·_·)" , "(>_<)" ,
			"(≥o≤)" , "(ㆆ _ ㆆ)" , "(╥﹏╥)" , "<(^_^)>" , "=^_^=" , "( ͡° ͜ʖ ͡°)_/¯" , "(-_-;)" , "(*^_^*)")

	/**
	 * Returns a random text emoticon
	 */
	fun get() = TextOnlyEmoticons[Random.nextInt( 0 , TextOnlyEmoticons.size )]

}