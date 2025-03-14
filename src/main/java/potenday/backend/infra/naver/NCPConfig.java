package potenday.backend.infra.naver;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class NCPConfig {

    @Value("${spring.cloud.ncp.storage.region}")
    private String region;
    @Value("${spring.cloud.ncp.storage.endpoint}")
    private String endPoint;
    @Value("${spring.cloud.ncp.storage.access-key}")
    private String accessKey;
    @Value("${spring.cloud.ncp.storage.secret-key}")
    private String secretKey;

    @Bean
    AmazonS3Client objectStorageClient() {
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, region))
            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
            .build();
    }

}
