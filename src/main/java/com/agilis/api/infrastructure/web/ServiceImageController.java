package com.agilis.api.infrastructure.web;

import com.agilis.api.application.service.AddServiceImageUseCase;
import com.agilis.api.application.service.RemoveServiceImageUseCase;
import com.agilis.api.application.service.UpdateThumbnailUseCase;
import com.agilis.api.domain.service.ServiceImage;
import com.agilis.api.domain.service.ServiceImageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/services/{serviceId}")
public class ServiceImageController {

    private final AddServiceImageUseCase addServiceImageUseCase;
    private final RemoveServiceImageUseCase removeServiceImageUseCase;
    private final UpdateThumbnailUseCase updateThumbnailUseCase;
    private final ServiceImageRepository serviceImageRepository;

    public ServiceImageController(
            AddServiceImageUseCase addServiceImageUseCase,
            RemoveServiceImageUseCase removeServiceImageUseCase,
            UpdateThumbnailUseCase updateThumbnailUseCase,
            ServiceImageRepository serviceImageRepository
    ) {
        this.addServiceImageUseCase    = addServiceImageUseCase;
        this.removeServiceImageUseCase = removeServiceImageUseCase;
        this.updateThumbnailUseCase    = updateThumbnailUseCase;
        this.serviceImageRepository    = serviceImageRepository;
    }

    @GetMapping("/images")
    public ResponseEntity<List<ServiceImage>> getImages(@PathVariable String serviceId) {
        return ResponseEntity.ok(
                serviceImageRepository.findAllByServiceId(UUID.fromString(serviceId))
        );
    }

    @PostMapping("/images")
    public ResponseEntity<AddServiceImageUseCase.Output> addImage(
            @PathVariable String serviceId,
            @RequestBody AddServiceImageUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        return ResponseEntity.ok(addServiceImageUseCase.execute(
                new AddServiceImageUseCase.Input(requesterId, serviceId, input.url())
        ));
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> removeImage(
            @PathVariable String imageId
    ) {
        String requesterId = getCurrentUserId();
        removeServiceImageUseCase.execute(
                new RemoveServiceImageUseCase.Input(requesterId, imageId)
        );
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/thumbnail")
    public ResponseEntity<UpdateThumbnailUseCase.Output> updateThumbnail(
            @PathVariable String serviceId,
            @RequestBody UpdateThumbnailUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        return ResponseEntity.ok(updateThumbnailUseCase.execute(
                new UpdateThumbnailUseCase.Input(requesterId, serviceId, input.url())
        ));
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}