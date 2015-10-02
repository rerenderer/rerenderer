package com.nvbn.tryrerenderer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.util.Log
import com.cognitect.transit.Keyword
import com.cognitect.transit.Symbol

// Vars
open data class VarOrVal()

data class Var(val id: String) : VarOrVal() {
    fun toVal(vars: Map<String, Any?>) = Val(vars.get(id))
}

data class Val(val value: Any?) : VarOrVal()

fun varToVal(vars: Map<String, Any?>, x: VarOrVal): Any? = when (x) {
    is Var -> Val(vars.get(x.id))
    is Val -> x
    else -> throw Exception("Can't unpack $x")
}

fun toVarsOrVals(xs: Any): List<VarOrVal> = (xs as List<List<*>>).map { x ->
    when (x.get(0)) {
        ":var" -> Var(x.get(1) as String)
        ":val" -> Val(x.get(1))
        else -> throw Exception("Unknown variable definition $x")
    }
}

open data class Command()

data class New(val resultVar: String, val cls: String, val args: List<VarOrVal>) : Command()

data class Call(val resultVar: String, val objVar: String, val method: String,
                val args: List<VarOrVal>) : Command()

data class Get(val resultVar: String, val objVar: String, val attr: String) : Command()


class Stopped : Exception()

class Interpreter {
    val TAG = "INTERPRETE"
    var callId = 0

    fun call(script: Collection<List<Any>>, rootId: String): Bitmap {
        callId += 1
        val currentCallId = callId

        val actions = script.map { line ->
            Log.d(TAG, line.toString())
            when (line.get(0)) {
                ":new" -> New(
                        line.get(1) as String,
                        line.get(2) as String,
                        toVarsOrVals(line.get(3)))
                ":call" -> Call(
                        line.get(1) as String,
                        line.get(2) as String,
                        line.get(3) as String,
                        toVarsOrVals(line.get(3)))
                ":get" -> Get(
                        line.get(1) as String,
                        line.get(2) as String,
                        line.get(3) as String)
                else -> throw Exception("Unknown action $line")
            }
        }
        return actions.fold(mapOf<String, Any?>(), { vars, action ->
            if (callId != currentCallId)
                throw Stopped()
            interpeteLine(vars, action)
        }).get(rootId) as Bitmap
    }

    fun prepareArg(vars: Map<String, Any?>, arg: VarOrVal): Any? = when (arg) {
        is Var -> arg.toVal(vars).value
        is Val -> arg.value
        else -> throw Exception()
    }

    fun prepareArgs(vars: Map<String, Any?>, args: List<VarOrVal>): List<Any?> = args.map { arg ->
        prepareArg(vars, arg)
    }

    fun interpeteLine(vars: Map<String, Any?>, line: Command): Map<String, Any?> {
        Log.d(TAG, line.toString())
        return when (line) {
            is New -> vars.plus(line.resultVar to doNew(
                    vars, line.cls, line.args))
            is Call -> vars.plus(line.resultVar to doCall(
                    vars, vars.getOrElse(line.objVar, {-> line.objVar}),
                    line.method, prepareArgs(vars, line.args)))
            is Get -> vars.plus(line.resultVar to doGet(
                    vars, line.objVar, line.attr))
            else -> vars
        }
    }

}
