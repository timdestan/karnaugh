# Karnaugh

```scala
scala> import karnaugh._
import karnaugh._

scala> import karnaugh.Implicits._
import karnaugh.Implicits._

scala> val e = ("a" or "b") and not (("c" and "d") or "e")
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
scala> tt.minterms
res1: karnaugh.Exp = (¬a ∧ b ∧ ¬c ∧ ¬d ∧ ¬e) ∨ (¬a ∧ b ∧ ¬c ∧ d ∧ ¬e) ∨ (¬a ∧ b ∧ c ∧ ¬d ∧ ¬e) ∨ (a ∧ ¬b ∧ ¬c ∧ ¬d ∧ ¬e) ∨ (a ∧ ¬b ∧ ¬c ∧ d ∧ ¬e) ∨ (a ∧ ¬b ∧ c ∧ ¬d ∧ ¬e) ∨ (a ∧ b ∧ ¬c ∧ ¬d ∧ ¬e) ∨ (a ∧ b ∧ ¬c ∧ d ∧ ¬e) ∨ (a ∧ b ∧ c ∧ ¬d ∧ ¬e)

```
