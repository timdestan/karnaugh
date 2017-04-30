
Simple example:

```tut

import karnaugh._
import karnaugh.Implicits._
val e = ("a" or "b") and not (("c" and "d") or "e")
val tt = e.toTruthTable
println(tt.karnaughMap)

```

One-bit adder with carry:

```tut

import karnaugh._
import karnaugh.Implicits._

val tableConfig = """
Inputs
A B Cin

Outputs
Cout S

Table
A B Cin Cout S
0 0 0 0 0
0 0 1 0 1
0 1 0 0 1
0 1 1 1 0
1 0 0 0 1
1 0 1 1 0
1 1 0 1 0
1 1 1 1 1
"""

val tables = TruthTableParser.parse(tableConfig).valueOrDie

val carryOutTable = tables("Cout")
println(carryOutTable.karnaughMap)

val sumTable = tables("S")
println(sumTable.karnaughMap)

```
