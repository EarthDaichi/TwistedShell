package com.twisted.shell.builtin.commands;

import com.twisted.shell.command.Command;
import com.twisted.shell.command.CommandCategory;
import com.twisted.shell.command.CommandContext;
import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class BadAppleCommand implements Command {

    private Path temporaryDirectory;
    private Path temporaryVideo;
    private Path temporaryFfmpeg;

    private static final int WIDTH = 80;
    private static final int HEIGHT = 45;
    private static final int FPS = 30;

    private volatile Process videoProcess;
    private volatile Process audioProcess;

    private volatile Thread audioThread;
    private volatile SourceDataLine audioLine;

    private static final int WINDOW_COLUMNS = WIDTH;
    private static final int WINDOW_ROWS = HEIGHT + 2;

    private static final String VIDEO_RESOURCE =
            "/media/Bad_apple.mp4";

    private static final String FFMPEG_RESOURCE =
            "/native/windows/ffmpeg.exe";

    private static final char[] PIXELS = {
            ' ', '.', ':', '-', '=', '+', '*', '#', '%', '@'
    };

    @Override
    public String name() {
        return "badapple";
    }

    @Override
    public String description() {
        return "Play Bad Apple as ASCII animation";
    }

    @Override
    public CommandCategory category() {
        return CommandCategory.MEME;
    }

    @Override
    public String usage() {
        return "bad apple";
    }

    @Override
    public List<String> aliases() {
        return List.of("ba", "apple");
    }

    @Override
    public void execute(CommandContext context) {
        Terminal terminal = null;
        Attributes originalAttributes = null;

        int originalWidth = 80;
        int originalHeight = 24;

        Thread shutdownHook = new Thread(
                this::stopMedia,
                "bad-apple-shutdown"
        );

        Runtime.getRuntime().addShutdownHook(shutdownHook);

        try {
            prepareMediaFiles();

            String ffmpegFile =
                    temporaryFfmpeg.toAbsolutePath().toString();

            String videoFile =
                    temporaryVideo.toAbsolutePath().toString();

            terminal = TerminalBuilder.builder()
                    .system(true)
                    .jna(true)
                    .build();

            originalWidth = terminal.getWidth();
            originalHeight = terminal.getHeight();
            originalAttributes = terminal.enterRawMode();

            NonBlockingReader keyboard = terminal.reader();

            videoProcess = new ProcessBuilder(
                    ffmpegFile,
                    "-loglevel", "quiet",
                    "-i", videoFile,
                    "-vf",
                    "fps=" + FPS
                            + ",scale=" + WIDTH + ":" + HEIGHT
                            + ",format=gray",
                    "-an",
                    "-f", "rawvideo",
                    "-pix_fmt", "gray",
                    "-"
            ).start();

            startAudio(ffmpegFile, videoFile);

            resizeWindow(WINDOW_COLUMNS, WINDOW_ROWS);
            clearScreen();
            hideCursor();

            try (InputStream input =
                         new BufferedInputStream(
                                 videoProcess.getInputStream()
                         )) {

                byte[] frame = new byte[WIDTH * HEIGHT];
                long frameDuration =
                        1_000_000_000L / FPS;

                boolean playing = true;

                while (playing && readFrame(input, frame)) {
                    long frameStarted = System.nanoTime();

                    moveCursorHome();

                    System.out.print(createAsciiFrame(frame));
                    System.out.print(
                            "\033[90mPress Q or ESC to stop\033[0m"
                    );
                    System.out.flush();

                    long deadline =
                            frameStarted + frameDuration;

                    while (System.nanoTime() < deadline) {
                        int key = keyboard.read(1);

                        if (key == 'q'
                                || key == 'Q'
                                || key == 27) {
                            playing = false;
                            break;
                        }

                        long remaining =
                                deadline - System.nanoTime();

                        if (remaining > 0) {
                            Thread.sleep(
                                    Math.min(
                                            remaining / 1_000_000L,
                                            2L
                                    )
                            );
                        }
                    }
                }
            }

        } catch (Exception exception) {
            System.err.println("Unable to play Bad Apple");
            exception.printStackTrace();

        } finally {
            stopMedia();

            if (terminal != null && originalAttributes != null) {
                terminal.setAttributes(originalAttributes);
            }

            showCursor();
            resetStyle();
            resizeWindow(originalWidth, originalHeight);
            clearScreen();

            deleteTemporaryFiles();

            try {
                Runtime.getRuntime()
                        .removeShutdownHook(shutdownHook);
            } catch (IllegalStateException ignored) {
            }

            System.out.println("Bad Apple stopped.");
        }
    }

    private boolean readFrame(InputStream input, byte[] frame)
            throws Exception {

        int offset = 0;

        while (offset < frame.length) {
            int read = input.read(
                    frame,
                    offset,
                    frame.length - offset
            );

            if (read == -1) {
                return false;
            }

            offset += read;
        }

        return true;
    }

    private String createAsciiFrame(byte[] frame) {
        StringBuilder output =
                new StringBuilder((WIDTH + 1) * HEIGHT);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int brightness =
                        Byte.toUnsignedInt(frame[y * WIDTH + x]);

                int index =
                        brightness * (PIXELS.length - 1) / 255;

                output.append(PIXELS[index]);
            }

            output.append('\n');
        }

        return output.toString();
    }

    private void resizeWindow(int columns, int rows) {
        System.out.printf(
                "\033[8;%d;%dt",
                rows,
                columns
        );
        System.out.flush();
    }

    private void clearScreen() {
        System.out.print("\033[2J\033[H");
        System.out.flush();
    }

    private void moveCursorHome() {
        System.out.print("\033[H");
    }

    private void hideCursor() {
        System.out.print("\033[?25l");
        System.out.flush();
    }

    private void showCursor() {
        System.out.print("\033[?25h");
        System.out.flush();
    }

    private void resetStyle() {
        System.out.print("\033[0m");
        System.out.flush();
    }

    private synchronized void stopMedia() {
        if (audioLine != null) {
            try {
                audioLine.stop();
                audioLine.flush();
                audioLine.close();
            } catch (Exception ignored) {
            }

            audioLine = null;
        }

        stopProcess(audioProcess);
        stopProcess(videoProcess);

        if (audioThread != null) {
            audioThread.interrupt();
            audioThread = null;
        }

        audioProcess = null;
        videoProcess = null;
    }



    private void stopProcess(Process process) {
        if (process == null) {
            return;
        }

        process.descendants().forEach(child -> {
            if (child.isAlive()) {
                child.destroyForcibly();
            }
        });

        if (process.isAlive()) {
            process.destroyForcibly();
        }
    }

    private void startAudio(
            String ffmpegFile,
            String videoFile
    ) throws IOException {

        audioProcess = new ProcessBuilder(
                ffmpegFile,
                "-loglevel", "quiet",
                "-i", videoFile,
                "-vn",
                "-f", "s16le",
                "-acodec", "pcm_s16le",
                "-ar", "44100",
                "-ac", "2",
                "-"
        ).start();

        audioThread = new Thread(
                () -> playPcmAudio(audioProcess),
                "bad-apple-audio"
        );

        audioThread.setDaemon(true);
        audioThread.start();
    }

    private void playPcmAudio(Process process) {
        AudioFormat format = new AudioFormat(
                44100,
                16,
                2,
                true,
                false
        );

        DataLine.Info info = new DataLine.Info(
                SourceDataLine.class,
                format
        );

        try (InputStream input = new BufferedInputStream(
                process.getInputStream()
        )) {
            SourceDataLine line =
                    (SourceDataLine) AudioSystem.getLine(info);

            audioLine = line;

            line.open(format);
            line.start();

            byte[] buffer = new byte[8192];
            int read;

            while (!Thread.currentThread().isInterrupted()
                    && (read = input.read(buffer)) != -1) {
                line.write(buffer, 0, read);
            }

            line.drain();

        } catch (Exception exception) {
            if (audioProcess != null
                    && audioProcess.isAlive()) {
                System.err.println(
                        "Audio playback failed: "
                                + exception.getMessage()
                );
            }

        } finally {
            closeAudioLine();
        }
    }

    private synchronized void closeAudioLine() {
        if (audioLine == null) {
            return;
        }

        try {
            audioLine.stop();
            audioLine.flush();
            audioLine.close();
        } catch (Exception ignored) {
        }

        audioLine = null;
    }

    private void prepareMediaFiles() throws IOException {
        temporaryDirectory = Files.createTempDirectory(
                "twisted-shell-badapple-"
        );

        temporaryVideo = temporaryDirectory.resolve(
                "Bad_apple.mp4"
        );

        temporaryFfmpeg = temporaryDirectory.resolve(
                "ffmpeg.exe"
        );

        extractResource(
                VIDEO_RESOURCE,
                temporaryVideo
        );

        extractResource(
                FFMPEG_RESOURCE,
                temporaryFfmpeg
        );
        temporaryVideo.toFile().deleteOnExit();
        temporaryFfmpeg.toFile().deleteOnExit();
        temporaryDirectory.toFile().deleteOnExit();
    }
    private void extractResource(
            String resourcePath,
            Path destination
    ) throws IOException {

        try (InputStream input =
                     BadAppleCommand.class.getResourceAsStream(
                             resourcePath
                     )) {

            if (input == null) {
                throw new IOException(
                        "Resource not found: " + resourcePath
                );
            }

            Files.copy(
                    input,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }
    private void deleteTemporaryFiles() {
        deleteFile(temporaryVideo);
        deleteFile(temporaryFfmpeg);
        deleteFile(temporaryDirectory);

        temporaryVideo = null;
        temporaryFfmpeg = null;
        temporaryDirectory = null;
    }
    private void deleteFile(Path path) {
        if (path == null) {
            return;
        }

        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }
}