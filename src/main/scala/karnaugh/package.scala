import fastparse.core._

package object karnaugh {
  implicit class StringExtensions(self: String) {
    def :=(value: TruthValue) = Assignment(self, value)
  }

  implicit class EitherExtensions[E, A](self: Either[E, A]) {
    def valueOrDie: A = self match {
      case Left(e) => throw new Exception(e.toString)
      case Right(a) => a
    }
  }

  implicit class ParsedExtensions[A, Elem, Repr](self: Parsed[A, Elem, Repr]) {
    def toEither : Either[String, A] =
      self match {
        case Parsed.Success(v, _) => Right(v)
        case failure => Left(failure.toString)
      }
  }
}
