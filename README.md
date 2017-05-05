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

## Example

The following should be directly runnable in the SBT REPL.

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
