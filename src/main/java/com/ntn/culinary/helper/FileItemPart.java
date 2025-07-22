package com.ntn.culinary.helper;

import org.apache.commons.fileupload.FileItem;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;

public class FileItemPart implements Part {
    private final FileItem fileItem;

    public FileItemPart(FileItem fileItem) {
        this.fileItem = fileItem;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return fileItem.getInputStream();
    }

    @Override
    public String getContentType() {
        return fileItem.getContentType();
    }

    @Override
    public String getName() {
        return fileItem.getFieldName();
    }

    @Override
    public String getSubmittedFileName() {
        return fileItem.getName();
    }

    @Override
    public long getSize() {
        return fileItem.getSize();
    }

    @Override
    public void write(String fileName) throws IOException {
        try {
            fileItem.write(new File(fileName));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void delete() throws IOException {
        fileItem.delete();
    }

    @Override
    public String getHeader(String name) {
        return fileItem.getHeaders().getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return Arrays.asList(fileItem.getHeaders().getHeader(name));
    }

    @Override
    public Collection<String> getHeaderNames() {
        Iterator<String> iterator = fileItem.getHeaders().getHeaderNames();
        List<String> names = new ArrayList<>();
        while (iterator.hasNext()) {
            names.add(iterator.next());
        }
        return names;
    }
}
