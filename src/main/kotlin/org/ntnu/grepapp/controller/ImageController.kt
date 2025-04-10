package org.ntnu.grepapp.controller


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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
@Tag(name = "Images", description = "API for uploading and retrieving images")
class ImageController(
    val imageService: ImageService
) {

    /**
     * Uploads an image file to the system.
     *
     * @param file The MultipartFile containing the image data to upload
     * @return ResponseEntity containing an UploadImageResponse with the generated image ID
     */
    @Operation(
        summary = "Upload an image",
        description = "Uploads an image file to the system and returns its generated ID"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Image successfully uploaded",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = UploadImageResponse::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid image file"
        )
    ])
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
    @Operation(
        summary = "Download images",
        description = "Retrieves multiple images by their IDs as base64-encoded strings"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Images successfully retrieved",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = Map::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid image IDs"
        )
    ])
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