# Karnaugh

This project contains an implementation of Karnaugh maps. A Karnaugh map is a
visualization of a truth table for a boolean expression. The values of the
inputs are written in the headers of the rows and columns of the table, while
the values of the outputs are written in the cells.

If there are more than 2 variables, some rows/columns will represent the values
of more than one variable. So if the columns are for variables `C`, `D`, and
`E`, for example, there would be one column for `010`.

The order of values is a special binary ordering called a Gray Code, which
ensures that each successive value only differ in one bit. This makes them handy
for spotting patterns in the expression and can be used to minimize the number
of logic gates needed to implement a circuit.

## Examples

### Simple example

```scala
scala> import karnaugh._
import karnaugh._

scala> import karnaugh.Implicits._
import karnaugh.Implicits._

scala> val e = ("a" or "b") and ~(("c" and "d") or "e")
e: karnaugh.Exp.And = (a ∨ b) ∧ ¬((c ∧ d) ∨ e)

scala> val tt = e.toTruthTable
tt: karnaugh.TruthTable =
a := 0 | b := 0 | c := 0 | d := 0 | e := 0 -> 0
a := 0 | b := 0 | c := 0 | d := 0 | e := 1 -> 0
a := 0 | b := 0 | c := 0 | d := 1 | e := 0 -> 0
a := 0 | b := 0 | c := 0 | d := 1 | e := 1 -> 0
a := 0 | b := 0 | c := 1 | d := 0 | e := 0 -> 0
a := 0 | b := 0 | c := 1 | d := 0 | e := 1 -> 0
a := 0 | b := 0 | c := 1 | d := 1 | e := 0 -> 0
a := 0 | b := 0 | c := 1 | d := 1 | e := 1 -> 0
a := 0 | b := 1 | c := 0 | d := 0 | e := 0 -> 1
a := 0 | b := 1 | c := 0 | d := 0 | e := 1 -> 0
a := 0 | b := 1 | c := 0 | d := 1 | e := 0 -> 1
a := 0 | b := 1 | c := 0 | d := 1 | e := 1 -> 0
a := 0 | b := 1 | c := 1 | d := 0 | e := 0 -> 1
a := 0 | b := 1 | c := 1 | d := 0 | e := 1 -> 0
a := 0 | b := 1 | c := 1 | d := 1 | e := 0 -> 0
a := 0 | b := 1 | c := 1 | d := 1 | e := 1 -> 0
a :...

scala> tt.karnaughMap
res0: String =
abc\de 00 01 11 10
   000  0  0  0  0
   001  0  0  0  0
   011  1  0  0  0
   010  1  0  0  1
   110  1  0  0  1
   111  1  0  0  0
   101  1  0  0  0
   100  1  0  0  1

scala> tt.minterms
res1: karnaugh.Exp = (¬a ∧ b ∧ ¬c ∧ ¬d ∧ ¬e) ∨ (¬a ∧ b ∧ ¬c ∧ d ∧ ¬e) ∨ (¬a ∧ b ∧ c ∧ ¬d ∧ ¬e) ∨ (a ∧ ¬b ∧ ¬c ∧ ¬d ∧ ¬e) ∨ (a ∧ ¬b ∧ ¬c ∧ d ∧ ¬e) ∨ (a ∧ ¬b ∧ c ∧ ¬d ∧ ¬e) ∨ (a ∧ b ∧ ¬c ∧ ¬d ∧ ¬e) ∨ (a ∧ b ∧ ¬c ∧ d ∧ ¬e) ∨ (a ∧ b ∧ c ∧ ¬d ∧ ¬e)

scala> tt.maxterms
res2: karnaugh.Exp = (a ∨ b ∨ c ∨ d ∨ e) ∧ (a ∨ b ∨ c ∨ d ∨ ¬e) ∧ (a ∨ b ∨ c ∨ ¬d ∨ e) ∧ (a ∨ b ∨ c ∨ ¬d ∨ ¬e) ∧ (a ∨ b ∨ ¬c ∨ d ∨ e) ∧ (a ∨ b ∨ ¬c ∨ d ∨ ¬e) ∧ (a ∨ b ∨ ¬c ∨ ¬d ∨ e) ∧ (a ∨ b ∨ ¬c ∨ ¬d ∨ ¬e) ∧ (a ∨ ¬b ∨ c ∨ d ∨ ¬e) ∧ (a ∨ ¬b ∨ c ∨ ¬d ∨ ¬e) ∧ (a ∨ ¬b ∨ ¬c ∨ d ∨ ¬e) ∧ (a ∨ ¬b ∨ ¬c ∨ ¬d ∨ e) ∧ (a ∨ ¬b ∨ ¬c ∨ ¬d ∨ ¬e) ∧ (¬a ∨ b ∨ c ∨ d ∨ ¬e) ∧ (¬a ∨ b ∨ c ∨ ¬d ∨ ¬e) ∧ (¬a ∨ b ∨ ¬c ∨ d ∨ ¬e) ∧ (¬a ∨ b ∨ ¬c ∨ ¬d ∨ e) ∧ (¬a ∨ b ∨ ¬c ∨ ¬d ∨ ¬e) ∧ (¬a ∨ ¬b ∨ c ∨ d ∨ ¬e) ∧ (¬a ∨ ¬b ∨ c ∨ ¬d ∨ ¬e) ∧ (¬a ∨ ¬b ∨ ¬c ∨ d ∨ ¬e) ∧ (¬a ∨ ¬b ∨ ¬c ∨ ¬d ∨ e) ∧ (¬a ∨ ¬b ∨ ¬c ∨ ¬d ∨ ¬e)
```

