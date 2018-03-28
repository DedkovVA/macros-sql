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

    val Function(
      List(ValDef(_, _, tpt, EmptyTree)),
      Apply(
        Select(Ident(TermName("SqlSelect")), TermName("apply")),
        List(
          Apply(
            Select(Ident(TermName("Columns")), TermName("apply")),
            columnsSelector
          ),
          whereSelector
        )
      )
    ) = f

    val tableFullName = tpt.tpe.toString
    val dotLastIndex = tableFullName.lastIndexOf('.')
    val tableName = tableFullName.substring(dotLastIndex + 1)

    def sqlSelectFromTemplate(columns: String) = s"SELECT $columns FROM $tableName"

    def valueToString(value: Any): String = {
      if (value.isInstanceOf[String]) s"'$value'"
      else value.toString
    }

    val columns = columnsSelector.map {
      case Select(Ident(TermName(_)), TermName(columnName)) => columnName
      case Literal(Constant(value)) => valueToString(value)
    }

    def whereSelectorToString(tree: c.universe.Tree): String = {
      val Apply(select, list) = tree
      (select, list) match {
        case (
          Select(Select(Ident(TermName(_)), TermName(col)), TermName(term)),
          List(Literal(Constant(value)))
        ) => predicateToString(tree)
        case (Select(newTree, TermName("$bar$bar")), List(treeInList)) =>
          whereSelectorToString(newTree) + " OR " + whereSelectorToString(treeInList)
        case (Select(newTree, TermName("$amp$amp")), List(treeInList@Apply(Select(_), List(Apply(_))))) =>
          whereSelectorToString(newTree) + " AND (" + whereSelectorToString(treeInList) + ")"
        case (Select(newTree, TermName("$amp$amp")), List(treeInList)) =>
          whereSelectorToString(newTree) + " AND " + whereSelectorToString(treeInList)
      }
    }

    def predicateToString(tree: c.universe.Tree): String = {
      val Apply(Select(Select(Ident(TermName(_)), TermName(col)), TermName(term)), List(Literal(Constant(value)))) = tree
      val terms = term.split("\\$").filter(_.nonEmpty).toList
      val sign = termsToString(terms)
      val valueStr = valueToString(value)
      s"$col $sign $valueStr"
    }

    def termsToString(terms: List[String]): String = {
      terms match {
        case "eq" :: "eq" :: Nil => "="
        case "eq" :: m => "=" + termsToString(m)
        case "greater" :: m => ">" + termsToString(m)
        case "less" :: m => "<" + termsToString(m)
        case "bang" :: m => "!" + termsToString(m)
        case _ => ""
      }
    }

    val selectFrom = sqlSelectFromTemplate(columns.mkString(", "))
    val where = "WHERE " + whereSelectorToString(whereSelector)
    val sql = s"$selectFrom $where"

/*    Function(
      List(ValDef(Modifiers(PARAM), TermName("t"), TypeTree().setOriginal(Ident(com.github.dedkovva.TABLE_1)), EmptyTree)),
      Block(
        List(
          ValDef(
            Modifiers(ARTIFACT),
            TermName("x$1"),
            TypeTree(),
            Apply(
              Select(
                Apply(
                  Select(
                    Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_A")), TermName("$eq$eq")), List(Literal(Constant("col-a-v")))),
                    TermName("$bar$bar")
                  ),
                  List(Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_B")), TermName("$eq$eq")), List(Literal(Constant("col-b-v0")))))
                ),
                TermName("$bar$bar")
              ),
              List(
                Apply(
                  Select(
                    Apply(
                      Select(
                        Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_B")), TermName("$eq$eq")), List(Literal(Constant("col-b-v1")))),
                        TermName("$amp$amp")
                      ),
                      List(Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_C")), TermName("$greater")), List(Literal(Constant(0)))))
                    ),
                    TermName("$amp$amp")
                  ),
                  List(Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_C")), TermName("$less")), List(Literal(Constant(10)))))
                )
              )
            )
          ),
          ValDef(
            Modifiers(ARTIFACT),
            TermName("x$2"),
            TypeTree(),
            Apply(
              Select(Ident(com.github.dedkovva.Columns), TermName("apply")),
              List(Select(Ident(TermName("t")), TermName("COLUMN_A")), Select(Ident(TermName("t")), TermName("COLUMN_B")))
            )
          )
        ),
        Apply(Select(Ident(com.github.dedkovva.SqlSelect), TermName("apply")), List(Ident(TermName("x$2")), Ident(TermName("x$1"))))
      )
    )*/

