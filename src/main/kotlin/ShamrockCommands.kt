package top.mrxiaom.overflow.shamrock

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand

object ShamrockCommands : CompositeCommand(
    owner = OverflowShamrockExt,
    primaryName = "shamrock",
    secondaryNames = arrayOf()
) {
    @SubCommand
    @Description("重载插件配置")
    suspend fun CommandSender.reload() {
        OverflowShamrockExt.reloadConfig()
        sendMessage("重载成功")
    }
}