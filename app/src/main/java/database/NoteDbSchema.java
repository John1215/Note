package database;

/**
 * Created by Deer-Apple on 2016/12/8.
 */

public class NoteDbSchema {
    public static final class NoteTable{
        public static final String NAME = "notes";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String CONTENT = "content";
            public static final String COLOR = "color";
        }
    }
}
