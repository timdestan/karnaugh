package object karnaugh {
  implicit class StringExtensions(self: String) {
    def :=(value: TruthVal) = Assignment(self, value)
  }
}
