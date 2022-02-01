package chess

/*
The object is responsible for drawing the chess board
For actions with pawns use LogicalBoard
 */
object PhysicalBoard {
    private const val height = 17 // from 0
    private const val width = 34 // from 0
    private val desk = MutableList(height + 1) { MutableList(width + 1) { ' ' } }

    init {
        initFiles()
        initRanks()
        initField()
    }

    // column's names
    private fun initFiles() {
        var c = 'a'
        for (i in 0..width) {
            if ((i + 1) % 4 == 0) {
                desk[height][i + 1] = c
                c++
            }
        }
    }

    // raw's names
    private fun initRanks() {
        var c = '8'
        for (i in 0 until height - 1) {
            if (i % 2 == 1) {
                desk[i][0] = c
                c--
            }
        }
    }

    private fun initField() {
        for (i in 0 until height) {
            for (j in 2..width) {
                if (i % 2 == 0) {   // rows +---+
                    if (j % 4 == 2)
                        desk[i][j] = '+' // cols 2, 6, 10 ...
                    else
                        desk[i][j] = '-'
                } else { // rows |   |
                    if (j % 4 == 2)
                        desk[i][j] = '|' // cols: 2, 6, 10 ...
                }
            }
        }
    }

    fun printBoard() {
        syncPawnsPositions()
        for (b in desk) {
            println(b.joinToString(""))
        }
    }

    /*
    the method reads pawns position from the LogicalBoard and redraw pawns on the PhysicalBoard
     */
    private fun syncPawnsPositions() {
        val board = LogicalBoard.board
        for (rank in 0 until LOGICAL_BOARD_SIZE) {
            for (file in 0 until LOGICAL_BOARD_SIZE) {
                when (board[rank][file].team) {
                    Team.BLACK -> desk[rank * 2 + 1][file * 4 + 4] = 'B'
                    Team.WHITE -> desk[rank * 2 + 1][file * 4 + 4] = 'W'
                    null       -> desk[rank * 2 + 1][file * 4 + 4] = ' '
                }
            }
        }
    }
}