package chess

import kotlin.math.absoluteValue

const val LOGICAL_BOARD_SIZE = 8

object LogicalBoard {
    val board: MutableList<MutableList<Field>> = MutableList(LOGICAL_BOARD_SIZE) {
        MutableList(LOGICAL_BOARD_SIZE) { Field() }
    }

    var isCapturing: Boolean = false

    init {
        for (i in 0 until LOGICAL_BOARD_SIZE) {
            board[1][i] = Field(Team.BLACK)
            board[LOGICAL_BOARD_SIZE - 2][i] = Field(Team.WHITE)
        }
    }

    fun performMove(team: Team, input: String): Boolean {
        isCapturing = false
        val move = try {
            Move(input)
        } catch (e: Exception) {
            return invalidInput()
        }

        if (!isInputValid(team, move)) return false

        resetField(move.startNotation)
        resetField(move.endNotation, team)
        checkEnPassant(team, move)
        resetEnPassant(team, move)
        return true
    }

    private fun resetField(field: String, team: Team? = null) {
        val file = getFileIndex(field[0])
        val rank = getRankIndex(field[1])
        board[rank][file] = Field(team)
    }

    private fun getRankIndex(c: Char): Int {
        return when (c) {
            '8'  -> 0
            '7'  -> 1
            '6'  -> 2
            '5'  -> 3
            '4'  -> 4
            '3'  -> 5
            '2'  -> 6
            '1'  -> 7
            else -> -1
        }
    }

    private fun getFileIndex(c: Char): Int {
        return when (c) {
            'a'  -> 0
            'b'  -> 1
            'c'  -> 2
            'd'  -> 3
            'e'  -> 4
            'f'  -> 5
            'g'  -> 6
            'h'  -> 7
            else -> -1
        }
    }

    private fun checkEnPassant(team: Team, move: Move) {
        if (isCapturing && EnPassant.isPossible) {
            if (EnPassant.file() == move.endFile && EnPassant.rank() - move.endRank == team.increment())
                resetField(EnPassant.field)
        }
    }

    private fun resetEnPassant(team: Team, move: Move) {
        if (move.startRank == team.startRank() && move.endRank == team.maxFirstMoveRank())
            EnPassant.enable(move.endNotation, team)
        else EnPassant.disable()

    }

    private fun isInputValid(team: Team, move: Move): Boolean {

        // check that the move starts with the right team
        if (getTeamFromField(move.startNotation) != team) {
            println("No ${team.name.lowercase()} pawn at ${move.startNotation}")
            return false
        }


        // check that move is in the right direction
        when (team) {
            Team.BLACK -> if (move.startRank <= move.endRank) return invalidInput()
            Team.WHITE -> if (move.startRank >= move.endRank) return invalidInput()
        }


        // check if it is capturing
        val isEnPassant = (EnPassant.isPossible // check that enPassant is possible
                && EnPassant.teamOnField == team.opposite() // for this team
                && EnPassant.field == move.nextField(team.opposite()))// and that ahead of the pawn stays enPassant pawn

        if ((move.isFilesNeighbour())
            && (move.startRank - move.endRank == team.increment())  // it is one rank move
            && ((getTeamFromField(move.endNotation) == team.opposite()) || isEnPassant) // it is straight capturing or enPassant
        ) {
            isCapturing = true
            return true // no more checks needed
        }


        // check that the move is on the same file
        if (move.startFile != move.endFile) return invalidInput()


        // check correct length of the move
        if (move.startRank == team.startRank()) { // it's possible to do a long move
            if ((move.startRank - move.endRank).absoluteValue > 2) return invalidInput()
        } else {
            if ((move.startRank - move.endRank).absoluteValue > 1) return invalidInput()
        }


        // check that nothing prevents from moving
        if (getTeamFromField(move.endNotation) != null) return invalidInput()
        if (move.isLongMove()) {
            val field = "${move.startFile}${move.getMiddleRank()}"
            if (getTeamFromField(field) != null) return invalidInput()
        }


        return true
    }

    private fun getTeamFromField(field: String): Team? {
        val file = getFileIndex(field[0])
        val rank = getRankIndex(field[1])
        return board[rank][file].team
    }

    private fun invalidInput(): Boolean {
        println("Invalid Input")
        return false
    }

    data class Field(val team: Team? = null)

    class Move(fullNotation: String) {
        init {
            // validate input format
            val regex = "[a-h][1-8][a-h][1-8]".toRegex()
            if (!regex.matches(fullNotation)) throw IllegalArgumentException()
        }

        val startFile = fullNotation[0]
        val endFile = fullNotation[2]
        val startRank = fullNotation[1].digitToInt()
        val endRank = fullNotation[3].digitToInt()

        val startNotation = fullNotation.substring(0, 2)
        val endNotation = fullNotation.substring(2, 4)

        /*
        Throws IllegalStateException in case of call on 1-field move
         */
        fun getMiddleRank(): Int {
            if (!isLongMove()) throw IllegalStateException()
            return if (startRank > endRank) startRank - 1
            else startRank + 1
        }

        fun isLongMove(): Boolean {
            return startRank - endRank == 2 || endRank - startRank == 2
        }

        fun isFilesNeighbour(): Boolean {
            return startFile - endFile == 1 || startFile - endFile == -1
        }

        /*
        Returns next field from the end of the move for concrete team
         */
        fun nextField(team: Team): String {
            return endFile + "$endRank + ${team.increment()}"
        }
    }

    object EnPassant {
        var isPossible = false
        var field: String = ""
        lateinit var teamOnField: Team

        fun file(): Char {
            return if (isPossible) field[0]
            else throw IllegalStateException()
        }

        fun rank(): Int {
            return if (isPossible) field[1].digitToInt()
            else throw IllegalStateException()
        }

        fun enable(endNotation: String, team: Team) {
            isPossible = true
            field = endNotation
            teamOnField = team
        }

        fun disable() {
            isPossible = false
            field = ""
        }
    }
}

