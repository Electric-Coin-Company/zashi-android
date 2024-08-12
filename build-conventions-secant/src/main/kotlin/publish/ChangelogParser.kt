package publish

import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ChangelogParser {
    // Enable this when you need detailed parser logging. This should be turned off for production builds.
    private const val DEBUG_LOGS_ENABLED = false

    private const val CHANGELOG_TITLE_POSITION = 0
    private const val UNRELEASED_TITLE_POSITION = 4

    private fun log(value: Any) {
        if (DEBUG_LOGS_ENABLED) {
            println(value)
        }
    }

    fun getChangelogEntry(
        filePath: String,
        versionNameFallback: String
    ): ChangelogEntry {
        log("Parser: starting...")

        val src = File(filePath).readText()
        val parsedTree = MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(src)

        log("Parser: ${parsedTree.children.size}")

        val nodes =
            parsedTree.children
                .map { it.getTextInNode(src).toString() }
                .filter { it.isNotBlank() }
                .onEachIndexed { index, value -> log("Parser: node $index: $value") }

        // Validate content
        check(
            nodes[CHANGELOG_TITLE_POSITION].contains("# Changelog") &&
                nodes[UNRELEASED_TITLE_POSITION].contains("## [Unreleased]")
        ) {
            "Provided changelog file is incorrect or its structure is malformed."
        }

        val fromIndex = findFirstValidNodeIndex(nodes)
        log("Parser: index from: $fromIndex")

        val toIndex =
            nodes.subList(fromIndex + 1, nodes.size)
                .indexOfFirst { findNodeByPrefix(it) }
                .let {
                    // Applies to the last or the only one entry
                    if (it < 0) {
                        nodes.size
                    } else {
                        it + fromIndex + 1
                    }
                }
        log("Parser: index to: $toIndex")

        val lastChangelogEntry =
            nodes.subList(fromIndex = fromIndex, toIndex = toIndex).let { parts ->
                ChangelogEntry(
                    version = parts.getVersionPart(versionNameFallback),
                    date = parts.getDatePart(),
                    added = parts.getNodePart("Added"),
                    changed = parts.getNodePart("Changed"),
                    fixed = parts.getNodePart("Fixed"),
                    removed = parts.getNodePart("Removed"),
                )
            }

        log("Parser: result: $lastChangelogEntry")
        return lastChangelogEntry
    }

    private fun findFirstValidNodeIndex(nodes: List<String>): Int {
        nodes.forEachIndexed { index, node ->
            if (findNodeByPrefix(node) && findValidSubNodeByPrefix(nodes[index + 1])) {
                return index
            }
        }

        error("Provided changelog file is incorrect or its structure is malformed.")
    }

    private fun findNodeByPrefix(node: String): Boolean = node.startsWith("## [")

    private fun findValidSubNodeByPrefix(subNode: String): Boolean =
        subNode.startsWith("### Added") ||
            subNode.startsWith("### Changed") ||
            subNode.startsWith("### Fixed") ||
            subNode.startsWith("### Removed")

    private fun List<String>.getVersionPart(versionNameFallback: String): String {
        return if (this.contains("## [Unreleased]")) {
            versionNameFallback
        } else {
            this[0].split("[")[1].split("]")[0].trim()
        }
    }

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    private fun List<String>.getDatePart(): String {
        return if (this.contains("## [Unreleased]")) {
            dateFormatter.format(Date())
        } else {
            this[0].split("- ")[1].trim()
        }
    }

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
        return subList(startIndex, endIndex)
            .onEach { log("Parser: before formatting item: $it") }
            // To remove hard line wrap from AS
            .map { it.replace("\n  ", "") }
            .joinToString(prefix = "\n", separator = "\n")
            .takeIf { it.isNotBlank() }?.let {
                ChangelogEntrySection(title = title, content = it)
            }
    }
}
