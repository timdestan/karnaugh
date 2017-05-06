package karnaugh

sealed trait TruthValue

case object T extends TruthValue {
  override def toString = "1"
}
case object F extends TruthValue {
  override def toString = "0"
}
case object DC extends TruthValue

case class Assignment(name: String, value: TruthValue) {
  override def toString = s"$name := $value"
}


// TODO: This should enforce that the table is "full" (has entries for all 2^n
//       combinations of input vars) or karnaugh map blows up. This is always
//       true when generating from an expression but not otherwise.
class TruthTable(vars: List[String],
                 entries: List[TruthTable.RawEntry]) { self =>
  import TruthTable._

  override def toString = entries.map(_.toEntry(vars)).mkString("\n")

  def minterms: Exp =
    Exp.Or(entries.map(_.toEntry(vars)).filter(_.value == T).map(_.toMinterm))

  def maxterms: Exp =
    Exp.And(entries.map(_.toEntry(vars)).filter(_.value == F).map(_.toMaxterm))

  def karnaughMap: String = {
    val entriesByInputValues = entries.groupBy(_.inputValues)

    def showTable(rowVars: List[String],
                  colVars: List[String]): String = {
      val rowValues = TruthTable.grayCode(rowVars.size)
      val colValues = TruthTable.grayCode(colVars.size)

      // Example:
      //
      // AB\CD 00 01 11 10
      //    00  0  0  1  1
      //    01  1  1  1  1
      //    11  0  1  1  1
      //    10  1  1  0  1

      // The split variable names shown in the upper left.
      val splitNames: String = (rowVars ++ "\\" ++ colVars).mkString

      def formatRowHdr(hdr: String) = s"%${splitNames.size}s".format(hdr)
      def formatRowEntry(entry: String) = s"%${colVars.size}s".format(entry)

      var hdr = (splitNames :: colValues.map {
        c => formatRowEntry(c.mkString)
      }).mkString(" ")
      val tableRows = rowValues.map {
        r => {
          val rowHdr = formatRowHdr(r.mkString)
          val row = colValues.map {
            c => {
              val values = r ++ c
              // Dies if table is missing an entry for these inputs.
              val entry = entriesByInputValues(values).head
              formatRowEntry(entry.outputValue.toString)
            }
          }
          (rowHdr :: row).mkString(" ")
        }
      }
      (hdr :: tableRows).mkString("\n")
    }

    val split = if (vars.size % 2 == 0) (vars.size / 2) else (vars.size / 2 + 1)
    vars.splitAt(split) match {
      case (Nil, Nil) => "[empty]"
      case (rows, cols) => showTable(rows, cols)
    }
  }
}

object TruthTable {
  import Exp._

  def apply(entries: List[Entry]): Result[TruthTable] = {
    // Validate that each entry has the same set of vars in the same order,
    // and return that set of vars.
    def validateVars(entries: List[Entry],
                     vars: Option[List[String]])
                        : Result[List[String]] = entries match {
      case Nil => vars.toRight("No entries")
      case x :: xs => vars match {
        case None => validateVars(xs, Some(x.vars))
        case s@Some(vars) =>
          if (vars == x.vars) validateVars(xs, s)
          else Left("Mismatched vars.")
      }
    }
    for {
      vars <- validateVars(entries, None)
    } yield new TruthTable(vars, entries.map(_.toRawEntry))
  }

  // Entry that doesn't repeat the input vars in each row. Requires a truth
  // table to interpret.
  case class RawEntry(inputValues: List[TruthValue],
                      outputValue: TruthValue) {
    def toEntry(vars: List[String]) = Entry(toAssignments(vars), outputValue)

    def toAssignments(vars: List[String]) =
      vars.zip(inputValues).map((Assignment.apply(_,_)).tupled)
  }

  case class Entry(assignments: List[Assignment], value: TruthValue) {
    val byName = assignments.groupBy(_.name)

    val vars: List[String] = assignments.map(_.name)

    override def toString =
        assignments.mkString(" | ") + " -> " + value.toString

    def toRawEntry = RawEntry(assignments.map(_.value), value)

    def toMinterm: Exp = And(assignments.map {
      case Assignment(name, v) => v match {
        case T => Variable(name)
        case F => ~Variable(name)
        case DC => Literal(DC)
      }
    })

    def toMaxterm: Exp = Or(assignments.map {
      case Assignment(name, v) => v match {
        case T => ~Variable(name)
        case F => Variable(name)
        case DC => Literal(DC)
      }
    })
  }

  def full(n: Int): List[List[TruthValue]] =
    (1 to n).foldRight[List[List[TruthValue]]](List(Nil)) {
      (n, as) => as.map(F :: _) ++ as.map(T :: _)
    }

  def grayCode(n: Int): List[List[TruthValue]] =
    (1 to n).foldRight[List[List[TruthValue]]](List(Nil)) {
      (n, as) => as.map(F :: _) ++ as.reverse.map(T :: _)
    }
}

sealed trait Exp { self =>
  import Exp._

  def or(other: Exp) = Or(List(self, other))
  def and(other: Exp) = And(List(self, other))
  def unary_~(): Exp = Not(self)

  override def toString = pp.Tree(self).toString

  def vars: Set[String] = self match {
    case Variable(v) => Set(v)
    case Not(e) => e.vars
    case Or(subs) => subs.map(_.vars).toSet.flatten
    case And(subs) => subs.map(_.vars).toSet.flatten
    case Literal(_) => Set.empty
  }

  def cost: Int = self match {
    case Variable(v) => 0
    case Not(e) => 1
    case Or(subs) => 1 + subs.map(_.cost).sum
    case And(subs) => 1 + subs.map(_.cost).sum
    case Literal(_) => 0
  }

  def eval (assignments: List[Assignment]): TruthValue = self match {
    case Variable(v) =>
      assignments.find(_.name == v).map(_.value).getOrElse(DC)
    case Not(e) => e.eval(assignments) match {
      case T => F
      case F => T
      case DC => DC
    }
    case Or(subs) => subs.map(_.eval(assignments)).foldLeft[TruthValue](F) {
      case (T, _) | (_, T) => T
      case (DC, _) | (_, DC) => DC
      case (F, F) => F
    }
    case And(subs) => subs.map(_.eval(assignments)).foldLeft[TruthValue](T) {
      case (F, _) | (_, F) => F
      case (DC, _) | (_, DC) => DC
      case (T, T) => T
    }
    case Literal(l) => l
  }

  def toTruthTable: TruthTable = {
    val vars = self.vars.toList.sorted
    val inputRows: List[List[TruthValue]] = TruthTable.full(vars.size)
    new TruthTable(
      vars,
      inputRows.map {
        inputRow =>
          TruthTable.RawEntry(
            inputRow,
            self.eval(vars.zip(inputRow).map((Assignment.apply(_,_)).tupled)))
      })
  }
}

object Exp {
  case class Variable(name: String) extends Exp
  case class Not(e: Exp) extends Exp
  case class Or(subs: List[Exp]) extends Exp
  case class And(subs: List[Exp]) extends Exp
  case class Literal(tv: TruthValue) extends Exp
}

object Implicits {
  implicit def toVar(str: String) = Exp.Variable(str)
  implicit def toLit(tv: TruthValue) = Exp.Literal(tv)
}
