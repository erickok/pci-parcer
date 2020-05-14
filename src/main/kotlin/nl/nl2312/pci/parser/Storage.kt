package nl.nl2312.pci.parser

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import nl.nl2312.pci.parser.data.Publication
import okio.buffer
import okio.source
import java.io.File
import java.io.FileNotFoundException

class Storage(dir: File) {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val moshiSettings = moshi.adapter(Settings::class.java).indent("  ")
    private val moshiPublication = moshi.adapter(Publication::class.java).indent("  ")
    private val moshiLastRun = moshi.adapter(LastRun::class.java).indent("  ")

    init {
        dir.mkdirs()
    }

    private val settingsFile = File(dir, "pci-parser-settings.json")
    private val historyFile = File(dir, "pci-parser-history.json").apply {
        if (!exists()) createNewFile()
    }
    private val lastRunFile = File(dir, "pci-parser-last.json")

    fun readSettings(): Settings {
        return moshiSettings.fromJson(settingsFile.source().buffer())
            ?: throw FileNotFoundException("pci-parser-settings.json not found or corrupt")
    }

    fun readLastRun(): LastRun {
        return if (lastRunFile.exists()) {
            moshiLastRun.fromJson(lastRunFile.source().buffer()) ?: LastRun()
        } else LastRun()
    }

    fun saveLastRun(run: LastRun) {
        lastRunFile.writeText(moshiLastRun.toJson(run))
    }

    fun appendToHistory(publication: Publication) {
        val pubText = moshiPublication.toJson(publication)
        historyFile.appendText("$pubText,")
    }
}

class Settings(
    val emailFromAddress: String, val emailFromName: String, val emailTo: List<String>,
    val emailHostName: String, val emailHostUser: String, val emailHostPass: String,
    val biorxivTopic: String
)

class LastRun(val processCount: Int = 0, val doi: List<String> = emptyList())
