package org.example.clean4u.notice;

import java.util.List;

public class NoticeImagesDeletedEvent {
    private final List<String> filenames;

    public NoticeImagesDeletedEvent(List<String> filenames) {
        this.filenames = filenames;
    }

    public List<String> getFilenames() {
        return filenames;
    }
}
