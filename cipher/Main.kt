package com.github.kasnil.kotlin.invent.cipher

const val SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
const val MAX_KEY_SIZE = SYMBOLS.length

fun getMode(): String {
    while (true) {
        println("Do you wish to encrypt or decrypt a message?")
        val mode = readLine()?.trim()?.lowercase() ?: ""
        if (mode in listOf("encrypt", "e", "decrypt", "d")) {
            return mode
        } else {
            println("Enter either \"encrypt\" or \"e\" or \"decrypt\" or \"d\".")
        }
    }
}

fun getMessage(): String {
    println("Enter your message:")
    return readLine()?.trim() ?: ""
}

fun getKey(): Int {
    while (true) {
        println("Enter the key number (1-$MAX_KEY_SIZE)")
        val key = readLine()?.trim()?.toIntOrNull() ?: 0
        if (key in 1..MAX_KEY_SIZE) {
            return key
        }
    }
}

fun getTranslatedMessage(
    mode: String,
    message: String,
    key: Int,
): String {
    val key =
        if (mode[0] == 'd') {
            -key
        } else {
            key
        }
    var translated = ""

    for (symbol in message) {
        var symbolIndex = SYMBOLS.indexOf(symbol)
        if (symbolIndex == -1) { // Symbol not found in SYMBOLS.
            // Just add this symbol without any change.
            translated += symbol
        } else {
            // Encrypt or decrypt
            symbolIndex += key

            if (symbolIndex >= SYMBOLS.length) {
                symbolIndex -= SYMBOLS.length
            } else if (symbolIndex < 0) {
                symbolIndex += SYMBOLS.length
            }

            translated += SYMBOLS[symbolIndex]
        }
    }
    return translated
}

fun main() {
    val mode = getMode()
    val message = getMessage()
    val key = getKey()
    println("Your translated text is:")
    println(getTranslatedMessage(mode, message, key))
}
