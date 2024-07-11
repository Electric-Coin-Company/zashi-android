package publish

import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import java.io.File

object ChangelogParser {
    private const val CHANGELOG_TITLE_POSITION = 0
    private const val UNRELEASED_TITLE_POSITION = 4

    fun getChangelogEntry(filePath: String): ChangelogEntry {
        val src = File(filePath).readText()
        val parsedTree = MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(src)

        println("Parser: ${parsedTree.children.size}")

        val nodes =
            parsedTree.children
                .map { it.getTextInNode(src).toString() }
                .filter { it.isNotBlank() }

        // Validate content
        check(
            nodes[CHANGELOG_TITLE_POSITION].contains("# Changelog") &&
                nodes[UNRELEASED_TITLE_POSITION].contains("## [Unreleased]")
        ) {
            "Provided changelog file is incorrect or its structure is malformed."
        }

        val fromIndex = nodes.indexOfFirst { findNodeByPrefix(it) }
        val toIndex = nodes.subList(fromIndex + 1, nodes.size).indexOfFirst { findNodeByPrefix(it) } + fromIndex + 1

        val lastChangelogEntry =
            nodes.subList(fromIndex = fromIndex, toIndex).let { parts ->
                ChangelogEntry(
                    version = parts[0].split("[")[1].split("]")[0].trim(),
                    date = parts[0].split("- ")[1].trim(),
                    added = parts.getNodePart("Added"),
                    changed = parts.getNodePart("Changed"),
                    fixed = parts.getNodePart("Fixed"),
                    removed = parts.getNodePart("Removed"),
                )
            }

        return lastChangelogEntry
    }

    private fun findNodeByPrefix(node: String): Boolean = node.startsWith("## [") && node != "## [Unreleased]"

    private fun List<String>.getNodePart(title: String): ChangelogEntrySection? {
        val fromContent = "### $title"
        val toContent = "###"
        val startIndex =
            indexOfFirst { it.contains(fromContent) }.let { index ->
                if (index < 0) {
                    return null
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
        return subList(startIndex, endIndex).joinToString("\n").takeIf { it.isNotBlank() }?.let {
            ChangelogEntrySection(title = title, content = it)
        }
    }
}
