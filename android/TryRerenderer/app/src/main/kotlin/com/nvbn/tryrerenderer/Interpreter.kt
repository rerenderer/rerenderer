package com.nvbn.tryrerenderer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.util.Log
import com.cognitect.transit.Keyword
import com.cognitect.transit.Symbol

data class New(val resultVar: String, val cls: String, val args: List<Any?>)

data class Call(val resultVar: String, val objVar: Any?, val method: String,
                val args: List<Any?>)

data class Get(val resultVar: String, val objVar: String, val attr: String)

class Interpreter {
    val TAG = "INTERPRETE"

    fun call(script: Collection<List<Any>>, rootId: String): Bitmap {
        val actions = script.map { line ->
            Log.d(TAG, line.toString())
            when (line.get(0)) {
                ":new" -> New(
                        line.get(1) as String,
                        line.get(2) as String,
                        line.get(3) as List<Any?>)
                ":call" -> Call(
                        line.get(1) as String,
                        line.get(2) as String,
                        line.get(3) as String,
                        line.get(4) as List<Any?>)
                ":get" -> Get(
                        line.get(1) as String,
                        line.get(2) as String,
                        line.get(3) as String)
                else -> throw Exception("Unknown action $line")
            }
        }
        return actions.fold(mapOf<String, Any?>(), { vars, action ->
            interpeteLine(vars, action)
        }).get(rootId) as Bitmap
    }

    fun prepareArg(vars: Map<String, Any?>, arg: Any?): Any? = when (arg) {
        is String -> vars.getOrElse(arg, { -> arg })
        else -> arg
    }

    fun prepareArgs(vars: Map<String, Any?>, args: List<Any?>): List<Any?> = args.map { arg ->
        prepareArg(vars, arg)
    }

    fun interpeteLine(vars: Map<String, Any?>, line: Any): Map<String, Any?> {
        Log.d(TAG, line.toString())
        return when (line) {
            is New -> vars.plus(line.resultVar to doNew(
                    vars, line.copy(args = prepareArgs(vars, line.args))))
            is Call -> vars.plus(line.resultVar to doCall(
                    vars, line.copy(
                    objVar = prepareArg(vars, line.objVar as String),
                    args = prepareArgs(vars, line.args))))
            is Get -> vars.plus(line.resultVar to doGet(vars, line))
            else -> vars
        }
    }

}
