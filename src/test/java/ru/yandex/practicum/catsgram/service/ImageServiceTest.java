package ru.yandex.practicum.catsgram.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.yandex.practicum.catsgram.dal.ImageRepository;
import ru.yandex.practicum.catsgram.dto.image.ImageDto;
import ru.yandex.practicum.catsgram.dto.post.PostDto;
import ru.yandex.practicum.catsgram.exception.ImageFileException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.ImageData;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private PostService postService;

    @InjectMocks
    private ImageService imageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageService, "imageDirectory", tempDir.toString());
    }

    @Test
    void getPostImages_returnsImagesForPost() {
        Image img = new Image();
        img.setId(1L);
        img.setPostId(10L);
        img.setOriginalFileName("cat.jpg");
        PostDto post = new PostDto();
        post.setId(10L);
        when(postService.findById(10L)).thenReturn(post);
        when(imageRepository.findByPostId(10L)).thenReturn(List.of(img));
        List<ImageDto> result = imageService.getPostImages(10L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOriginalFileName()).isEqualTo("cat.jpg");
    }

    @Test
    void getPostImages_whenPostNotFound_propagatesNotFoundException() {
        when(postService.findById(99L)).thenThrow(new NotFoundException("not found"));
        assertThatThrownBy(() -> imageService.getPostImages(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void saveImages_whenFileIsEmpty_throwsParameterNotValidException() {
        PostDto post = new PostDto();
        post.setId(5L);
        post.setAuthorId(1L);
        when(postService.findById(5L)).thenReturn(post);
        MockMultipartFile emptyFile = new MockMultipartFile("image", "photo.jpg", "image/jpeg", new byte[0]);
        assertThatThrownBy(() -> imageService.saveImages(5L, List.of(emptyFile)))
                .isInstanceOf(ParameterNotValidException.class);
    }

    @Test
    void saveImages_whenFileHasNoExtension_throwsParameterNotValidException() {
        PostDto post = new PostDto();
        post.setId(5L);
        post.setAuthorId(1L);
        when(postService.findById(5L)).thenReturn(post);
        MockMultipartFile file = new MockMultipartFile("image", "photoNoExt", "image/jpeg", new byte[]{1});
        assertThatThrownBy(() -> imageService.saveImages(5L, List.of(file)))
                .isInstanceOf(ParameterNotValidException.class);
    }

    @Test
    void saveImages_whenFileHasInvalidExtension_throwsParameterNotValidException() {
        PostDto post = new PostDto();
        post.setId(5L);
        post.setAuthorId(1L);
        when(postService.findById(5L)).thenReturn(post);
        MockMultipartFile file = new MockMultipartFile("image", "photo.pdf", "image/jpeg", new byte[]{1});
        assertThatThrownBy(() -> imageService.saveImages(5L, List.of(file)))
                .isInstanceOf(ParameterNotValidException.class);
    }

    @Test
    void saveImages_whenContentTypeIsInvalid_throwsParameterNotValidException() {
        PostDto post = new PostDto();
        post.setId(5L);
        post.setAuthorId(1L);
        when(postService.findById(5L)).thenReturn(post);
        MockMultipartFile file = new MockMultipartFile("image", "photo.jpg", "application/pdf", new byte[]{1});
        assertThatThrownBy(() -> imageService.saveImages(5L, List.of(file)))
                .isInstanceOf(ParameterNotValidException.class);
    }

    @Test
    void saveImages_whenValidFile_savesAndReturnsDto() {
        PostDto post = new PostDto();
        post.setId(5L);
        post.setAuthorId(1L);
        when(postService.findById(5L)).thenReturn(post);
        doAnswer(invocation -> {
            Image img = invocation.getArgument(0);
            img.setId(100L);
            return img;
        }).when(imageRepository).save(any(Image.class));
        MockMultipartFile file = new MockMultipartFile(
                "image", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3}
        );
        List<ImageDto> result = imageService.saveImages(5L, List.of(file));
        assertThat(result).hasSize(1);
    }

    @Test
    void saveImages_whenDbFails_deletesUploadedFile() {
        PostDto post = new PostDto();
        post.setId(5L);
        post.setAuthorId(1L);
        when(postService.findById(5L)).thenReturn(post);
        when(imageRepository.save(any())).thenThrow(new RuntimeException("DB error"));
        MockMultipartFile file = new MockMultipartFile(
                "image", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3}
        );
        assertThatThrownBy(() -> imageService.saveImages(5L, List.of(file)))
                .isInstanceOf(RuntimeException.class);
        Path authorDir = tempDir.resolve("1").resolve("5");
        if (Files.exists(authorDir)) {
            assertThat(authorDir.toFile().listFiles()).isEmpty();
        }
    }

    @Test
    void getImageData_whenImageNotFound_throwsNotFoundException() {
        when(imageRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> imageService.getImageData(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getImageData_whenFileExists_returnsData() throws Exception {
        Path filePath = tempDir.resolve("test.jpg");
        Files.write(filePath, new byte[]{10, 20, 30});
        Image image = new Image();
        image.setId(1L);
        image.setPostId(1L);
        image.setOriginalFileName("test.jpg");
        image.setFilePath(filePath.toString());
        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));
        ImageData result = imageService.getImageData(1L);
        assertThat(result.getData()).containsExactly(10, 20, 30);
        assertThat(result.getName()).isEqualTo("test.jpg");
    }

    @Test
    void getImageData_whenFileDoesNotExist_throwsImageFileException() {
        Image image = new Image();
        image.setId(1L);
        image.setOriginalFileName("missing.jpg");
        image.setFilePath(tempDir.resolve("missing.jpg").toString());
        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));
        assertThatThrownBy(() -> imageService.getImageData(1L))
                .isInstanceOf(ImageFileException.class);
    }
}
