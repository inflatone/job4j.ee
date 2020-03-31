package ru.job4j.auto.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.auto.model.Image;
import ru.job4j.auto.service.ImageService;
import ru.job4j.auto.to.ImageTo;
import ru.job4j.auto.web.converter.UrlConverter;

import java.io.IOException;
import java.net.URI;
import java.util.function.BiFunction;
import java.util.function.Function;

@Controller
@RequestMapping(ImageController.URL)
@RequiredArgsConstructor
public class ImageController {
    public static final String URL = "/images";

    private final ImageService service;

    private final UrlConverter urlConverter;

    @GetMapping({"/", "/{id}"})
    public ResponseEntity<byte[]> findImage(@PathVariable(required = false) Integer id) {
        return buildImageDataResponse(service.find(id));
    }

    @PostMapping("/profile")
    public ResponseEntity<ImageTo> uploadToUser(@RequestParam MultipartFile userPhoto,
                                                @AuthenticationPrincipal AuthorizedUser auth) throws IOException {
        return upload(userPhoto, img -> service.uploadToUser(auth.id(), img),
                (img, url) -> new ImageTo("userPhoto", false, url, urlConverter.userImageModifiableUrl(null)));
    }

    @PostMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ImageTo> uploadToUser(@PathVariable Integer id,
                                                @RequestParam MultipartFile userPhoto) throws IOException {
        return upload(userPhoto, img -> service.uploadToUser(id, img),
                (img, url) -> new ImageTo("userPhoto", false, url, urlConverter.userImageModifiableUrl(id)));
    }

    @PostMapping("/profile/posts/{id}")
    public ResponseEntity<ImageTo> uploadToPost(@PathVariable Integer id,
                                                @RequestParam MultipartFile postPhoto,
                                                @AuthenticationPrincipal AuthorizedUser auth) throws IOException {
        return upload(postPhoto, img -> service.uploadToPost(id, auth.id(), img),
                (img, url) -> new ImageTo("postPhoto", false, url, urlConverter.postImageModifiableUrl(null, id)));
    }

    @PostMapping("/users/{profileId}/posts/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ImageTo> uploadToPost(@PathVariable Integer id, @PathVariable Integer profileId,
                                                @RequestParam MultipartFile postPhoto) throws IOException {
        return upload(postPhoto, img -> service.uploadToPost(id, profileId, img),
                (img, url) -> new ImageTo("postPhoto", false, url, urlConverter.postImageModifiableUrl(profileId, id)));
    }

    @DeleteMapping("/profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthorizedUser auth) {
        service.deleteFromUser(auth.id());
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(@PathVariable Integer id) {
        service.deleteFromUser(id);
    }

    @DeleteMapping("/profile/posts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFromPost(@PathVariable int id) {
        deleteFromPost(id, SecurityHelper.authUserId());
    }

    @DeleteMapping("/users/{profileId}/posts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteFromPost(@PathVariable Integer id, @PathVariable Integer profileId) {
        service.deleteFromPost(id, profileId);
    }

    private ResponseEntity<byte[]> buildImageDataResponse(Image image) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .header("Content-Disposition", "inline; filename=\"" + image.getFileName() + '"')
                .body(image.getData());
    }

    private ResponseEntity<ImageTo> upload(MultipartFile file,
                                           Function<Image, Image> imageUploader,
                                           BiFunction<Image, URI, ImageTo> mapper) throws IOException {
        Image uploaded = imageUploader.apply(retrieveImage(file));
        URI uriOfNewResource = urlConverter.imageUrl(uploaded.getId());
        return ResponseEntity.created(uriOfNewResource).body(mapper.apply(uploaded, uriOfNewResource));
    }

    private Image retrieveImage(MultipartFile file) throws IOException {
        Image image = null;
        if (!file.isEmpty()) {
            var contentType = file.getContentType();
            image = new Image(null, file.getOriginalFilename(),
                    contentType == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : contentType,
                    file.getBytes());
        }
        return image;
    }
}