### One-bit adder with carry

```scala
scala> import karnaugh._
import karnaugh._

scala> import karnaugh.Implicits._
import karnaugh.Implicits._

scala> val tableConfig = """
     | Inputs
     | A B Cin
     |
     | Outputs
     | Cout S
     |
     | Table
     | A B Cin Cout S
     | 0 0 0 0 0
     | 0 0 1 0 1
     | 0 1 0 0 1
     | 0 1 1 1 0
     | 1 0 0 0 1
     | 1 0 1 1 0
     | 1 1 0 1 0
     | 1 1 1 1 1
     | """
tableConfig: String =
"
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
"

scala> val tables = TruthTableParser.parse(tableConfig).valueOrDie
tables: Map[String,karnaugh.TruthTable] =
Map(Cout -> A := 0 | B := 0 | Cin := 0 -> 0
A := 0 | B := 0 | Cin := 1 -> 0
A := 0 | B := 1 | Cin := 0 -> 0
A := 0 | B := 1 | Cin := 1 -> 1
A := 1 | B := 0 | Cin := 0 -> 0
A := 1 | B := 0 | Cin := 1 -> 1
A := 1 | B := 1 | Cin := 0 -> 1
A := 1 | B := 1 | Cin := 1 -> 1, S -> A := 0 | B := 0 | Cin := 0 -> 0
A := 0 | B := 0 | Cin := 1 -> 1
A := 0 | B := 1 | Cin := 0 -> 1
A := 0 | B := 1 | Cin := 1 -> 0
A := 1 | B := 0 | Cin := 0 -> 1
A := 1 | B := 0 | Cin := 1 -> 0
A := 1 | B := 1 | Cin := 0 -> 0
A := 1 | B := 1 | Cin := 1 -> 1)

scala> val carryOutTable = tables("Cout")
carryOutTable: karnaugh.TruthTable =
A := 0 | B := 0 | Cin := 0 -> 0
A := 0 | B := 0 | Cin := 1 -> 0
A := 0 | B := 1 | Cin := 0 -> 0
A := 0 | B := 1 | Cin := 1 -> 1
A := 1 | B := 0 | Cin := 0 -> 0
A := 1 | B := 0 | Cin := 1 -> 1
A := 1 | B := 1 | Cin := 0 -> 1
A := 1 | B := 1 | Cin := 1 -> 1

scala> carryOutTable.karnaughMap
res3: String =
AB\Cin 0 1
    00 0 0
    01 0 1
    11 1 1
    10 0 1

scala> val sumTable = tables("S")
sumTable: karnaugh.TruthTable =
A := 0 | B := 0 | Cin := 0 -> 0
A := 0 | B := 0 | Cin := 1 -> 1
A := 0 | B := 1 | Cin := 0 -> 1
A := 0 | B := 1 | Cin := 1 -> 0
A := 1 | B := 0 | Cin := 0 -> 1
A := 1 | B := 0 | Cin := 1 -> 0
A := 1 | B := 1 | Cin := 0 -> 0
A := 1 | B := 1 | Cin := 1 -> 1

scala> sumTable.karnaughMap
res4: String =
AB\Cin 0 1
    00 0 1
    01 1 0
    11 0 1
    10 1 0
```

## Self-reminder:

Generated tut documentation is in target/scala-2.12/tut/example.md

TODO: Generate this file automatically using tut.
