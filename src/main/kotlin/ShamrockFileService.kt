package top.mrxiaom.overflow.shamrock

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.Services
import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import top.mrxiaom.overflow.spi.FileService
import java.util.*

class ShamrockFileService : FileService {
    override val priority: Int = 900
    override suspend fun upload(res: ExternalResource): String {
        OverflowShamrockExt.logger.debug("正在上传 ${res.formatName} 文件")
        val bytes = res.inputStream().use { it.readBytes() }
        return runCatching {
            HttpClients.createDefault().use {
                val post = HttpPost("${url.removeSuffix("/")}/upload_file").apply {
                    entity = buildMultipartEntity {
                        addBinaryBody(
                            "file",
                            bytes,
                            ContentType.create(res.mimeType),
                            "${UUID.randomUUID()}.${res.formatName}"
                        )
                        setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                        setCharset(Charsets.UTF_8)
                    }
                }
                it.execute(post).use { resp ->
                    val resultString = EntityUtils.toString(resp.entity, Charsets.UTF_8)
                    val result = json.decodeFromString(ShamrockResponse.serializer(), resultString)
                    if (result.retCode == 0) {
                        val file = result.file()
                        "file:///${file.file.removePrefix("/")}"
                    } else throw IllegalStateException("message=${result.message}, retCode=${result.retCode}")
                }
            }
        }.onFailure {
            OverflowShamrockExt.logger.warning("上传 ${res.formatName} 文件时出错", it)
        }.getOrElse { "" }
    }
    @Serializable
    data class ShamrockResponse (
        @SerialName("status")
        val status: String,
        @SerialName("retcode")
        val retCode: Int,
        @SerialName("data")
        val data: JsonElement,
        @SerialName("message")
        val message: String,
        @SerialName("echo")
        val echo: JsonElement
    ) {
        fun file(): ShamrockFile {
            return json.decodeFromJsonElement(data)
        }
    }
    @Serializable
    data class ShamrockFile (
        @SerialName("file")
        val file: String,
        @SerialName("md5")
        val md5: String
    )

    companion object {
        private val json = Json { ignoreUnknownKeys = true }
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
        val ExternalResource.mimeType: String
            get() =  when (formatName) {
                "png" -> "image/png"
                "jpg" -> "image/jpeg"
                "gif" -> "image/gif"
                "tif" -> "image/tiff"
                "bmp" -> "image/bmp"
                "amr" -> "audio/amr"
                "silk" -> "audio/silk"
                "mp4" -> "video/mp4"
                "mkv" -> "video/x-matroska"
                else -> "application/octet-stream"
            }

        fun buildMultipartEntity(block: MultipartEntityBuilder.() -> Unit): HttpEntity {
            return MultipartEntityBuilder.create().also(block).build()
        }
    }
}
