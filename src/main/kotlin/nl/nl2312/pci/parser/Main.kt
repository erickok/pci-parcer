package nl.nl2312.pci.parser

import nl.nl2312.pci.parser.biorxiv.BiorxivParser
import java.io.File

fun main() {
    val storage = Storage(File("storage"))
    val settings = storage.readSettings()
    val lastRun = storage.readLastRun()

    // Get recent publications
    val biorxivParser = BiorxivParser(settings.biorxivTopic)
    val mailer = Mailer(settings)
    val recent = biorxivParser.recent()
    println("Found ${recent.size} publications")

    // Process new publications
    val mostRecentDoi = mutableListOf<String>()
    var processedCount = 0
    for (publication in recent) {
        if (lastRun.doi.contains(publication.doi)) {
            mostRecentDoi.add(publication.doi)
            continue
        }

        println("  Working on ${publication.doi}")
        publication.authors = biorxivParser.articleAuthors(publication.doi)
        mailer.send(publication)
        storage.appendToHistory(publication)

        mostRecentDoi.add(publication.doi)
        processedCount++
    }

    println("Processed $processedCount new publications")
    storage.saveLastRun(LastRun(processedCount, mostRecentDoi))
}
