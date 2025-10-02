package com.trbear9.plants.api

import android.content.Context
import android.graphics.Bitmap
import lombok.*
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

@Getter
@AllArgsConstructor
@NoArgsConstructor
class UserVariable {
    @Setter
    private var tanah: String? = null

    //parameters
    var soil: SoilParameters = SoilParameters()
    var geo: GeoParameters = GeoParameters()
    var custom: CustomParameters? = null

    var image: Bitmap? = null
    var context: Context? = null
    var hash: String? = null
}
