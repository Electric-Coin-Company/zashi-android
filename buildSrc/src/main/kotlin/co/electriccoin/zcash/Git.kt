package co.electriccoin.zcash

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ObjectId
import java.io.File

object Git {
    // Get the info for the current branch
    const val HEAD = "HEAD" // $NON-NLS-1$
    const val MAIN = "main" // $NON-NLS-1$

    fun newInfo(
        branch: String,
        workingDirectory: File
    ): GitInfo {
        val git = Git.open(workingDirectory)
        val repository = git.repository

        val head: ObjectId = repository.resolve(branch)
        val count = git.log().call().count()

        return GitInfo(ObjectId.toString(head), count)
    }
}

data class GitInfo(val sha: String, val commitCount: Int)
