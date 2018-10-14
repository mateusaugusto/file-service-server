package com.exb.server.fileserviceserver.repository;


import com.exb.server.fileserviceserver.domain.File;
import com.exb.server.fileserviceserver.exception.FileServiceException;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Repository
public class FileRepository {

    private static ExecutorService saveFileExecutor = Executors.newFixedThreadPool(5);

    private static ExecutorService deleteFileExecutor = Executors.newFixedThreadPool(5);

    private final ConcurrentNavigableMap<String, List<File>> fileListMap = new ConcurrentSkipListMap();

    private final ReentrantLock lock = new ReentrantLock();

    public void save(final String aSessionId, MultipartFile file) {
        saveFileExecutor.submit(new Thread(() -> {
            lock.lock();

            try {

                byte[] bytes = file.getBytes();
                Path path = Paths.get("/tmp/" + file.getOriginalFilename());
                Path tempPath = Files.write(path, bytes);

                if (!fileListMap.containsKey(aSessionId)) {
                    fileListMap.put(aSessionId, new ArrayList());
                }

                fileListMap.get(aSessionId).add(this.buildFile(tempPath));

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        }));
    }

    private File buildFile(Path tempPath) {
        File file = new File();
        file.setName(tempPath.getFileName().toString());
        file.setPath(tempPath);
        return file;
    }

    public boolean exists(final String aSessionId, final String aPath) throws FileServiceException {
        lock.lock();
        try {
            return fileListMap
                    .tailMap(aSessionId)
                    .values()
                    .parallelStream()
                    .flatMap(Collection::parallelStream)
                    .filter(path -> path.getPath().toString().equals(aPath))
                    .findFirst()
                    .isPresent();
        } finally {
            lock.unlock();
        }

    }

    public String getParent(final String aSessionId, final String aPath) throws FileServiceException {
        lock.lock();

        try {
            File file = this.getFileInHashMap(aSessionId, aPath);

            if (file.getPath() == null) {
                return null;
            }

            return file.getPath().getParent().toString();

        } finally {
            lock.unlock();
        }
    }

    private File getFileInHashMap(final String aSessionId, final String aPath) {
        return fileListMap
                .tailMap(aSessionId)
                .values()
                .parallelStream()
                .flatMap(Collection::parallelStream)
                .filter(path -> path.getPath().toString().equals(aPath))
                .findFirst()
                .orElse(new File());
    }

    public List<String> findAll(final String aSessionId) throws FileServiceException {
        lock.lock();
        try {
            return fileListMap
                    .tailMap(aSessionId)
                    .values()
                    .parallelStream()
                    .flatMap(Collection::parallelStream)
                    .map(p -> p.getPath().toAbsolutePath().toString())
                    .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    public List<File> findAllFile(final String aSessionId) throws FileServiceException {
        lock.lock();
        try {
            return fileListMap
                    .tailMap(aSessionId)
                    .values()
                    .parallelStream()
                    .flatMap(Collection::parallelStream)
                    .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    public OutputStream openForWriting(final String aSessionId, final String aPath, final boolean aAppend)
            throws FileServiceException {
        lock.lock();
        try {
            File file = this.getFileInHashMap(aSessionId, aPath);
            return new FileOutputStream(file.getPath().toFile(), aAppend);
        } catch (final FileNotFoundException e) {
            throw new FileServiceException("cannot open entry", e);
        } finally {
            lock.unlock();
        }
    }

    public InputStream openForReading(final String aSessionId, final String aPath) throws FileServiceException {
        lock.lock();
        try {
            File file = this.getFileInHashMap(aSessionId, aPath);
            return new FileInputStream(file.getPath().toFile());
        } catch (final FileNotFoundException e) {
            throw new FileServiceException("cannot open entry", e);
        } finally {
            lock.unlock();
        }
    }

    public void delete(final String aSessionId, final String aPath, final boolean aRecursive)
            throws FileServiceException {

        deleteFileExecutor.submit(new Thread(() -> {
            lock.lock();

            try {

                File file = this.getFileInHashMap(aSessionId, aPath);

                if (file.getPath() == null) {
                    return;
                }

                if (aRecursive) {
                    final Path path = Paths.get(file.getPath().toAbsolutePath().toString());
                    try {
                        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                            @Override
                            public FileVisitResult visitFile(final Path aFile, final BasicFileAttributes aAttrs)
                                    throws IOException {
                                Files.delete(aFile);
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(final Path aDir, final IOException aExc)
                                    throws IOException {
                                Files.delete(aDir);
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    } catch (final IOException e) {
                        try {
                            throw new FileServiceException("cannot delete entries", e);
                        } catch (FileServiceException e1) {
                            e1.printStackTrace();
                        }
                    }

                } else {
                    if (!file.getPath().toFile().delete()) {
                        throw new FileServiceException("cannot delete entry");
                    }
                }

                fileListMap
                        .tailMap(aSessionId)
                        .values()
                        .removeIf(path -> path.removeIf(pr -> pr.getPath().toString().equals(aPath)));

            } catch (FileServiceException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        }));
    }

}
