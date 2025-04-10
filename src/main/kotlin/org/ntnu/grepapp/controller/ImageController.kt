package org.ntnu.grepapp.controller

import org.ntnu.grepapp.dto.image.UploadImageResponse
import org.ntnu.grepapp.service.ImageService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

/**
 * REST controller for image-related operations.
 * Provides endpoints for uploading and retrieving images.
 */
@RestController
@RequestMapping("/api/image")
@CrossOrigin(origins = ["http://localhost:5173"])
class ImageController(
    val imageService: ImageService
) {

    /**
     * Uploads an image file to the system.
     *
     * @param file The MultipartFile containing the image data to upload
     * @return ResponseEntity containing an UploadImageResponse with the generated image ID
     */
    @PostMapping("/upload")
    fun upload(@RequestParam file: MultipartFile): ResponseEntity<UploadImageResponse> {
        val id = imageService.save(file.bytes)
        val body = UploadImageResponse(id.toString())
        return ResponseEntity(body, HttpStatus.OK)
    }

    /**
     * Retrieves multiple images by their IDs.
     *
     * @param imageIds A list of image IDs to retrieve
     * @return ResponseEntity containing a map where keys are image IDs and values are base64-encoded image data
     */
    @GetMapping
    fun download(@RequestParam imageIds: List<String>): ResponseEntity<Map<String, String>> {
        val images = imageService.load(imageIds.map { UUID.fromString(it) })
        val map = HashMap<String, String>()
        for (img in images) {
            map[img.id.toString()] = img.buffer
        }
        return ResponseEntity.ok(map)
    }
}