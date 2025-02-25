package potenday.backend.infra;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import potenday.backend.application.AudioUtil;

import java.io.*;

@Component
public class FfmpegAudioUtil implements AudioUtil {

    @Override
    public int getDuration(MultipartFile audioFile) {
        try {
            byte[] fileBytes = audioFile.getBytes();
            return extractAudioDuration(fileBytes);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("오디오 길이 분석 중 오류 발생", e);
        }
    }

    @Override
    public MultipartFile convertToMp3(MultipartFile audioFile) {
        String originalFilename = audioFile.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("파일 이름을 확일할 수 없습니다.");
        }

        String extension = extractExtension(originalFilename);
        if (extension.equals("mp3")) {
            return audioFile;
        }

        try {
            byte[] sourceBytes = audioFile.getBytes();

            byte[] mp3Bytes = convertAudioToMp3(sourceBytes, extension);

            return new CustomMultipartFile(mp3Bytes);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("오디오 변환 중 오류 발생", e);
        }
    }

    private int extractAudioDuration(byte[] audioBytes) throws IOException, InterruptedException {
        // 임시 파일 생성 (입력 파일)
        File tempInputFile = File.createTempFile("input_", ".tmp"); // 파일 확장자는 임시로 .tmp로 설정

        // 오디오 데이터를 임시 파일에 저장
        try (FileOutputStream fos = new FileOutputStream(tempInputFile)) {
            fos.write(audioBytes);
            fos.flush(); // 파일에 데이터가 완전히 기록되도록
        }

        // ffprobe를 사용하여 오디오 파일의 길이 추출
        ProcessBuilder builder = new ProcessBuilder(
            "ffprobe", "-i", tempInputFile.getAbsolutePath(),
            "-show_entries", "format=duration", "-v", "quiet", "-of", "csv=p=0"
        );
        builder.redirectErrorStream(true);

        Process process = builder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String durationString = reader.readLine();
            process.waitFor();

            if (durationString != null) {
                double duration = Double.parseDouble(durationString.trim());
                return (int) Math.round(duration / 60); // 분 단위로 반환
            }
        } finally {
            // 임시 파일 삭제
            tempInputFile.delete();
        }

        return 0; // 예외적인 경우
    }

    private byte[] convertAudioToMp3(byte[] audioBytes, String inputFileType) throws IOException, InterruptedException {
        // 임시 입력 파일과 출력 파일 생성
        File tempInputFile = File.createTempFile("input_", "." + inputFileType);
        File tempOutputFile = File.createTempFile("output_", ".mp3");

        // 입력 파일에 데이터 기록
        try (FileOutputStream fos = new FileOutputStream(tempInputFile)) {
            fos.write(audioBytes);
            fos.flush(); // 파일에 데이터가 완전히 기록되도록
        }

        // ffmpeg로 파일 변환
        ProcessBuilder builder = new ProcessBuilder(
            "ffmpeg", "-i", tempInputFile.getAbsolutePath(), "-q:a", "2", "-y", tempOutputFile.getAbsolutePath()
        );
        builder.redirectErrorStream(true);
        Process process = builder.start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("FFmpeg process failed with exit code " + exitCode);
        }

        // 변환된 MP3 파일을 읽어서 바이트 배열로 반환
        byte[] mp3Bytes;
        try (FileInputStream fis = new FileInputStream(tempOutputFile)) {
            mp3Bytes = fis.readAllBytes();
        }

        // 임시 파일 삭제
        tempInputFile.delete();
        tempOutputFile.delete();

        return mp3Bytes;
    }


    private String extractExtension(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
    }

    static class CustomMultipartFile implements MultipartFile {

        private final byte[] input;

        public CustomMultipartFile(byte[] input) {
            this.input = input;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(input);
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
            return input.length == 0;
        }

        @Override
        public long getSize() {
            return input.length;
        }

        @Override
        public byte[] getBytes() {
            return input;
        }

        @Override
        public void transferTo(File dest) throws IOException {
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(input);
            }
        }

    }

}
