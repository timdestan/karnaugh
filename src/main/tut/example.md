
Simple example:

```tut

import karnaugh._
import karnaugh.Implicits._
val e = ("a" or "b") and ~(("c" and "d") or "e")
val tt = e.toTruthTable.valueOrDie
tt.karnaughMap
tt.minterms
tt.maxterms

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
carryOutTable.karnaughMap

val sumTable = tables("S")
sumTable.karnaughMap

```
