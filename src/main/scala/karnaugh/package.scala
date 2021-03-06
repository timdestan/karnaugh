import fastparse.core._

package object karnaugh {
  type Result[A] = Either[String, A]

  implicit class StringExtensions(self: String) {
    def :=(value: TruthValue) = Assignment(self, value)
  }

  implicit class EitherExtensions[E, A](self: Either[E, A]) {
    def valueOrDie: A = self.asInstanceOf[Right[E, A]].value
  }

  implicit class ParsedExtensions[A, Elem, Repr](self: Parsed[A, Elem, Repr]) {
    def toResult : Result[A] =
      self match {
        case Parsed.Success(v, _) => Right(v)
        case failure => Left(failure.toString)
      }
  }
}
