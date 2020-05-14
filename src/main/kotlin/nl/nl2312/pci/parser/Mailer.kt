package nl.nl2312.pci.parser

import nl.nl2312.pci.parser.data.Publication
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail

class Mailer(private val settings: Settings) {

    fun send(publication: Publication) {
        val authorsText = publication.authors.orEmpty().joinToString("\n              ") { author ->
            val corresponding = if (author.isCorresponding) " ***" else ""
            "${author.name}$corresponding, ${author.institution} (${author.email})"
        }
        val message = """
            A new publication was submitted to bioRxiv:
            
            ${publication.title}
              $authorsText
              
            ${publication.doi}
            ${publication.link}
            
            ${publication.description}
        """.trimIndent()

        SimpleEmail().apply {
            hostName = settings.emailHostName
            setSmtpPort(587)
            setAuthenticator(DefaultAuthenticator(settings.emailHostUser, settings.emailHostPass))
            isStartTLSEnabled = true

            setFrom(settings.emailFromAddress, settings.emailFromName)
            subject = "New bioRxiv pub: ${publication.title}"
            setMsg(message)
            settings.emailTo.forEach { addTo(it) }
            send()
        }
    }
}
