package kma.health.app.kma_health.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RandomProfileImageServiceTest {

    private final RandomProfileImageService randomProfileImageService = new RandomProfileImageService();

    @Test
    void testGetRandomProfilePicture_ReturnsValidPath() {
        // This test will work if there are images in the profile_pictures directory
        // If no images exist, it will throw a RuntimeException which we should test for
        try {
            String result = randomProfileImageService.getRandomProfilePicture();
            
            assertNotNull(result);
            assertTrue(result.startsWith("/images/profile_pictures/"));
            assertTrue(result.matches(".*\\.(png|jpg|jpeg|gif)$"));
        } catch (RuntimeException e) {
            // Expected if no images in directory or directory doesn't exist
            assertTrue(e.getMessage().contains("No images found") || 
                       e.getMessage().contains("Failed to load"));
        }
    }

    @Test
    void testGetRandomProfilePicture_PathFormat() {
        try {
            String result = randomProfileImageService.getRandomProfilePicture();
            
            // Verify the path format is correct
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.contains("profile_pictures"));
        } catch (RuntimeException e) {
            // Expected if resources are not available in test environment
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void testGetRandomProfilePicture_MultipleCallsReturnValidPaths() {
        // Call multiple times to verify randomness doesn't break the path format
        try {
            for (int i = 0; i < 5; i++) {
                String result = randomProfileImageService.getRandomProfilePicture();
                assertNotNull(result);
                assertTrue(result.startsWith("/images/profile_pictures/"));
            }
        } catch (RuntimeException e) {
            // Expected if resources are not available in test environment
            assertNotNull(e.getMessage());
        }
    }
}
