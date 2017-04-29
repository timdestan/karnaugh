package karnaugh

sealed trait TruthVal

case object T extends TruthVal {
  override def toString = "1"
}
case object F extends TruthVal {
  override def toString = "0"
}
case object DC extends TruthVal

case class Assignment(name: String, value: TruthVal) {
  override def toString = s"$name := $value"
}

case class TruthTable(entries: List[TruthTable.Entry]) {
  import TruthTable._

  override def toString =
    entries.map {
      case (input: Input, tv: Output) =>
        input.map(_.toString).mkString(" | ") + " -> " + tv.toString
    }.mkString("\n")
}

object TruthTable {
  type Input = List[Assignment]
  type Output = TruthVal
  type Entry = Tuple2[Input, Output]

  def full(xs: String*): List[Input] = full(xs.toList)
  def full(xs: List[String]): List[Input] = xs.foldRight[List[Input]](List(Nil)) {
    (x, as) => as.map((x := F) :: _) ++ as.map((x := T) :: _)
  }

  def grayCode(xs: String*): List[Input] = grayCode(xs.toList)
  def grayCode(xs: List[String]): List[Input] = xs.foldRight[List[Input]](List(Nil)) {
    (x, as) => as.map((x := F) :: _) ++ as.reverse.map((x := T) :: _)
  }
}

sealed trait Exp { self =>
  import Exp._

  def or(other: Exp) = Or(self, other)
  def and(other: Exp) = And(self, other)

  override def toString = self match {
    case Variable(v) => v
    case Not(e) => "¬" + e.toString
    case Or(l, r) => l.toString + " ∨ " + r.toString
    case And(l, r) => l.toString + " ∧ " + r.toString
    case Literal(v) => v.toString
  }

  def varsOf: Set[String] = self match {
    case Variable(v) => Set(v)
    case Not(e) => e.varsOf
    case Or(l, r) => l.varsOf union r.varsOf
    case And(l, r) => l.varsOf union r.varsOf
    case Literal(_) => Set.empty
  }

  def toTruthTable: TruthTable = {
    val inputs = TruthTable.full(self.varsOf.toList.sorted)

    def eval (exp: Exp, input: TruthTable.Input): TruthVal = exp match {
      case Variable(v) =>
        input.find(_.name == v).map(_.value).getOrElse(DC)
      case Not(e) => eval(e, input) match {
        case T => F
        case F => T
        case DC => DC
      }
      case Or(l, r) => (eval(l, input), eval(r, input)) match {
        case (F, F) => F
        case (_, T) | (T , _) => T
        case _ => DC
      }
      case And(l, r) => (eval(l, input), eval(r, input)) match {
        case (T, T) => T
        case (_, F) | (F , _) => F
        case _ => DC
      }
      case Literal(l) => l
    }

    TruthTable(inputs.map {
      input => (input, eval(self, input))
    })
  }
}

object Exp {
  case class Variable(name: String) extends Exp
  case class Not(e: Exp) extends Exp
  case class Or(l: Exp, r: Exp) extends Exp
  case class And(l: Exp, r: Exp) extends Exp
  case class Literal(tv: TruthVal) extends Exp
}

object Implicits {
  implicit def toVar(str: String) = Exp.Variable(str)
  implicit def toLit(tv: TruthVal) = Exp.Literal(tv)
  def not(e: Exp) = Exp.Not(e)  // Not so implicit
}