/*  Function(
      List(ValDef(Modifiers(PARAM), TermName("t"), TypeTree().setOriginal(Ident(com.github.dedkovva.TABLE_1)), EmptyTree)),
      Apply(
        Select(Ident(com.github.dedkovva.SqlSelect), TermName("apply")),
        List(
          Apply(
            Select(Ident(com.github.dedkovva.Columns), TermName("apply")),
            List(Select(Ident(TermName("t")), TermName("COLUMN_A")), Select(Ident(TermName("t")), TermName("COLUMN_B")))
          ),
          Apply(
            Select(
              Apply(
                Select(
                  Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_A")), TermName("$eq$eq")), List(Literal(Constant("col-a-v")))),
                  TermName("$bar$bar")
                ),
                List(Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_B")), TermName("$eq$eq")), List(Literal(Constant("col-b-v0")))))
              ),
              TermName("$bar$bar")
            ),
            List(
              Apply(
                Select(
                  Apply(
                    Select(
                      Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_B")), TermName("$eq$eq")), List(Literal(Constant("col-b-v1")))),
                      TermName("$amp$amp")
                    ),
                    List(Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_C")), TermName("$greater")), List(Literal(Constant(0)))))
                  ),
                  TermName("$amp$amp")
                ),
                List(Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_C")), TermName("$less")), List(Literal(Constant(10)))))
              )
            )
          )
        )
      )
    )*/

/*    Function(
      List(ValDef(Modifiers(PARAM), TermName("t"), TypeTree().setOriginal(Ident(com.github.dedkovva.TABLE_1)), EmptyTree)),
      Apply(
        Select(Ident(com.github.dedkovva.SqlSelect), TermName("apply")),
        List(
          Apply(
            Select(Ident(com.github.dedkovva.Columns), TermName("apply")),
            List(Select(Ident(TermName("t")), TermName("COLUMN_A")), Select(Ident(TermName("t")), TermName("COLUMN_B")))
          ),
          Apply(
            Select(
              Apply(
                Select(
                  Apply(
                    Select(
                      Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_A")), TermName("$eq$eq")), List(Literal(Constant("col-a-v")))),
                      TermName("$bar$bar")
                    ),
                    List(Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_B")), TermName("$eq$eq")), List(Literal(Constant("col-b-v0")))))
                  ),
                  TermName("$bar$bar")
                ),
                List(
                  Apply(
                    Select(
                      Apply(
                        Select(
                          Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_B")), TermName("$eq$eq")), List(Literal(Constant("col-b-v1")))),
                          TermName("$amp$amp")
                        ),
                        List(Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_C")), TermName("$greater")), List(Literal(Constant(0)))))
                      ),
                      TermName("$amp$amp")
                    ),
                    List(Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_C")), TermName("$less$eq")), List(Literal(Constant(10)))))
                  )
                )
              ),
              TermName("$bar$bar")
            ),
            List(Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_A")), TermName("$eq$eq")), List(Literal(Constant("col-a-v1")))))
          )
        )
      )
    )*/

/*    Function(
      List(ValDef(Modifiers(PARAM), TermName("t"), TypeTree().setOriginal(Ident(com.github.dedkovva.TABLE_1)), EmptyTree)),
      Apply(
        Select(Ident(com.github.dedkovva.SqlSelect), TermName("apply")),
        List(
          Apply(
            Select(Ident(com.github.dedkovva.Columns), TermName("apply")),
            List(Select(Ident(TermName("t")), TermName("COLUMN_A")), Select(Ident(TermName("t")), TermName("COLUMN_B")))
          ),
          Apply(
            Select(
              Apply(
                Select(Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_A")), TermName("$eq$eq")), List(Literal(Constant("col-a-v")))), TermName("$bar$bar")),
                List(Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_B")), TermName("$eq$eq")), List(Literal(Constant("col-b-v0")))))
              ),
              TermName("$bar$bar")
            ),
            List(
              Apply(
                Select(
                  Apply(
                    Select(Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_B")), TermName("$eq$eq")), List(Literal(Constant("col-b-v1")))), TermName("$amp$amp")),
                    List(Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_C")), TermName("$greater")), List(Literal(Constant(0)))))
                  ),
                  TermName("$amp$amp")
                ),
                List(
                  Apply(
                    Select(Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_C")), TermName("$less$eq")), List(Literal(Constant(10)))), TermName("$bar$bar")),
                    List(Apply(Select(Select(Ident(TermName("t")), TermName("COLUMN_A")), TermName("$eq$eq")), List(Literal(Constant("col-a-v1")))))
                  )
                )
              )
            )
          )
        )
      )
    )*/

/*    Function(
      List(ValDef(Modifiers(PARAM), TermName("t"), TypeTree().setOriginal(Ident(com.github.dedkovva.TABLE_1)), EmptyTree)),
      Apply(
        Select(Ident(com.github.dedkovva.SqlSelect), TermName("apply")),
        List(
          Apply(Select(Ident(com.github.dedkovva.Columns), TermName("apply")), List(Literal(Constant(1)))),
          Literal(Constant(true))
        )
      )
    )*/


    //      c.Expr(q"${showRaw(tpt)}")
//    c.Expr(q"${showRaw(f)}")
//    c.Expr(q"$tableName")
    c.Expr(q"$sql")
  }
}
