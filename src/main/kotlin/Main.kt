import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.loadFont
import org.openrndr.extra.color.presets.LIGHT_YELLOW
import org.openrndr.math.IntVector2
import org.openrndr.math.Vector2

fun drawCells(drawer: Drawer, width: Int, height: Int) {
    drawer.lineSegment(Vector2(width / 3.0, 0.0), Vector2(width / 3.0, height.toDouble()))
    drawer.lineSegment(Vector2(width / 3.0 * 2.0, 0.0), Vector2(width / 3.0 * 2.0, height.toDouble()))
    drawer.lineSegment(Vector2(0.0, height / 3.0), Vector2(width.toDouble(), height / 3.0))
    drawer.lineSegment(Vector2(0.0, height / 3.0 * 2.0), Vector2(width.toDouble(), height / 3.0 * 2.0))
}

fun Drawer.cross(position: Vector2, size: Double) {
    lineSegment(position, position + Vector2(size, size))
    lineSegment(position + Vector2(size, 0.0), position + Vector2(0.0, size))
}

fun drawCrossesAndNulls(drawer: Drawer, width: Int, height: Int, gameField: List<Cell>) {
    gameField.forEachIndexed { i, cell ->
        val x = i % 3
        val y = i / 3
        if (cell == Cell.CROSS)
            drawer.cross(
                Vector2(width.toDouble() / 3.0 * x.toDouble(), height.toDouble() / 3.0 * y.toDouble()),
                width / 3.0
            )
        if (cell == Cell.NULL)
            drawer.circle(
                width / 3.0 * x.toDouble() + width / 6.0,
                height / 3.0 * y.toDouble() + height / 6.0,
                width / 6.0
            )
    }
}

/**
 * Функция, возвращающая победителя
 *
 * @return true - победили крестики, false - победили нолики, null - ни одной победной комбинации на данный момент, возможно, ничья
 */
fun checkWin(gameField: List<Cell>): Boolean? {
    if (gameField[0] == gameField[1] && gameField[1] == gameField[2] && gameField[0] != Cell.EMPTY) return gameField[0] == Cell.CROSS
    if (gameField[3] == gameField[4] && gameField[4] == gameField[5] && gameField[3] != Cell.EMPTY) return gameField[3] == Cell.CROSS
    if (gameField[6] == gameField[7] && gameField[7] == gameField[8] && gameField[6] != Cell.EMPTY) return gameField[6] == Cell.CROSS
    if (gameField[0] == gameField[3] && gameField[3] == gameField[6] && gameField[0] != Cell.EMPTY) return gameField[0] == Cell.CROSS
    if (gameField[1] == gameField[4] && gameField[4] == gameField[7] && gameField[1] != Cell.EMPTY) return gameField[1] == Cell.CROSS
    if (gameField[2] == gameField[5] && gameField[5] == gameField[8] && gameField[2] != Cell.EMPTY) return gameField[2] == Cell.CROSS
    if (gameField[0] == gameField[4] && gameField[4] == gameField[8] && gameField[0] != Cell.EMPTY) return gameField[0] == Cell.CROSS
    if (gameField[2] == gameField[4] && gameField[4] == gameField[6] && gameField[2] != Cell.EMPTY) return gameField[2] == Cell.CROSS
    return null
}


fun main() {
    application {
        configure {
            this.height = 350
            this.width = 350
            title = "TicTacToe"
        }
        program {
            val font = loadFont("data/fonts/default.otf", 24.0)
            // игровое поле, по-умолчанию состоит из пустых клеток
            var gameField = mutableListOf(
                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
                Cell.EMPTY, Cell.EMPTY, Cell.EMPTY,
            )

            val gameFieldDefault = gameField.toList()

            // ходят ли сейчас крестики или нет true - крестики, false - нолики
            var stepOfCrosses = true

            mouse.buttonDown.listen {
                val relPosition = it.position / width.toDouble() * 3.0
                val intRelPosition = IntVector2(relPosition.x.toInt(), relPosition.y.toInt())
                if (intRelPosition.x in 0..2 && intRelPosition.y in 0..2)
                    if (gameField[intRelPosition.y * 3 + intRelPosition.x] == Cell.EMPTY) {
                        if (stepOfCrosses) {
                            this.application.windowTitle = "Ход ноликов"
                            gameField[intRelPosition.y * 3 + intRelPosition.x] = Cell.CROSS
                        } else {
                            this.application.windowTitle = "Ход крестиков"
                            gameField[intRelPosition.y * 3 + intRelPosition.x] = Cell.NULL
                        }
                        stepOfCrosses = !stepOfCrosses
                    }
            }
            var s = -5.0
            var winner: Boolean? = null
            extend {
                drawer.clear(ColorRGBa.LIGHT_YELLOW)
                drawer.fontMap = font
                drawer.stroke = ColorRGBa.BLACK
                if (seconds - s <= 1.0) {
                    drawer.fill = ColorRGBa.BLACK
                    if (winner == true)
                        drawer.text("Крестики победили", 10.0, 50.0)
                    if (winner == false)
                        drawer.text("Нолики победили", 10.0, 50.0)
                    if (winner == null)
                        drawer.text("Ничья", 10.0, 50.0)
                } else {
                    drawer.fill = null
                    drawCells(drawer, width, height)
                    drawCrossesAndNulls(drawer, width, height, gameField)
                    winner = checkWin(gameField)
                    if (winner != null) {
                        s = seconds
                        gameField = gameFieldDefault.toMutableList()
                        this.application.windowTitle = if (winner == true) "Крестики победили" else "Нолики победили"
                        stepOfCrosses = true
                    }else if(Cell.EMPTY !in gameField){
                        s = seconds
                        gameField = gameFieldDefault.toMutableList()
                        this.application.windowTitle = "Ничья"
                        stepOfCrosses = true
                    }
                }

            }
        }
    }
}