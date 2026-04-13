package jammy.platform.services;

import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Map;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Singleton
public class S3Service {
  private final S3Client s3Client;
  private final S3Presigner presigner;

  private final String bucketName;

  public S3Service(
      S3Client s3Client,
      S3Presigner presigner,
      @ConfigProperty(name = "bucket.name") String bucketName) {
    this.s3Client = s3Client;
    this.presigner = presigner;
    this.bucketName = bucketName;
  }

  public void uploadFileFromResources() {
    String fileName = "example.txt";

    // Читаємо файл з папки resources
    try (InputStream is =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
      if (is == null) {
        throw new RuntimeException("Файл не знайдено в resources: " + fileName);
      }

      // Перетворюємо в RequestBody для S3
      // Примітка: для InputStream потрібно вказати розмір контенту

      PutObjectResponse putObjectResponse =
          s3Client.putObject(
              request ->
                  request
                      .bucket(bucketName)
                      .key(fileName)
                      .contentType("text/plain")
                      .checksumAlgorithm((ChecksumAlgorithm) null),
              RequestBody.fromInputStream(is, is.available()));

      System.out.println("Завантажено успішно: " + putObjectResponse.toString());

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public String createPresignedUrl(String keyName, Map<String, String> metadata) {
    PutObjectRequest objectRequest =
        PutObjectRequest.builder().bucket(bucketName).key(keyName).metadata(metadata).build();

    PutObjectPresignRequest presignRequest =
        PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(5))
            .putObjectRequest(objectRequest)
            .build();

    PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
    return presignedRequest.url().toExternalForm();
  }
}
