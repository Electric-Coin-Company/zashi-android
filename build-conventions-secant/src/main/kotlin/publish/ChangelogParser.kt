package publish

import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import java.io.File

object ChangelogParser {
    private const val CHANGELOG_TITLE_POSITION = 0
    private const val UNRELEASED_TITLE_POSITION = 4


    fun getChangelogEntry(filePath: String): ChangelogEntry {
        println("Parser: starting...")

        val src = File(filePath).readText()
        val flavour = GFMFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(src)

        println("Parser: ${parsedTree.children.size}")

        val nodes =
            parsedTree.children
                .map { it.getTextInNode(src).toString() }
                .filter { it.isNotBlank() }
        // .onEachIndexed { index, value -> log("Parser: item $index: $value") }

        // Validate content
        check(
            nodes[CHANGELOG_TITLE_POSITION].contains("# Changelog") &&
                nodes[UNRELEASED_TITLE_POSITION].contains("## [Unreleased]")
        ) {
            "Provided changelog file is incorrect or its structure is malformed."
        }

        // Get the last changelog entry
        val entryPredicate: (String) -> Boolean = { it.startsWith("## [") && it != "## [Unreleased]" }

        val fromIndex = nodes.indexOfFirst { entryPredicate(it) }
        val toIndex = nodes.subList(fromIndex + 1, nodes.size).indexOfFirst { entryPredicate(it) } + fromIndex + 1

        println("Parser: $fromIndex, $toIndex")

        val lastChangelogEntry =
            nodes.subList(fromIndex = fromIndex, toIndex).let { parts ->
                println("Parser: related nodes:\n${parts.joinToString("\n")}")
                ChangelogEntry(
                    version = parts[0].split("(")[1].split(")")[0],
                    date = parts[0].split("- ")[1],
                    added = parts.extractTextFromNodes("### Added", "###"),
                    changed = parts.extractTextFromNodes("### Changed", "###"),
                    fixed = parts.extractTextFromNodes("### Fixed", "###"),
                    removed = parts.extractTextFromNodes("### Removed", "###"),
                )
            }

        println("Parser: $lastChangelogEntry")

        return lastChangelogEntry
    }

    private fun List<String>.extractTextFromNodes(
        fromContent: String,
        toContent: String
    ) = runCatching {
        println("Parser: extract text from nodes: $fromContent, $toContent")
        val startIndex =
            indexOfFirst { it.contains(fromContent) }.let { index ->
                if (index < 0) {
                    return@runCatching ""
                } else {
                    index + 1
                }
            }
        val endIndex =
            subList(startIndex, size).indexOfFirst { it.contains(toContent) }.let { index ->
                if (index < 0) {
                    size
                } else {
                    index + startIndex
                }
            }
        return subList(startIndex, endIndex).joinToString("\n").also {
            println("Parser: extracted part: \n$it")
        }
    }.onFailure { println("Parser: failed with: $it") }.getOrDefault("")
}
