package de.dertyp7214.rboard;

import de.dertyp7214.rboard.RboardTheme;

interface IRboard {
    int getAidlVersion();
    String[] getRboardThemes();
    @nullable Bitmap getPreview(in String name);
    @nullable RboardTheme getRboardTheme(in String name);
    boolean installRboardTheme(in RboardTheme theme);
}