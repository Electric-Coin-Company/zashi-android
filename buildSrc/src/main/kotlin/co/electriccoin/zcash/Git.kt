package co.electriccoin.zcash

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ObjectId
import java.io.File

object Git {
    // Get the info for the current branch
    private const val HEAD = "HEAD"

    fun newInfo(workingDirectory: File): GitInfo {
        val git = Git.open(workingDirectory)
        val repository = git.repository

        val head: ObjectId = repository.resolve(HEAD)
        val count = git.log().call().count()

        return GitInfo(ObjectId.toString(head), count)
    }
}

data class GitInfo(val sha: String, val commitCount: Int)
