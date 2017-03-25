package tk.omgpi.files;

import tk.omgpi.OMGPI;

public class Mainfig extends OMGConfig {
    public Mainfig() {
        super(OMGPI.instance.getDataFolder(), "main.omgc");
        if (setUnpresent("selectedGame", "random"))
            setUnpresent("mysql", "hostname", "localhost", "port", "3306", "database", "db", "username", "admin", "password", "1234");
        setUnpresent("area", "-500,0,-500,500,255,500");
        save();
    }
}
