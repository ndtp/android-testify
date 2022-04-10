/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2020 Shopify Inc.
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
package dev.testify.internal.processor.compare.colorspace

import com.github.ajalt.colormath.RGB
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * The CIELAB color space (also known as CIE L*a*b* or sometimes abbreviated as simply "Lab" color
 * space) is a color space defined by the International Commission on Illumination (CIE) in 1976.
 * It expresses color as three values: L* for the lightness from black (0) to white (100), a* from
 * green (−) to red (+), and b* from blue (−) to yellow (+). CIELAB was designed so that the same
 * amount of numerical change in these values corresponds to roughly the same amount of visually
 * perceived change.
 *
 * The CIE calls their distance metric ΔE*ab where delta is a Greek letter often used to denote
 * difference, and E stands for Empfindung; German for "sensation".
 *
 * If deltaE < 1 then the difference can't recognized by human eyes.
 *
 * https://en.wikipedia.org/wiki/Color_difference#CIEDE2000
 *
 * Calculate the colour difference value between two colours in lab space.
 *
 * @param L1 first colour's L component
 * @param a1 first colour's a component
 * @param b1 first colour's b component
 * @param L2 second colour's L component
 * @param a2 second colour's a component
 * @param b2 second colour's b component
 * @return the CIE 2000 colour difference
 */
fun calculateDeltaE(L1: Double, a1: Double, b1: Double, L2: Double, a2: Double, b2: Double): Double {
    val lMean = (L1 + L2) / 2.0
    val c1 = sqrt(a1 * a1 + b1 * b1)
    val c2 = sqrt(a2 * a2 + b2 * b2)
    val cMean = (c1 + c2) / 2.0

    val g = (1.0 - sqrt(cMean.pow(7.0) / (cMean.pow(7.0) + 25.0.pow(7.0)))) / 2.0

    val a1prime = a1 * (1.0 + g)
    val a2prime = a2 * (1.0 + g)

    val c1prime = sqrt(a1prime * a1prime + b1 * b1)
    val c2prime = sqrt(a2prime * a2prime + b2 * b2)
    val cMeanPrime = (c1prime + c2prime) / 2.0

    val h1prime = atan2(b1, a1prime) + 2.0 * Math.PI * if (atan2(b1, a1prime) < 0) 1.0 else 0.0
    val h2prime = atan2(b2, a2prime) + 2.0 * Math.PI * if (atan2(b2, a2prime) < 0) 1.0 else 0.0
    val hMeanPrime =
        if (abs(h1prime - h2prime) > Math.PI) (h1prime + h2prime + 2.0 * Math.PI) / 2.0 else (h1prime + h2prime) / 2

    val t = 1.0 -
        0.17 *
        cos(hMeanPrime - Math.PI / 6.0) +
        0.24 *
        cos(2 * hMeanPrime) +
        0.32 *
        cos(3 * hMeanPrime + Math.PI / 30) -
        0.2 *
        cos(4 * hMeanPrime - 21 * Math.PI / 60)

    var deltaHPrime = if (abs(h1prime - h2prime) <= Math.PI) {
        h2prime - h1prime
    } else {
        if (h2prime <= h1prime) {
            h2prime - h1prime + 2.0 * Math.PI
        } else {
            h2prime - h1prime - 2.0 * Math.PI
        }
    }

    val deltaLPrime = L2 - L1
    val deltaCPrime = c2prime - c1prime
    deltaHPrime = 2.0 * sqrt(c1prime * c2prime) * sin(deltaHPrime / 2.0)

    val sL = 1.0 + 0.015 * (lMean - 50.0) * (lMean - 50.0) / sqrt(20.0 + (lMean - 50.0) * (lMean - 50.0))
    val sC = 1.0 + 0.045 * cMeanPrime
    val sH = 1.0 + 0.015 * cMeanPrime * t

    val deltaTheta = 30.0 *
        Math.PI /
        180.0 *
        exp(-((180.0 / Math.PI * hMeanPrime - 275.0) / 25.0) * ((180.0 / Math.PI * hMeanPrime - 275.0) / 25.0))
    val rC = 2.0 * sqrt(cMeanPrime.pow(7.0) / (cMeanPrime.pow(7.0) + 25.0.pow(7.0)))
    val rT = -rC * sin(2.0 * deltaTheta)

    return sqrt(
        deltaLPrime / (KL * sL) * (deltaLPrime / (KL * sL)) +
            deltaCPrime / (KC * sC) * (deltaCPrime / (KC * sC)) +
            deltaHPrime / (KH * sH) * (deltaHPrime / (KH * sH)) +
            rT * (deltaCPrime / (KC * sC)) * (deltaHPrime / (KH * sH))
    )
}

fun calculateDeltaE(baselinePixel: Int, currentPixel: Int): Double {
    val baselineLab = RGB.fromInt(baselinePixel).toLAB()
    val currentLab = RGB.fromInt(currentPixel).toLAB()
    return calculateDeltaE(
        baselineLab.l,
        baselineLab.a,
        baselineLab.b,
        currentLab.l,
        currentLab.a,
        currentLab.b
    )
}

private const val KL = 1.0
private const val KC = 1.0
private const val KH = 1.0
