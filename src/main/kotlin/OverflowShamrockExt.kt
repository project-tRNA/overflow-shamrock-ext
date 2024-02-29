package top.mrxiaom.overflow.shamrock

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin

object OverflowShamrockExt : KotlinPlugin(
    JvmPluginDescription(
        id = "top.mrxiaom.overflow-shamrock-ext",
        name = "OverflowShamrockExt",
        version = BuildConstants.VERSION,
    ) {
        author("MrXiaoM")
    }
) {
    override fun PluginComponentStorage.onLoad() {
        reloadConfig()
        ShamrockFileService.register()
        logger.info("已注册 Shamrock 文件服务")
    }

    override fun onEnable() {
        ShamrockCommands.register()
    }

    fun reloadConfig() {
        Config.reload()
        ShamrockFileService.url = Config.url
    }

    object Config : ReadOnlyPluginConfig("config") {
        @ValueDescription("Shamrock 主动HTTP地址，结尾可加可不加/")
        val url by value("http://127.0.0.1:5700")
    }
}
