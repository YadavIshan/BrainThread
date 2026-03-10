package com.ishan.BrainThread.utils;

import java.time.LocalDateTime;

public class CursorUtils {
    public static boolean isValidCursor(String cursor) {
        if(cursor == null || cursor.isEmpty()) {
            return false;
        }
        try{
            LocalDateTime.parse(cursor);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public static LocalDateTime encodeCursor(String cursor) {
        if(!isValidCursor(cursor)) {
            throw new IllegalArgumentException("invalid cursor");
        }
        return LocalDateTime.parse(cursor);
    }
}
