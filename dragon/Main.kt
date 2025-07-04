package com.github.kasnil.kotlin.invent.dragon

import kotlin.random.Random
import kotlin.random.nextInt

fun displayIntro() {
    println("""
    |You are in a land full of dragons. In front of you,
    |you see two caves. In one cave, the dragon is friendly
    |and will share his treasure with you. The other dragon
    |is greedy and hungry, and will eat you on sight.
    """.trimMargin())
}

fun chooseCave(): String {
    var cave: String? = null
    while (cave != "1" && cave != "2") {
        print("Which cave will you go into? (1 or 2) ")
        cave = readLine()?.trim() ?: ""
    }
    return cave!!
}

fun checkCave(chosenCave: String) {
    println("You approach the cave...")
    Thread.sleep(2_000)
    println("It is dark and spooky...")
    Thread.sleep(2_000)
    println("A large dragon jumps out in front of you! He opens his jaws and...")
    println()
    Thread.sleep(2_000)

    val friendlyCave = Random.nextInt(1..2).toString()

    if (chosenCave == friendlyCave) {
        println("Gives you his treasure!")
    } else {
        println("Gobbles you down in one bite!")
    }
}

fun main() {
    var playAgain = "yes"
    while (playAgain == "yes" || playAgain == "y") {
        displayIntro()
        val caveNumber = chooseCave()
        checkCave(caveNumber)

        println("Do you want to play again? (yes or no)")
        playAgain = readLine()?.trim() ?: ""
    }
}