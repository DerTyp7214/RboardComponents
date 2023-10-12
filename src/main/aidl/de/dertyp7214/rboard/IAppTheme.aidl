package de.dertyp7214.rboard;

import de.dertyp7214.rboard.RboardTheme;

interface IAppTheme {
    int getAidlVersion();
    String getAppTheme();
    String[] getRboardThemes();
    RboardTheme getRboardTheme(in String name);
    boolean installRboardTheme(in RboardTheme theme);
}