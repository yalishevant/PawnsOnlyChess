package chess

object WinChecker {

    fun check(currentTeam: Team): RoundResult {

        // if a team reaches the opposite end of the desk, it wins
        if (LogicalBoard.board[0].contains(LogicalBoard.Field(Team.WHITE))) return RoundResult.WHITE_WINS
        if (LogicalBoard.board[7].contains(LogicalBoard.Field(Team.BLACK))) return RoundResult.BLACK_WINS

        // if a team has eaten all opponents, it wins
        var countOfW = 0
        var countOfB = 0
        for (rank in 0 until LOGICAL_BOARD_SIZE) {
            for (file in 0 until LOGICAL_BOARD_SIZE) {
                when (LogicalBoard.board[rank][file].team) {
                    Team.BLACK -> countOfB++
                    Team.WHITE -> countOfW++
                }
            }
        }
        if (countOfB == 0) return RoundResult.WHITE_WINS
        if (countOfW == 0) return RoundResult.BLACK_WINS

        // if an opposite team has no options to move, then it's Stalemate
        if (getMovablePawns(currentTeam.opposite()) == 0) return RoundResult.STALEMATE

        return RoundResult.NONE
    }

    private fun getMovablePawns(currentTeam: Team): Int {
        val board = LogicalBoard.board
        var countOfMovablePawns = 0

        for (rank in 0 until LOGICAL_BOARD_SIZE) {
            for (file in 0 until LOGICAL_BOARD_SIZE) {
                if (board[rank][file].team == currentTeam) {
                    // check possibility to move forward
                    val nextRankIndex = rank + currentTeam.increment()
                    if (board[nextRankIndex][file].team == currentTeam.opposite()) // move forward is blocked
                    {
                        try {
                            // check possibility to eat the opponent
                            if (board[nextRankIndex][file - 1].team == currentTeam.opposite()
                                || board[nextRankIndex][file + 1].team == currentTeam.opposite())
                                countOfMovablePawns++
                        } catch (_: IndexOutOfBoundsException) {
                            // if the pawn stays on the first or the last file, then [file - or + 1] accordingly
                            // will throw IndexOutOfBoundsException. It's not a problem here.
                        }
                    }
                    else // move forward is possible
                        countOfMovablePawns++
                }
            }
        }
        return countOfMovablePawns
    }

    enum class RoundResult {
        WHITE_WINS, BLACK_WINS, STALEMATE, NONE
    }
}