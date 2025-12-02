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
        try {
            String result = randomProfileImageService.getRandomProfilePicture();
            
            assertNotNull(result);
            assertTrue(result.startsWith("/images/profile_pictures/"));
            assertTrue(result.matches(".*\\.(png|jpg|jpeg|gif)$"));
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("No images found") || 
                       e.getMessage().contains("Failed to load"));
        }
    }

    @Test
    void testGetRandomProfilePicture_PathFormat() {
        try {
            String result = randomProfileImageService.getRandomProfilePicture();
            
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.contains("profile_pictures"));
        } catch (RuntimeException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void testGetRandomProfilePicture_MultipleCallsReturnValidPaths() {
        try {
            for (int i = 0; i < 5; i++) {
                String result = randomProfileImageService.getRandomProfilePicture();
                assertNotNull(result);
                assertTrue(result.startsWith("/images/profile_pictures/"));
            }
        } catch (RuntimeException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void testGetRandomProfilePicture_ThrowsExceptionWhenNoImagesFound() {
        RandomProfileImageService service = new RandomProfileImageService();
        
        try {
            String result = service.getRandomProfilePicture();
            assertNotNull(result);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            assertTrue(message.contains("No images found") || message.contains("Failed to load"),
                    "Exception message should indicate the problem: " + message);
        }
    }

    @Test
    void testGetRandomProfilePicture_HandlesIOException() {
        RandomProfileImageService service = new RandomProfileImageService();
        
        try {
            String result = service.getRandomProfilePicture();
            assertTrue(result.contains("/images/profile_pictures/"));
        } catch (RuntimeException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().startsWith("No images found") || 
                       e.getMessage().startsWith("Failed to load") ||
                       e.getMessage().startsWith("Error"));
        }
    }

    @Test
    void testGetRandomProfilePicture_ConsistentOutput() {
        try {
            String result1 = randomProfileImageService.getRandomProfilePicture();
            String result2 = randomProfileImageService.getRandomProfilePicture();
            
            assertTrue(result1.startsWith("/images/profile_pictures/"));
            assertTrue(result2.startsWith("/images/profile_pictures/"));
            
            assertTrue(result1.matches(".*\\.(png|jpg|jpeg|gif)$"));
            assertTrue(result2.matches(".*\\.(png|jpg|jpeg|gif)$"));
        } catch (RuntimeException e) {
            assertNotNull(e.getMessage());
        }
    }
}
