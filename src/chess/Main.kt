package chess

import java.util.*

fun main() {
    println("Pawns-Only Chess")
    Game.run()
}



object Game {

    lateinit var firstPlayer: String
    lateinit var secondPlayer: String
    private val scanner = Scanner(System.`in`)

    fun run() {
        initPlayers()
        PhysicalBoard.printBoard()
        runPlayCycle()
    }

    private fun initPlayers() {
        println("First Player's name:")
        firstPlayer = scanner.next()
        println("Second Player's name:")
        secondPlayer = scanner.next()
    }



    private fun runPlayCycle() {
        var currentTeam = Team.WHITE  // WHITE team starts

        while (true) {
            when (currentTeam) {
                Team.WHITE -> println("$firstPlayer's turn:")
                Team.BLACK -> println("$secondPlayer's turn:")
            }

            val input = scanner.next()
            if (input == "exit") break

            val isMoveCorrect = LogicalBoard.performMove(currentTeam, input)
            if (!isMoveCorrect) continue  // the cycle refreshes in case of a wrong move

            // print updated board after the move
            PhysicalBoard.printBoard()

            val roundResult = WinChecker.check(currentTeam)
            when (roundResult) {
                WinChecker.RoundResult.WHITE_WINS -> println("White Wins!")
                WinChecker.RoundResult.BLACK_WINS -> println("Black Wins!")
                WinChecker.RoundResult.STALEMATE -> println("Stalemate!")
            }
            if (roundResult != WinChecker.RoundResult.NONE) break

            currentTeam = currentTeam.opposite()
        }
        println("Bye!")
    }
}

enum class Team {
    BLACK, WHITE;

    fun startRank() = when (this) {
        BLACK -> LOGICAL_BOARD_SIZE - 1
        WHITE -> 2
    }

    fun maxFirstMoveRank() = when (this) {
        BLACK -> LOGICAL_BOARD_SIZE - 3
        WHITE -> 4
    }

    fun opposite(): Team {
        return when (this) {
            BLACK -> WHITE
            WHITE -> BLACK
        }
    }

    /*
    Semantically it's a direction (top or bottom) to where the team can move.
    It's how a pawn should change a row when one moves. BLACKs move down, WHITEs move up.
     */
    fun increment(): Int {
        return when (this) {
            BLACK -> 1
            WHITE -> -1
        }
    }
}