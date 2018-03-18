package com.github.dedkovva

/**
  * Created by dedkov-va on 04.03.18.
  */
object SqlExample extends App {
//  def sqlSelect(f: _ => _) = select(f)

//  println(Macros.select((t: TABLE_1) => SqlSelect(Columns(t.COLUMN_A), t.COLUMN_A == "col-a-v")))
//  println(Macros.select((t: TABLE_1) => SqlSelect(
//    Columns(t.COLUMN_A, t.COLUMN_B),
//    t.COLUMN_A == "col-a-v" || t.COLUMN_B == "col-b-v")))
//  println(Macros.select((t: TABLE_1) => SqlSelect(
//    Columns(t.COLUMN_A, t.COLUMN_B, t.COLUMN_C),
//    t.COLUMN_A == "col-a-v" && t.COLUMN_C == 1)))

  println(Macros.select((t: TABLE_1) => t.COLUMN_A))
  println(Macros.select((t: TABLE_1) => (t.COLUMN_A, t.COLUMN_B)))
  println(Macros.select((t: TABLE_1) => (t.COLUMN_A, t.COLUMN_B, t.COLUMN_C)))
//  println(Macros.select((t: TABLE_1) => (t.COLUMN_A, t.COLUMN_B, t.COLUMN_C, "1")))
}
