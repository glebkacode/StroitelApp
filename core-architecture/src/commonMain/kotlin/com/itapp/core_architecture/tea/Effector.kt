package com.itapp.core_architecture.tea

interface Effector<in E, I, M> {
    fun init(callback: Callback<I, M>)

    fun onEffect(effect: E)
    fun dispose()

    interface Callback<in I, in M> {
        fun onIntent(intent: I)
        fun onEvent(event: M)
    }
}