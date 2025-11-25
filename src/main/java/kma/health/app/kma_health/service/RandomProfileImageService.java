package kma.health.app.kma_health.service;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Random;

@Service
public class RandomProfileImageService {

    private static final String PROFILE_PIC_DIR = "classpath:static/images/profile_pictures";

    public String getRandomProfilePicture() {
        try {
            File folder = ResourceUtils.getFile(PROFILE_PIC_DIR);

            File[] files = folder.listFiles((dir, name) ->
                    name.toLowerCase().matches(".*\\.(png|jpg|jpeg|gif)"));

            if (files == null || files.length == 0)
                throw new RuntimeException("No images found in profile_pictures directory");

            Random random = new Random();
            File randomFile = files[random.nextInt(files.length)];

            return "/images/profile_pictures/" + randomFile.getName();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load profile pictures directory", e);
        }
    }
}

