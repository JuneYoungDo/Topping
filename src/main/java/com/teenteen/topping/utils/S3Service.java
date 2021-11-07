package com.teenteen.topping.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3Service {
    private AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.region.static}")
    private String region;

    public String upload(MultipartFile file) throws IOException {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
        String fileName = System.currentTimeMillis() + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        byte[] bytes = IOUtils.toByteArray(file.getInputStream());
        metadata.setContentLength(bytes.length);
        s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        return fileName;
    }

    public String uploadThumbnail(MultipartFile file) throws IOException {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();

        File uploadFile = new File(System.currentTimeMillis() + "s_" + file.getOriginalFilename());
        file.transferTo(uploadFile);

        Thumbnails.of(uploadFile).size(190,150).outputFormat("png").toFile();
        File uploadFile = new File(fileName);
        ImageIO.write(thumbnailImage, "jpg", uploadFile);

        s3Client.putObject(new PutObjectRequest(bucket + "/thumbnail", fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        return fileName;

    }
}
