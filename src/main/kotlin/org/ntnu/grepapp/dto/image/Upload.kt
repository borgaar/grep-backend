package org.ntnu.grepapp.dto.image

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Response after successfully uploading an image")
data class UploadImageResponse(
    @Schema(description = "ID of the uploaded image", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    val id: String
)