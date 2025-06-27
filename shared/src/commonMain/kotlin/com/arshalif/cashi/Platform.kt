package com.arshalif.cashi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform