package com.projectdelta.habbit.data

import kotlin.random.Random

object NotFound {

	private val TextOnlyEmoticons = listOf<String>(
		"(='X'=)", "^o^", "(·_·)", "(>_<)",
		"(≥o≤)", "(ㆆ _ ㆆ)", "(╥﹏╥)", "<(^_^)>", "=^_^=", "(-_-;)", "(*^_^*)", "(◠﹏◠)"
	)

	/**
	 * Returns a random text emoticon
	 */
	fun get() = TextOnlyEmoticons[Random.nextInt(0, TextOnlyEmoticons.size)]

}