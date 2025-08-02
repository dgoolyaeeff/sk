package Sk

import scalatags.Text.all.*
import scalatags.Text.tags2.{section, title => ttitle, main => tmain}
import cask.*

object Sk extends cask.MainRoutes:
  @cask.get("/s.css")
  def s() = 
    val path =
      os.root / "home" / "dimany" / "th" / "all_src" / "mid" / "sk" / "Sk" / "src" / "s.css"
    val content = os.read(path)
    content
  
  @cask.get("/")
  def h() =
    html(lang:="ru")(
      head(
        meta(charset:="UTF-8"),
        meta(httpEquiv:="X-UA-Compatible", content:="IE=edge"),
        meta(name:="viewport", content:="width=device-width, initial-scale=1.0"),
        meta(name:="description", content:="ебейший таск трекер"),
        ttitle("sk"),
        link(rel:="stylesheet", href:="/s.css"),
      ),
      body(
        header(cls:="hd")(
          button(cls:="add-task-btn")("новый таск?")
        ),
        tmain(
          div(cls:="mainmatrix")(
            div(cls:="q q1")(
              h2("Важно и срочно")
            ),
            div(cls:="q q2")(
              h2("Важно но не срочно")
            ),
            div(cls:="q q3")(
              h2("Не важно но срочно"),
              h3("ммм?")
            ),
            div(cls:="q q4")(
              h2("Не важно и не срочно"),
              h3("чеееел?")
            )
          )
        )
      )
    )
  initialize()