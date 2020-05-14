package nl.nl2312.pci.parser.biorxiv

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import nl.nl2312.pci.parser.data.Author
import nl.nl2312.pci.parser.data.Publication
import nl.nl2312.pci.parser.data.zip3
import okhttp3.OkHttpClient
import okhttp3.Request

class BiorxivParser(subject: String) {

    private val recentFeedUrl = "http://connect.biorxiv.org/biorxiv_xml.php?subject=$subject"
    private val articleInfoUrl = "https://www.biorxiv.org/content/%sv1.article-info"
    private val httpClient = OkHttpClient()

    fun recent(): List<Publication> {
        val call = httpClient.newCall(Request.Builder().url(recentFeedUrl).build())
        val xml = call.execute().body
        val xmlMapper = XmlMapper().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
        val rdf = xmlMapper.readValue(xml?.byteStream(), Rdf::class.java)
        return rdf?.item?.map {
            val shortDoi = it.link.substringAfterLast("/").substringBefore("v1")
            Publication(
                shortDoi,
                "10.1101/$shortDoi",
                it.title.trim(),
                it.link.trim(),
                it.description.trim()
            )
        } ?: emptyList()
    }

    fun articleAuthors(doi: String): List<Author> {
        val call = httpClient.newCall(Request.Builder().url(articleInfoUrl.format(doi)).build())
        val html = call.execute().body?.string() ?: ""
        val authorNames = "<meta name=\"citation_author\" content=\"([^\"]+)\" />".toRegex()
            .findAll(html).map { it.groupValues[1] }
        val authorInstitutions = "<meta name=\"citation_author_institution\" content=\"([^\"]+)\" />".toRegex()
            .findAll(html).map { it.groupValues[1] }
        val authorEmails = "<meta name=\"citation_author_email\" content=\"([^\"]+)\" />".toRegex()
            .findAll(html).map { it.groupValues[1] }
        val corresponding = "Corresponding author([^>]*)>([^>]*)>([^<]*)".toRegex()
            .find(html)?.groupValues?.get(3)?.replace("{at}", "@")
        return zip3(
            authorNames,
            authorInstitutions,
            authorEmails
        ) { name, institution, email ->
            Author(
                name,
                institution.removeSuffix(";"),
                email,
                email == corresponding
            )
        }.toList()
    }
}

@JacksonXmlRootElement(localName = "rdf:RDF")
class Rdf(
    @JacksonXmlElementWrapper(useWrapping = false)
    var item: List<RdfItem>? = null
)

class RdfItem(
    var title: String = "",
    var link: String = "",
    var description: String = ""
)
