package org.ntnu.grepapp.controller

import org.ntnu.grepapp.dto.image.UploadImageResponse
import org.ntnu.grepapp.service.ImageService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api/image")
@CrossOrigin(origins = ["http://localhost:5173"])
class ImageController(
    val imageService: ImageService
) {

    @PostMapping("/upload")
    fun upload(@RequestParam file: MultipartFile): ResponseEntity<UploadImageResponse> {
        val id = imageService.save(file.bytes)
        val body = UploadImageResponse(id.toString())
        return ResponseEntity(body, HttpStatus.OK)
    }

    @GetMapping
    fun download(@RequestParam imageIds: List<String>): ResponseEntity<Map<String, ByteArray>> {
        val images = imageService.load(imageIds.map { UUID.fromString(it) })
        val map = HashMap<String, ByteArray>()
        for (img in images) {
            map[img.id.toString()] = img.buffer
        }
        return ResponseEntity.ok(map)
    }
}