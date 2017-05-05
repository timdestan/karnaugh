package karnaugh
package pp

// Tree for pretty printing. Currently the only interesting thing this does is
// removes unnecessary parens. Currently we just have to track the type of the
// enclosing node.
abstract trait Tree

object Tree {
  abstract trait Context
  case object CTop extends Context
  case object CAnd extends Context
  case object COr extends Context
  case object CNot extends Context

  def wrapInParens(s: String) = s"($s)"

  case class Atom(value: String) extends Tree {
    override def toString = value
  }

  case class Not(child: Tree) extends Tree {
    override def toString = s"¬${child.toString}"
  }

  case class And(children: List[Tree], parentContext: Context) extends Tree {
    def renderedChildren = children.mkString(" ∧ ")

    override def toString = parentContext match {
      case CNot | COr => wrapInParens(renderedChildren)
      case CTop | CAnd => renderedChildren
    }
  }

  case class Or(children: List[Tree], parentContext: Context) extends Tree {
    def renderedChildren = children.mkString(" ∨ ")

    override def toString = parentContext match {
      case CNot | CAnd => wrapInParens(renderedChildren)
      case CTop | COr => renderedChildren
    }
  }

  def apply(exp: Exp): Tree = {
    def loop(exp: Exp, parentContext: Context): Tree = exp match {
      case Exp.Variable(v) => Atom(v)
      case Exp.Not(e) => Not(loop(e, CNot))
      case Exp.Or(es) => Or(es.map(loop(_, COr)), parentContext)
      case Exp.And(es) => And(es.map(loop(_, CAnd)), parentContext)
      case Exp.Literal(v) => Atom(v.toString)
    }
    loop(exp, CTop)
  }
}
