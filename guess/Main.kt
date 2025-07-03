package com.github.kasnil.kotlin.invent.guess

import kotlin.random.Random
import kotlin.random.nextInt

fun main() {
    println("Hello! What is your name?")
    val myName = readLine()!!

    var number = Random.nextInt(1..20)
    println("Well, $myName, I am thinking of a number between 1 and 20.")

    var guess = 0
    var guessesTaken = 0
    for (guessesTaken in 0..number) {
        println("Take a guess.")
        guess = readLine()!!.toInt()

        if (guess < number) {
            println("Your guess is too low.")
        } else if (guess > number) {
            println("Your guess is too high.")
        } else {
            break
        }
    }

    if (guess == number) {
        println("Good job, $myName! You guessed my number in ${guessesTaken + 1} guesses!")
    } else {
        println("Nope. The number I was thinking of was $number.")
    }
}
