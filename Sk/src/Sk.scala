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
  
  val defaultAddTskBtn = Seq(
    a(href:="/add-task.html")("новый таск?"))

  val addingTskBtn = Seq(
    a(href:="/")("передумал?"))

  def base(
    blr: Boolean = false, 
    addTasForm: Seq[Modifier] = Nil,
    addTasBtn: Seq[Modifier] = defaultAddTskBtn,
    addTskForm: Seq[Modifier] = Nil) =
    html(lang:="ru")(
      head(
        meta(charset:="UTF-8"),
        meta(httpEquiv:="X-UA-Compatible", content:="IE=edge"),
        meta(name:="viewport", content:="width=device-width, initial-scale=1.0"),
        meta(name:="description", content:="ебейший таск трекер"),
        ttitle("sk"),
        link(rel:="stylesheet", href:="/s.css")),
      body(
        header(cls:="hd")(
          button(cls:="add-task-btn")(
            addTasBtn)),
        tmain(cls:= { if blr then "blur__page_task-add" else "" })(
          div(cls:="mainmatrix")(
            div(cls:="q q1")(
              h2("Важно и срочно")),
            div(cls:="q q2")(
              h2("Важно но не срочно")),
            div(cls:="q q3")(
              h2("Не важно но срочно"),
              h3("ммм?")),
            div(cls:="q q4")(
              h2("Не важно и не срочно"),
              h3("чеееел?")
            )
          )
        ),
        section(addTskForm)
      )
    )

  @cask.get("/")
  def h() = base()

  @cask.get("/add-task.html")
  def hAddTask() = base(
    blr=true,
    addTasBtn=addingTskBtn,
    addTskForm=Seq(
      div(cls:="task-form")(
        div(cls:="form-header")(
          h2("Добавим таск")
        ),
        form(id:="taskForm")(
          div(cls:="form-group")(
            input(
              `type`:="text", 
              cls:="form-control",
              id:="task-title",
              placeholder:="Название таска",
              required
              )
          ),
          div(cls:="form-group")(
            textarea(
              cls:="form-control", 
              id:="taskDesc",
              placeholder:="Описание"
            )
          ),
          div(
            cls:="form-group"
          )(
            label(
              `for`:="prior"
            )("Категория:"),
            select(
              cls:="form-control",
              id:="prior"
            )(
              option(value:="red")("красный"),
              option(value:="green")("зелёный"),
              option(value:="brown")("коричневый"),
              option(value:="yellow")("жёлтый")
            )
          ),
          div(
            cls:="form-group"
          )(
            label(`for`:="taskDeadline")("Дедлайн:"),
            input(
              `type`:="date",
              cls:="form-control",
              id:="task-deadline"
            )
          ),
          div(
            cls:="taskRoadmap"
          )(
            div(cls:="taskRoadmapHeader")(
              div(cls:="taskRoadmapTitle")(
                "Дорожная карта таска:"
              ),
              button(
                `type`:="button",
                cls:="addPodtaskBtn",
                id:="addPodtaskBtnId"
              )("Добавим подтаск?")
            ),
            div(
              cls:="podtaskContainer",
              id:="podtaskContainer"
            )(
              // сюда будут добавляться подтаски!
            )
          ),
          button(
            `type`:="submit",
            cls:="submit-btn"
          )("Добавить таск!")
        ) 
      )
    )
  )
  initialize()