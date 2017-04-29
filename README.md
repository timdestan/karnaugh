# Karnaugh

```scala
scala> import karnaugh._
import karnaugh._

scala> import karnaugh.Implicits._
import karnaugh.Implicits._

scala> val e = ("a" or "b") and not ("c")
e: karnaugh.Exp.And = a ∨ b ∧ ¬c

scala> e.toTruthTable
res0: karnaugh.TruthTable =
a := 0 | b := 0 | c := 0 -> 0
a := 0 | b := 0 | c := 1 -> 0
a := 0 | b := 1 | c := 0 -> 1
a := 0 | b := 1 | c := 1 -> 0
a := 1 | b := 0 | c := 0 -> 1
a := 1 | b := 0 | c := 1 -> 0
a := 1 | b := 1 | c := 0 -> 1
a := 1 | b := 1 | c := 1 -> 0
```
