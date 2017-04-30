import fastparse.core._

package object karnaugh {
  implicit class StringExtensions(self: String) {
    def :=(value: TruthVal) = Assignment(self, value)
  }

  implicit class EitherExtensions[E, A](self: Either[E, A]) {
    def valueOrDie: A = self match {
      case Left(e) => throw new Exception(e.toString)
      case Right(a) => a
    }
  }

  implicit class VectorOfEitherExtensions[A](self: Vector[Either[String, A]]) {
    // One-off implementation of sequence for just Vector and Either. I should
    // just pull in cats...
    def sequence: Either[String, Vector[A]] =
      self.foldLeft[Either[String, Vector[A]]](Right(Vector())) {
        case (l@Left(_), _) => l
        case (_, Left(e)) => Left(e)
        case (Right(vs), Right(v)) => Right(vs :+ v)
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
