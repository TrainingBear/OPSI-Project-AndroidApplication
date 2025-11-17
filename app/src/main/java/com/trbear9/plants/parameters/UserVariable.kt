package com.trbear9.plants.parameters

import android.content.Context
import android.graphics.Bitmap
import lombok.*

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
