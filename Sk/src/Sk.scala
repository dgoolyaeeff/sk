package Sk

import scalatags.Text.all.*
import scalatags.Text.tags2.{section, title => ttitle, main => tmain}
import cask.*
import upickle.default.*
import scala.collection.mutable.Map
import scala.util.{Random,Try,Success,Failure}
import scala.annotation.tailrec

object Sk extends cask.MainRoutes:
  val jsonpath = os.pwd / "Sk" / "src" / "tasks.json"

  def mapInit(): Map[Int, Task] =
    Try(read[Map[Int, Task]](os.read(jsonpath))) match
      case Success(x) => x
      case Failure(_) => Map[Int, Task]()
    
  var tasks = mapInit()

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

  def tskViewAtMatrix(tsk: Task): Seq[Modifier] = Seq(
    div(cls:="taskAtMatrix")(
      a(href:="/edit-task.html")(
        h4(tsk.name)
      )
    )
  )

  //def filterTasks(clr: String): List[Task] = 
  //  var resL = List[Task]()
  //  tasks.ma
  //  resL

  //def matrixTasks(clr: String): Seq[Modifier] = 
  //  val localTasks = tasks.filter(
  //    ((_, ta) => if clr == ta.color then true else false))

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
    addTskForm: Seq[Modifier] = Seq.empty,
    ) =
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
              h2("Важно и срочно"),
              //matrixTasks("red")
              tasks
                .filter((_, x) => x.color == PriorColor.Red)
                .map[Seq[Modifier]]((_, x) => tskViewAtMatrix(x))
                .toSeq
            ),
            div(cls:="q q2")(
              h2("Важно но не срочно"),
              //matrixTasks("green")
              tasks
                .filter((_, x) => x.color == PriorColor.Green)
                .map[Seq[Modifier]]((_, x) => tskViewAtMatrix(x))
                .toSeq
            ),
            div(cls:="q q3")(
              h2("Не важно но срочно"),
              h3("ммм?"),
              //matrixTasks("brown")
              tasks
                .filter((_, x) => x.color == PriorColor.Brown)
                .map[Seq[Modifier]]((_, x) => tskViewAtMatrix(x))
                .toSeq
            ),
            div(cls:="q q4")(
              h2("Не важно и не срочно"),
              h3("чеееел?"),
              //matrixTasks("yellow")
              //filterTasks("yellow")
              tasks
                .filter((_, x) => x.color == PriorColor.Yellow)
                .map[Seq[Modifier]]((_, x) => tskViewAtMatrix(x))
                .toSeq
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
    addTskForm=initialTaskAdderH,
  )
  

  @cask.postForm("/addTaskForm")
  def formEndpoint(
    taskName: FormValue,
    taskDesc: FormValue,
    color: FormValue,
    ddln: FormValue
  ) = 

    def createTaskId(t: Map[Int, Task]): Int =
      @tailrec
      def helper(ts: Map[Int, Task], guess: Int): Int=
        if !(ts contains guess) then guess
        else helper(ts, guess + 1)
      helper(t, Random.nextInt(1000))

    val taskId = createTaskId(tasks)

    println(tasks)

    tasks = tasks + ((taskId, Task(
      taskName.value,
      taskDesc.value,
      PriorColor(color.value),
      ddln.value)))

    println(tasks)

    os.write.over(
      jsonpath, 
      write(tasks)
    )

    println(tasks)


    Response(
      "",
      statusCode = 302,
      headers = Seq("Location" -> "/")
    )

  initialize()

end Sk

enum PriorColor(val s: String):
  case Red extends PriorColor("red") 
  case Green extends PriorColor("green") 
  case Brown extends PriorColor("brown") 
  case Yellow extends PriorColor("yellow")

object PriorColor:
  def apply(s: String): PriorColor = s match
    case "red" => PriorColor.Red
    case "green" => PriorColor.Green
    case "brown" => PriorColor.Brown
    case "yellow" => PriorColor.Yellow

  implicit val rw: ReadWriter[PriorColor] = 
    upickle
      .default
      .readwriter[String]
      .bimap[PriorColor](
        clr => clr.s,
        str => PriorColor(str)
      )
end PriorColor

case class Task(
  name: String,
  desc: String,
  color: PriorColor,
  ddln: String
) derives ReadWriter

// object Task:
//   implicit val rw: ReadWriter[Task] =
//     upickle
//       .default
//       .readwriter[String]
//       .bimap[Task](
//         t => s"name:${t.name},desc:${t.desc},color:${t.color.s},ddln:${t.ddln}",
        
//       )
// end Task