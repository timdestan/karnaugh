package object karnaugh {
  implicit class StringExtensions(self: String) {
    def :=(value: TruthVal) = Assignment(self, value)
  }

  implicit class ListExtensions[A](self: List[A]) {
    def intersperse(a : A): List[A] = self match {
      case Nil => Nil
      case x :: xs => x :: a :: xs.intersperse(a)
    }
  }
}
