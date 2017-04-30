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

case class TruthTable(entries: List[TruthTable.Entry]) { self =>
  import TruthTable._

  override def toString = entries.mkString("\n")

  def vars: List[String] = entries.map(_.vars).headOption.getOrElse(Nil)

  def karnaughMap: String = {
    val vars = self.vars

    val entriesByInputSet = entries.groupBy(_.input.toSet)

    def showTable(rowVars: List[String],
                  colVars: List[String]): String = {
      val rowValues = TruthTable.grayCode(rowVars)
      val colValues = TruthTable.grayCode(colVars)

      def assignmentValueString(as : List[Assignment]) =
          as.map(_.value).mkString

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
        c => formatRowEntry(assignmentValueString(c))
      }).mkString(" ")
      val tableRows = rowValues.map {
        r => {
          val rowHdr = formatRowHdr(assignmentValueString(r))
          val row = colValues.map {
            c => {
              val assignments = r ++ c
              // Dies if we screwed up and this isn't a real entry.
              val entry = entriesByInputSet(assignments.toSet).head
              val result = entry.value
              formatRowEntry(result.toString)
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

  type Input = List[Assignment]
  type Output = TruthValue

  case class Entry(input: List[Assignment], value: TruthValue) {
    val byName = input.groupBy(_.name)

    val vars: List[String] = input.map(_.name)

    override def toString = input.mkString(" | ") + " -> " + value.toString
  }

  // In: [A := T, B := F, C := T, D := DC]
  // Out: And(A, Not(B), C, DC)
  // TODO: Could just drop the DC's
  def toConjunction(assignments: List[Assignment]) = And(assignments.map {
    case Assignment(name, v) => v match {
      case T => Variable(name)
      case F => Not(Variable(name))
      case DC => Literal(DC)
    }
  })

  def full(xs: String*): List[Input] = full(xs.toList)
  def full(xs: List[String]): List[Input] =
    xs.foldRight[List[Input]](List(Nil)) {
      (x, as) => as.map((x := F) :: _) ++ as.map((x := T) :: _)
    }

  def grayCode(xs: String*): List[Input] = grayCode(xs.toList)
  def grayCode(xs: List[String]): List[Input] =
    xs.foldRight[List[Input]](List(Nil)) {
      (x, as) => as.map((x := F) :: _) ++ as.reverse.map((x := T) :: _)
    }
}

sealed trait Exp { self =>
  import Exp._

  def or(other: Exp) = Or(List(self, other))
  def and(other: Exp) = And(List(self, other))

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

  def eval (input: TruthTable.Input): TruthValue = self match {
    case Variable(v) =>
      input.find(_.name == v).map(_.value).getOrElse(DC)
    case Not(e) => e.eval(input) match {
      case T => F
      case F => T
      case DC => DC
    }
    case Or(subs) => subs.map(_.eval(input)).foldLeft[TruthValue](F) {
      case (T, _) | (_, T) => T
      case (DC, _) | (_, DC) => DC
      case (F, F) => F
    }
    case And(subs) => subs.map(_.eval(input)).foldLeft[TruthValue](T) {
      case (F, _) | (_, F) => F
      case (DC, _) | (_, DC) => DC
      case (T, T) => T
    }
    case Literal(l) => l
  }

  def toTruthTable: TruthTable = {
    val inputs = TruthTable.full(self.vars.toList.sorted)
    TruthTable(inputs.map {
      input => TruthTable.Entry(input, self.eval(input))
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
  def not(e: Exp) = Exp.Not(e)  // Not so implicit
}
