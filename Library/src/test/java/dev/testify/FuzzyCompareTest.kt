/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 ndtp
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dev.testify

import com.github.ajalt.colormath.LAB
import com.github.ajalt.colormath.RGB
import dev.testify.internal.processor.compare.colorspace.calculateDeltaE
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.lang.Long.parseLong
import kotlin.random.Random

class FuzzyCompareTest {

    @Test
    fun identicalLab() {
        assertEquals(0.0, calculateDeltaE("000000".lab, "000000".lab), 0.1)
        assertEquals(0.0, calculateDeltaE("0000FF".lab, "0000FF".lab), 0.1)
        assertEquals(0.0, calculateDeltaE("00FF00".lab, "00FF00".lab), 0.1)
        assertEquals(0.0, calculateDeltaE("ABCDEF".lab, "ABCDEF".lab), 0.1)
        assertEquals(0.0, calculateDeltaE("FF0000".lab, "FF0000".lab), 0.1)
        assertEquals(0.0, calculateDeltaE("FFFFFF".lab, "FFFFFF".lab), 0.1)
    }

    @Test
    fun differenceLab() {
        assertEquals(0.2, calculateDeltaE("0F0F0F".lab, "0E0E0E".lab), 0.1)
        assertEquals(0.4, calculateDeltaE("000000".lab, "000001".lab), 0.1)
        assertEquals(2.6, calculateDeltaE("ED0000".lab, "E10000".lab), 0.1)
        assertEquals(33.3, calculateDeltaE("00FF00".lab, "FFFFFF".lab), 0.1)
        assertEquals(39.7, calculateDeltaE("0000FF".lab, "000000".lab), 0.1)
        assertEquals(45.8, calculateDeltaE("FF0000".lab, "FFFFFF".lab), 0.1)
        assertEquals(50.4, calculateDeltaE("FF0000".lab, "000000".lab), 0.1)
        assertEquals(52.9, calculateDeltaE("FF0000".lab, "0000FF".lab), 0.1)
        assertEquals(64.2, calculateDeltaE("0000FF".lab, "FFFFFF".lab), 0.1)
        assertEquals(83.2, calculateDeltaE("00FF00".lab, "0000FF".lab), 0.1)
        assertEquals(86.6, calculateDeltaE("FF0000".lab, "00FF00".lab), 0.1)
        assertEquals(87.9, calculateDeltaE("00FF00".lab, "000000".lab), 0.1)
    }

    @Test
    fun extremeDifferenceLab() {
        assertEquals(100.0, calculateDeltaE("000000".lab, "FFFFFF".lab), 0.1)
        assertEquals(100.0, calculateDeltaE("FFFFFF".lab, "000000".lab), 0.1)
        assertEquals(52.9, calculateDeltaE("FF0000".lab, "0000FF".lab), 0.1)
        assertEquals(83.2, calculateDeltaE("00FF00".lab, "0000FF".lab), 0.1)
        assertEquals(86.6, calculateDeltaE("FF0000".lab, "00FF00".lab), 0.1)
    }

    @Test
    fun grayscaleDifferenceLab() {
        (25..230).forEach { value1 ->
            val baseColor = RGB(value1, value1, value1)
            val lab1 = baseColor.toLAB()
            (-25..25).forEach { value2 ->
                val currentColor = RGB(value1 + value2, value1 + value2, value1 + value2)
                val lab2 = currentColor.toLAB()
                val deltaE = calculateDeltaE(lab1, lab2)
                if (deltaE >= 10) {
                    fail(
                        "deltaE of #${baseColor.toHex()} and " +
                            "#${currentColor.toHex()} is " +
                            "$deltaE which is greater than 10"
                    )
                }
            }
        }
    }

    @Test
    fun redDifferenceLab() {
        // Visually very similar Red-based colors
        (0..20).forEach { value1 ->
            val baseColor = RGB(255 - value1, value1, value1)
            val lab1 = baseColor.toLAB()
            (0..20).forEach { value2 ->
                val currentColor = RGB(255 - value2, value2, value2)
                val lab2 = currentColor.toLAB()
                val deltaE = calculateDeltaE(lab1, lab2)
                assertTrue(
                    "deltaE of ${baseColor.toHex()} and " +
                        "${currentColor.toHex()} is " +
                        "$deltaE which is greater than 5",
                    deltaE < 5
                )
            }
        }
    }

    @Test
    fun greenDifferenceLab() {
        // Visually very similar Green-based colors
        (0..20).forEach { value1 ->
            val baseColor = RGB(value1, 255 - value1, value1)
            val lab1 = baseColor.toLAB()
            (0..20).forEach { value2 ->
                val currentColor = RGB(value2, 255 - value2, value2)
                val lab2 = currentColor.toLAB()
                val deltaE = calculateDeltaE(lab1, lab2)
                assertTrue(
                    "deltaE of ${baseColor.toHex()} and " +
                        "${currentColor.toHex()} is " +
                        "$deltaE which is greater than 5",
                    deltaE < 5
                )
            }
        }
    }

    @Test
    fun blueDifferenceLab() {
        // Visually very similar Blue-based colors
        (0..20).forEach { value1 ->
            val baseColor = RGB(value1, value1, 255 - value1)
            val lab1 = baseColor.toLAB()
            (0..20).forEach { value2 ->
                val currentColor = RGB(value2, value2, 255 - value2)
                val lab2 = currentColor.toLAB()
                val deltaE = calculateDeltaE(lab1, lab2)
                assertTrue(
                    "deltaE of ${baseColor.toHex()} and " +
                        "${currentColor.toHex()} is " +
                        "$deltaE which is greater than 5",
                    deltaE < 5
                )
            }
        }
    }

    @Test
    fun largeArea() {
        repeat(1024) {
            repeat(768) {
                val color1 = RGB(
                    Random.nextInt(5, 250),
                    Random.nextInt(5, 250),
                    Random.nextInt(5, 250)
                )
                val color2 = RGB(
                    color1.r + Random.nextInt(-5, 5),
                    color1.g + Random.nextInt(-5, 5),
                    color1.b + Random.nextInt(-5, 5)
                )
                val deltaE = calculateDeltaE(color1.toLAB(), color2.toLAB())
                assertTrue(
                    "deltaE of ${color1.toHex()} and ${color2.toHex()} is $deltaE which is greater than 12.5",
                    deltaE < 12.5
                )
            }
        }
    }

    private val String.lab: LAB
        get() {
            val a = parseLong(this, 16).toInt()
            return RGB.fromInt(a).toLAB()
        }

    private fun calculateDeltaE(lab1: LAB, lab2: LAB): Double {
        return calculateDeltaE(lab1.l, lab1.a, lab1.b, lab2.l, lab2.a, lab2.b)
    }
}
