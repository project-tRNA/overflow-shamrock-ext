package top.mrxiaom.overflow.shamrock

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.Services
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.FileEntity
import org.apache.http.impl.client.HttpClients
import top.mrxiaom.overflow.spi.FileService
import java.io.File
import java.util.*

class ShamrockFileService : FileService {
    override val priority: Int = 900
    private val client = HttpClients.createDefault()
    override suspend fun upload(res: ExternalResource): String {
        if (!tempFolder.exists()) tempFolder.mkdirs()
        val file = File(tempFolder, "${UUID.randomUUID()}.tmp")
        res.inputStream().use { file.writeBytes(it.readBytes()) }
        val post = HttpPost("${url.removeSuffix("/")}/upload_file")
        post.entity = FileEntity(file)
        val resultString = client.execute(post).entity.content.readBytes().toString(Charsets.UTF_8)
        val result = Json.decodeFromString(ShamrockFile.serializer(), resultString)
        file.delete()
        return "file:///${result.file.removePrefix("/")}"
    }
    @Serializable
    data class ShamrockFile (
        @SerialName("file")
        val file: String
    )

    companion object {
        var tempFolder = File(".temp")
        @JvmStatic
        var url: String = "http://127.0.0.1:5700"
        @JvmStatic
        fun register() {
            Services.register(
                FileService::class.qualifiedName!!,
                ShamrockFileService::class.qualifiedName!!,
                ::ShamrockFileService
            )
        }
    }
}
