package com.github.dedkovva

/**
  * Created by dedkov-va on 04.03.18.
  */
case class SqlSelect(columns: Columns, where: Boolean)
case class Columns(columns: Any*)