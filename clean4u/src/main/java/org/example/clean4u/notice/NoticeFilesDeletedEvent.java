package org.example.clean4u.notice;

import org.example.clean4u.noticeFile.NoticeFile;

import java.util.List;

public class NoticeFilesDeletedEvent {
    private final List<NoticeFile> files;

    public NoticeFilesDeletedEvent(List<NoticeFile> files) {
        this.files = files;
    }

    public List<NoticeFile> getFiles() {
        return files;
    }
}
