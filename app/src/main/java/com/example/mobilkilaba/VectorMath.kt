package com.example.mobilkilaba

import kotlin.random.Random

class VectorMath(private val random: Random = Random.Default) {
    fun generateRandomVector(length: Int, minValueInclusive: Int = -9, maxValueInclusive: Int = 9): List<Int> {
        require(length > 0) { "Vector length must be positive" }
        require(minValueInclusive <= maxValueInclusive) { "Invalid range" }
        return List(length) { random.nextInt(from = minValueInclusive, until = maxValueInclusive + 1) }
    }

    fun dotProduct(a: List<Int>, b: List<Int>): Long {
        require(a.size == b.size) { "Vectors must be the same length" }
        var sum = 0L
        for (i in a.indices) {
            sum += a[i].toLong() * b[i].toLong()
        }
        return sum
    }
}


