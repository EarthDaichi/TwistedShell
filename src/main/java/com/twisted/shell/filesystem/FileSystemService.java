package com.twisted.shell.filesystem;

import com.twisted.shell.shell.ShellContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileSystemService {

    private static final long MAX_READ_SIZE = 5 * 1024 * 1024;

    private final ShellContext shell;

    public FileSystemService(ShellContext shell) {
        this.shell = shell;
    }

    public Path resolvePath(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Path cannot be empty.");
        }

        Path path = Path.of(input);

        if (path.isAbsolute()) {
            return path.normalize();
        }

        return shell.currentDirectory()
                .resolve(path)
                .normalize();
    }

    public List<Path> list(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            List<Path> entries = new ArrayList<>();

            try (DirectoryStream<Path> stream =
                         Files.newDirectoryStream(path)) {
                for (Path entry : stream) {
                    entries.add(entry);
                }
            }

            entries.sort(Comparator
                    .comparing((Path p) -> !Files.isDirectory(p))
                    .thenComparing(
                            p -> p.getFileName().toString(),
                            String.CASE_INSENSITIVE_ORDER
                    ));

            return entries;
        }

        if (Files.exists(path)) {
            return List.of(path);
        }

        throw new java.nio.file.NoSuchFileException(path.toString());
    }

    public void createDirectory(Path path) throws IOException {
        Files.createDirectory(path);
    }

    public void delete(Path path) throws IOException {
        Files.delete(path);
    }

    public void copy(Path source, Path destination)
            throws IOException {

        if (!Files.isRegularFile(source)) {
            throw new IOException(
                    "Source must be a regular file: " + source
            );
        }

        Path target = destination;

        if (Files.isDirectory(destination)) {
            target = destination.resolve(source.getFileName());
        }

        if (source.equals(target.toAbsolutePath().normalize())) {
            throw new IOException("Source and destination are identical.");
        }

        Files.copy(source, target);
    }

    public void move(Path source, Path destination)
            throws IOException {

        if (!Files.exists(source)) {
            throw new java.nio.file.NoSuchFileException(
                    source.toString()
            );
        }

        Path target = destination;

        if (Files.isDirectory(destination)) {
            target = destination.resolve(source.getFileName());
        }

        if (source.equals(target.toAbsolutePath().normalize())) {
            throw new IOException("Source and destination are identical.");
        }

        try {
            Files.move(source, target);
        } catch (java.nio.file.AtomicMoveNotSupportedException e) {
            Files.move(source, target);
        }
    }

    public String readText(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IOException("Not a regular file: " + path);
        }

        long size = Files.size(path);

        if (size > MAX_READ_SIZE) {
            throw new IOException(
                    "File exceeds the 5 MiB reading limit."
            );
        }

        return Files.readString(path, StandardCharsets.UTF_8);
    }
}