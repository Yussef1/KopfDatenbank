package de.yussef.headdatabase.utils;

import de.yussef.headdatabase.HeadDatabase;

public class ConfigMessages {

    public final static String HDB_PREFIX = HeadDatabase.getHeadDatabase().getConfig().getString("General.Prefix").replace("&", "§"),
                               NO_PERMISSION_MESSAGE = HeadDatabase.getHeadDatabase().getConfig().getString("General.NoPermissionMessage").replace("%prefix%", HDB_PREFIX).replace("&", "§"),
                               SENDER_IS_CONSOLE = HeadDatabase.getHeadDatabase().getConfig().getString("General.SenderIsConsole"),
                               COMMAND_PERMISSION = HeadDatabase.getHeadDatabase().getConfig().getString("General.Permission");
}