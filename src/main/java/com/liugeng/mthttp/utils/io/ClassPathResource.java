package com.liugeng.mthttp.utils.io;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.liugeng.mthttp.utils.ClassUtils;

public class ClassPathResource implements Resource{
    private String classPath;
    private ClassLoader loader;

    public ClassPathResource(String classPath) {
        this(classPath, null);
    }

    public ClassPathResource(String classPath, ClassLoader loader) {
        this.classPath = classPath;
        this.loader = loader != null ? loader : ClassUtils.getDefaultClassLoader();
    }

    public InputStream getInputStream() throws FileNotFoundException {
        InputStream is = this.loader.getResourceAsStream(this.classPath);
        if(is == null){
            throw new FileNotFoundException("file: " + this.classPath +" not found");
        }
        return is;
    }

    public String getDescription() {
        return this.classPath;
    }
}
