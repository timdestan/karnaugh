  package karnaugh

  import fastparse.all._

  object TruthTableParser {
    val space = P(CharsWhile(" \r\n\t".contains(_))).?

    val lowercase = P(CharIn('a' to 'z'))
    val uppercase = P(CharIn('A' to 'Z'))
    val varName: Parser[String] = P(lowercase | uppercase).rep(min = 1).!
    val truthValue: Parser[TruthValue] =
      P("0".!.map(_ => F) |
        "1".!.map(_ => T) |
        "DC".!.map(_ => DC))

    val tableHdr: Parser[Seq[String]] = P(varName.rep(sep = " "))
    val tableRow: Parser[Seq[TruthValue]] = P(truthValue.rep(sep = " "))

    var rawParser: Parser[
      (Seq[String],        // Inputs
       Seq[String],        // Outputs
       Seq[String],        // Table Header
       Seq[Seq[TruthValue]]) // Table Rows
    ] = P(space ~
          "Inputs" ~/
          space ~
          varName.rep(sep = " ") ~
          space ~
          "Outputs" ~/
          space ~
          varName.rep(sep = " ") ~
          space ~
          "Table" ~/
          space ~
          tableHdr ~/
          space ~
          tableRow.rep(sep = "\n") ~
          space ~
          End)

  // Each output variable gets its own truth table in the result, keyed by the
  // output variable name.
  def parse(text: String): Either[String, Map[String, TruthTable]] =
    rawParser.parse(text).toEither.flatMap {
      case (inputs, outputs, hdr, rows) =>
        resolveTables(
          inputs.toSet,
          outputs.toSet,
          hdr.toVector,
          rows.map(_.toVector).toVector)
    }

  case class Var(name: String, isInput: Boolean, index: Int) {
    def :=(tv: TruthValue) = Assignment(name, tv)
  }

  def resolveTables(inputs: Set[String],
                    outputs: Set[String],
                    header: Vector[String],
                    rows: Vector[Vector[TruthValue]]):
                        Either[String, Map[String, TruthTable]] =
    resolveVars(inputs, outputs, header).flatMap {
      varTable => rows.map(resolveRow(_, varTable)).sequence
    }.map {
      maps => maps.flatMap(_.toSeq)
                  .groupBy(_._1)
                  .mapValues(v => TruthTable(v.map(_._2).toList))
    }

  def resolveVars(inputs: Set[String],
                  outputs: Set[String],
                  header: Vector[String]) : Either[String, Map[Int, Var]] = {
    header.zipWithIndex.map {
      case (name, i) =>
        if (inputs(name))
          Right(Var(name, true, i))
        else if (outputs(name))
          Right(Var(name, false, i))
        else
          Left(s"Undefined variable $name in header.")
    }.sequence.map {
      // Assumes no duplicates. Would be friendlier to detect and report the
      // error.
      vars => vars.groupBy(_.index).mapValues(_.head)
    }
  }

  // Resolve a row given a mapping of row indices to variables.
  // Example Inputs:
  // Vector(T, F, T)
  // Map(0 => Var("A", true, 0),
  //     1 => Var("B", true, 1),
  //     2 => Var("C", false, 2))
  // Example Output:
  // Right(Map("C" => Entry(List(A := T, B := F), T)))
  def resolveRow(row: Vector[TruthValue],
                 varMap: Map[Int, Var])
                     : Either[String, Map[String, TruthTable.Entry]] = {
    row.zipWithIndex.map {
      case (truthValue, i) => varMap.get(i) match {
        case None => Left("Row longer than headers")
        case Some(variable) => Right((variable, variable := truthValue))
      }
    }.sequence.map {
      vars => {
        val (inputs, outputs) = vars.partition(_._1.isInput)
        val inputAssignments = inputs.map(_._2).toList
        outputs.map(_._2).map {
          output => (output.name ->
                         TruthTable.Entry(inputAssignments, output.value))
        }.toMap
      }
    }
  }
}
