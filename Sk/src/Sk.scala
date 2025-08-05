package Sk

import scalatags.Text.all.*
import scalatags.Text.tags2.{section, title => ttitle, main => tmain}
import cask.*
import upickle.default.*
import scala.collection.mutable.Map
import scala.util.Random

object Sk extends cask.MainRoutes:
  var tasks = Map[Int, Task]()
  val jsonpath = os.pwd / "Sk" / "src" / "tasks.json"

  def init() =
    val maindir = os.pwd / "Sk" / "src" 
    if (os.list(maindir) contains "tasks.json")
      then tasks = read[Map[Int, Task]](os.read(jsonpath))
      else ()
  Sk.init()


  @cask.get("/s.css")
  def s() = 
    val path =
      os.pwd / "Sk" / "src" / "s.css"
    val content = os.read(path)
    content
  
  val defaultAddTskBtn = Seq(
    a(href:="/add-task.html")("новый таск?"))

  val addingTskBtn = Seq(
    a(href:="/")("передумал?"))

  val initialTaskAdderH = Seq(
    div(cls:="task-form-overlay")(
      div(cls:="task-form")(
        div(cls:="form-header")(
          h2("Добавим таск")
        ),
        form(
          id:="taskForm",
          method:="post",
          action:=
            "http://localhost:8080/addTaskForm"
        )(
          div(cls:="form-group")(
            input(
              `type`:="text", 
              cls:="form-control",
              id:="task-title",
              name:="taskName",
              placeholder:="Название таска",
              required
              )
          ),
          div(cls:="form-group")(
            textarea(
              cls:="form-control", 
              id:="taskDesc",
              name:="taskDesc",
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
              id:="prior",
              name:="color"
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
              id:="task-deadline",
              name:="ddln"
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

  def base(
    blr: Boolean = false, 
    addTasForm: Seq[Modifier] = Seq.empty,
    addTasBtn: Seq[Modifier] = defaultAddTskBtn,
    addTskForm: Seq[Modifier] = Seq.empty) =
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
    addTskForm=initialTaskAdderH
  )

  @cask.postForm("/addTaskForm")
  def formEndpoint(
    taskName: FormValue,
    taskDesc: FormValue,
    color: FormValue,
    ddln: FormValue
  ) = 

    var taskId = Random.nextInt(1000)
    if (tasks contains taskId) then {
      while(tasks contains taskId) do taskId = Random.nextInt(1000)
    } else ()

    tasks = tasks + ((taskId, Task(
      taskName.value,
      taskDesc.value,
      color.value,
      ddln.value)))

    os.write.over(
      jsonpath, 
      write(tasks)
    )


    Response(
      "",
      statusCode = 302,
      headers = Seq("Location" -> "/")
    )

  initialize()

  // @cask.beforeShutdown
  // def bS() = println("bb")
end Sk

case class Task(
  name: String,
  desc: String,
  color: String,
  ddln: String
) derives ReadWriter
