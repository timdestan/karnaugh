
Simple example:

```tut

import karnaugh._
import karnaugh.Implicits._
val e = ("a" or "b") and not (("c" and "d") or "e")
val tt = e.toTruthTable
println(tt.karnaughMap)

```
