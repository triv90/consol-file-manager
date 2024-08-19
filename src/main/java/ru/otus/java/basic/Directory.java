package ru.otus.java.basic;

import java.nio.file.Path;

public class Directory {

    private Path directory;

    public Path getDirectory() {
        return directory;
    }

    public void setDirectory(Path directory) {
        this.directory = directory;
    }

    public Directory(Path directory) {
        this.directory = directory;
    }
}
