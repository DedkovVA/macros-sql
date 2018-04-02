package com.github.dedkovva

/**
  * Created by dedkov-va on 04.03.18.
  */
object SqlExample extends App {
  println(Macros.select((t: TABLE_1) => SqlSelect(Columns(t.COLUMN_A), t.COLUMN_A == "col-a-v")))

/*  println(Macros.select((t: TABLE_1) => SqlSelect(
    where = t.COLUMN_A == "col-a-v" || t.COLUMN_B == "col-b-v0" ||
      t.COLUMN_B == "col-b-v1" && t.COLUMN_C > 0 && t.COLUMN_C < 10,
    columns = Columns(t.COLUMN_A, t.COLUMN_B))))*/

  println(Macros.select((t: TABLE_1) => SqlSelect(
    Columns(t.COLUMN_A, t.COLUMN_B),
    t.COLUMN_A == "col-a-v" || t.COLUMN_B == "col-b-v0" ||
      t.COLUMN_B == "col-b-v1" && t.COLUMN_C > 0 && t.COLUMN_C < 10)))

  println(Macros.select((t: TABLE_1) => SqlSelect(
    Columns(t.COLUMN_A, t.COLUMN_B),
    t.COLUMN_A == "col-a-v" || t.COLUMN_B == "col-b-v0" ||
      t.COLUMN_B == "col-b-v1" && t.COLUMN_C > 0 && t.COLUMN_C <= 10 ||
      t.COLUMN_A == "col-a-v1")))

  println(Macros.select((t: TABLE_1) => SqlSelect(
    Columns(t.COLUMN_A, t.COLUMN_B),
    t.COLUMN_A == "col-a-v" || t.COLUMN_B == "col-b-v0" ||
      t.COLUMN_B == "col-b-v1" && t.COLUMN_C > 0 && (t.COLUMN_C <= 10 ||
        t.COLUMN_A == "col-a-v1"))))

  println(Macros.select((t: TABLE_1) => SqlSelect(
    Columns(t.COLUMN_A, t.COLUMN_B),
    t.COLUMN_A == "col-a-v" && (t.COLUMN_B == "col-b-v0" ||
      t.COLUMN_B == "col-b-v1") || t.COLUMN_C > 0 && (t.COLUMN_C <= 10 ||
        t.COLUMN_A != "col-a-v1"))))

  println(Macros.select((t: TABLE_1) => SqlSelect(
    Columns(1, t.COLUMN_A, 2, t.COLUMN_B, t.COLUMN_C, 3, "x"),
    t.COLUMN_A == "col-a-v" && t.COLUMN_C == 1)))

  println(Macros.select((t: TABLE_1) => SqlSelect(
    Columns(1),
    1 == 1)))

  println(Macros.select((t: TABLE_1) => SqlSelect(
    Columns(1, 2, "x"),
    1 == 2)))
}
