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
package dev.testify.accessibility.internal

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.Reader

internal class CheckResults(other: List<CheckResult>) : ArrayList<CheckResult>(other) {

    private val hasErrors: Boolean
        get() {
            return this.any { it.type == "ERROR" }
        }

    fun toJson(): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(this)
    }

    override fun toString(): String {
        return if (hasErrors) {
            "AccessibilityCheck failed.\n"
        } else {
            "AccessibilityCheck passed.\n"
        } +
            printType("ERROR") +
            printType("WARNING") +
            printType("INFO") +
            printType("RESOLVED") +
            printType("SUPPRESSED")
    }

    private fun printType(type: String): String {
        return if (this.any { it.type == type }) "    * $type:\n${this.prettyPrint(type)}" else ""
    }

    private fun List<CheckResult>.prettyPrint(filter: String): String {
        return if (this.isEmpty())
            "    * - [NONE]"
        else
            filter { it.type == filter }.joinToString(separator = "\n    * -", prefix = "    * -") {
                "${it.check}: [${it.elementClass}/${it.resourceName}] - ${it.message}"
            }
    }

    companion object {
        fun fromJson(json: Reader): CheckResults {
            val results = Gson().fromJson(json, Array<CheckResult>::class.java).toList()
            return CheckResults(results)
        }
    }
}
