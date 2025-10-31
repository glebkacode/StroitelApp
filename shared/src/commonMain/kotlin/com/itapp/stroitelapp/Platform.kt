package com.itapp.stroitelapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform