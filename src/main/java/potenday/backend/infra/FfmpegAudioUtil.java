package potenday.backend.infra;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import potenday.backend.application.AudioUtil;

import java.io.*;
import java.nio.file.Files;

@Component
class FfmpegAudioUtil implements AudioUtil {

    @Override
    public Result convertToMp3(MultipartFile audioFile) {
        String originalFilename = audioFile.getOriginalFilename();
        Assert.notNull(originalFilename, "Original filename is null");

        try {
            File tempInputFile = createTempFileWithContent(audioFile);

            String durationString = executeFfprobe(tempInputFile);
            int fileDuration = 0;
            if (durationString != null) {
                fileDuration = (int) Math.round(Double.parseDouble(durationString.trim()) / 60); // 분 단위 반환
            }

            String extension = extractExtension(originalFilename);
            if ("mp3".equals(extension)) {
                return new Result(fileDuration, audioFile); // 이미 MP3면 변환 필요 없음
            }

            File tempOutputFile = createTempFile("output_", ".mp3");
            executeFfmpeg(tempInputFile, tempOutputFile);

            return new Result(fileDuration, new CustomMultipartFile(Files.readAllBytes(tempOutputFile.toPath())));

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("오디오 변환 중 오류 발생", e);
        }
    }

    private String executeFfprobe(File tempInputFile) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(
            "ffprobe", "-i", tempInputFile.getAbsolutePath(),
            "-show_entries", "format=duration", "-v", "quiet", "-of", "csv=p=0"
        ).redirectErrorStream(true);

        Process process = builder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return reader.readLine();
        } finally {
            process.waitFor();
        }
    }

    private void executeFfmpeg(File tempInputFile, File tempOutputFile) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(
            "ffmpeg", "-i", tempInputFile.getAbsolutePath(),
            "-q:a", "2", "-y", tempOutputFile.getAbsolutePath()
        ).redirectErrorStream(true);

        Process process = builder.start();
        if (process.waitFor() != 0) {
            throw new IOException("FFmpeg 변환 실패");
        }
    }

    private File createTempFileWithContent(MultipartFile file) throws IOException {
        File tempFile = createTempFile("temp_audio_", "." + extractExtension(file.getOriginalFilename()));
        try (InputStream inputStream = file.getInputStream();
             OutputStream outputStream = new FileOutputStream(tempFile)) {
            inputStream.transferTo(outputStream);
        }
        return tempFile;
    }

    private File createTempFile(String prefix, String suffix) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix);
        tempFile.deleteOnExit();  // 프로세스 종료 시 파일 삭제
        return tempFile;
    }

    private String extractExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    static class CustomMultipartFile implements MultipartFile {

        private final byte[] content;

        public CustomMultipartFile(byte[] content) {
            this.content = content;
        }

        @Override
        public String getName() {
            return "file";
        }

        @Override
        public String getOriginalFilename() {
            return "converted.mp3";
        }

        @Override
        public String getContentType() {
            return "audio/mpeg";
        }

        @Override
        public boolean isEmpty() {
            return content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() {
            return content;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File dest) throws IOException {
            Files.write(dest.toPath(), content);
        }

    }

}
