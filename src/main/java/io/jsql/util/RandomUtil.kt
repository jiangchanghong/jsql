/*
 * Java-based distributed database like Mysql
 */
package io.jsql.util

/**
 * @author jsql
 */
object RandomUtil {
    private val bytes = charArrayOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm', 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N', 'M')
    private val multiplier = 0x5DEECE66DL
    private val addend = 0xBL
    private val mask = (1L shl 48) - 1
    private val integerMask = (1L shl 33) - 1
    private val seedUniquifier = 8682522807148012L

    private var seed: Long = 0

    init {
        var s = seedUniquifier + System.nanoTime()
        s = s xor multiplier and mask
        seed = s
    }

    fun randomBytes(size: Int): ByteArray {
        val ab = ByteArray(size)
        for (i in 0..size - 1) {
            ab[i] = randomByte(bytes.map({ it.toByte() }).toByteArray())
        }
        return ab
    }

    private fun randomByte(b: ByteArray): Byte {
        val ran = (next() and integerMask).ushr(16).toInt()
        return b[ran % b.size]
    }

    private operator fun next(): Long {
        val oldSeed = seed
        var nextSeed = 0L
        do {
            nextSeed = oldSeed * multiplier + addend and mask
        } while (oldSeed == nextSeed)
        seed = nextSeed
        return nextSeed
    }

}