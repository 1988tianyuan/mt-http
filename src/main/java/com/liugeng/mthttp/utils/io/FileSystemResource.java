package com.liugeng.mthttp.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.liugeng.mthttp.utils.Assert;

public class FileSystemResource implements Resource{
    private final String fileSystemPath;
    private final File file;

    public FileSystemResource(String fileSystemPath) {
        Assert.notNull(fileSystemPath, "path must not be null");
        this.fileSystemPath = fileSystemPath;
        this.file = new File(fileSystemPath);
    }

    public FileSystemResource(File file) {
        Assert.notNull(file, "file must not be null");
        this.fileSystemPath = file.getPath();
        this.file = file;
    }

    public InputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(file);
    }

    public String getDescription() {
        return this.fileSystemPath;
    }
}
