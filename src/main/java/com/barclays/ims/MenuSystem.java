package com.barclays.ims;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Class responsible for the functionality of the menu system.
 * You may build this out in any way you wish, provided that you stick to CLI.
 * GUIs, such as swing, are not permitted.
 */
public class MenuSystem {

    public static final Logger LOGGER = LogManager.getLogger();

    public MenuSystem() {
        init();
    }

    public void init() {
        DbUtils.connect();
        DbUtils.getInstance().init("src/main/resources/sql-schema.sql", "src/main/resources/sql-data.sql");
        System.out.println("haha");

    }

}
