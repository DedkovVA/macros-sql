package com.github.dedkovva

import scala.language.experimental.macros
import scala.reflect.api.Trees
import scala.reflect.macros.blackbox

/**
  * Created by dedkov-va on 08.03.18.
  */
object Macros {
  def select(f: _ => _): String = macro selectImpl

  def selectImpl(c: blackbox.Context)(f: c.Tree): c.Expr[String] = {
    import c.universe._

//    Apply(
//      Select(
//        This(TypeName("SqlExample")),
//        TermName("select")
//      ),
//      List(
//        Function(
//          List(ValDef(Modifiers(PARAM), TermName("t"), TypeTree().setOriginal(Ident(com.github.dedkovva.TABLE_1)), EmptyTree)),
//          Apply(
//            TypeApply(
//              Select(Select(Ident(scala), scala.Tuple2), TermName("apply")),
//              List(TypeTree(), TypeTree())
//            ),
//            List(Select(Ident(TermName("t")), TermName("COLUMN_A")), Select(Ident(TermName("t")), TermName("COLUMN_B")))
//          )
//        )
//      )
//    )

    //SqlExample.this.select(((t: com.github.dedkovva.TABLE_1) => scala.Tuple2.apply[String, String](t.COLUMN_A, t.COLUMN_B)))
//    val Function(Function(List(ValDef(_, name, _, _)), _)) = f

//    c.Expr(q"$name")

//    val Apply(Select(_, TermName("select")), List(Function(List(ValDef(_, _, tpt, _)), _))) = f
//    val Ident(TermName(tableName)) = tpt

//    Function(
//      List(ValDef(Modifiers(PARAM), TermName("t"), TypeTree().setOriginal(Ident(com.github.dedkovva.TABLE_1)), EmptyTree)),
//      Apply(
//        TypeApply(Select(Select(Ident(scala), scala.Tuple2), TermName("apply")), List(TypeTree(), TypeTree())),
//        List(Select(Ident(TermName("t")), TermName("COLUMN_A")), Select(Ident(TermName("t")), TermName("COLUMN_B")))
//      )
//    )

//    Function(
//      List(ValDef(Modifiers(PARAM), TermName("t"), TypeTree().setOriginal(Ident(com.github.dedkovva.TABLE_1)), EmptyTree)),
//      Select(Ident(TermName("t")), TermName("COLUMN_A"))
//    )

    val Function(List(ValDef(_, _, tpt, EmptyTree)), columnsSelector) = f

    val tableFullName = tpt.tpe.toString
    val dotLastIndex = tableFullName.lastIndexOf('.')
    val tableName = tableFullName.substring(dotLastIndex + 1)

    def sqlTemplate(columns: String) = s"SELECT $columns FROM $tableName"

    val sql = columnsSelector match {
      case Select(Ident(TermName(_)), TermName(columnName)) =>
        sqlTemplate(columnName)
      case Apply(_, values) =>
        val columns = values.map(tree => {
          val Select(Ident(TermName(_)), TermName(columnName)) = tree
          columnName
        })

        sqlTemplate(columns.mkString(", "))
    }

//    val Function(List(ValDef(_, _, tpt, EmptyTree)), Select(Ident(TermName(_), TermName(columnName)))) = f
//
//    val Function(List(ValDef(_, _, tpt, EmptyTree)), Apply(_, values)) = f
//    val tableFullName = tpt.tpe.toString
//    val dotLastIndex = tableFullName.lastIndexOf('.')
//    val tableName = tableFullName.substring(dotLastIndex + 1)
//
//    val columns = values.map(tree => {
//      val Select(Ident(TermName(_)), TermName(columnName)) = tree
//      columnName
//    })
//
//    val sql = s"SELECT ${columns.mkString(", ")} FROM $tableName"

//    c.Expr(q"${showRaw(tpt)}")
//    c.Expr(q"${showRaw(f)}")
//    c.Expr(q"$tableName")
    c.Expr(q"$sql")
  }
}
