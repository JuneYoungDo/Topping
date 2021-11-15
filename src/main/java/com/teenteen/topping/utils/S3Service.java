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
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        String fileName = System.currentTimeMillis() + "a";

        ObjectMetadata metadata = new ObjectMetadata();
        byte[] bytes = IOUtils.toByteArray(file.getInputStream());
        metadata.setContentLength(bytes.length);
        s3Client.putObject(new PutObjectRequest(bucket + "/input", fileName, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        return fileName;
    }

    public List<String> uploadVideoWithThumbnail(MultipartFile file) throws IOException, JCodecException {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();

        //---------동영상 업로드---------
        String fileName = System.currentTimeMillis() + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        byte[] bytes = IOUtils.toByteArray(file.getInputStream());
        metadata.setContentLength(bytes.length);
        s3Client.putObject(new PutObjectRequest(bucket + "/input", fileName, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        //--------썸네일 업로드----------
        String thumbnailName = fileName + ".png";
        File originalFile = convert(file);
        int frameNumber = 0;
        Picture picture = FrameGrab.getFrameFromFile(originalFile, frameNumber);
        BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
        File thumbnail = new File(thumbnailName);
        ImageIO.write(bufferedImage, "png", thumbnail);
        s3Client.putObject(new PutObjectRequest(bucket + "/thumbnail", thumbnailName, thumbnail)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        thumbnail.delete();
        originalFile.delete();

        return new ArrayList(Arrays.asList(fileName, "thumbnail/" + thumbnailName));
    }

    public File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

}
