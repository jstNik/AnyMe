package com.example.anyme.ui.composables

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.FloatRange
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.Transition
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target



@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BlurredGlideImage(
   model: Any?,
   contentDescription: String?,
   @FloatRange(from = 0.0) minRatio: Float,
   @FloatRange(from = 0.0) maxRatio: Float,
   blur: Dp,
   modifier: Modifier = Modifier,
   alignment: Alignment = Alignment.Center,
   contentScale: ContentScale = ContentScale.FillBounds,
   alpha: Float = DefaultAlpha,
   colorFilter: ColorFilter? = null,
   loading: Placeholder? = null,
   failure: Placeholder? = null,
   transition: Transition.Factory? = null,
){

   Box(
      modifier = modifier
   ) {

      var scale by remember{ mutableStateOf(contentScale) }
      var background: ImageBitmap? by remember{ mutableStateOf(null) }

      background?.let {
         Image(
            bitmap = it,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            modifier = Modifier
               .fillMaxSize()
               .blur(blur)
         )
      }

      GlideImage(
         model = model,
         contentDescription = contentDescription,
         alignment = alignment,
         contentScale = scale,
         alpha = alpha,
         colorFilter = colorFilter,
         loading = loading,
         failure = failure,
         transition = transition,
         modifier = Modifier
            .fillMaxSize()
      ){

         it.addListener(object: RequestListener<Drawable>{
            override fun onLoadFailed(
               e: GlideException?,
               model: Any?,
               target: Target<Drawable>,
               isFirstResource: Boolean,
            ) = false

            override fun onResourceReady(
               resource: Drawable,
               model: Any,
               target: Target<Drawable>?,
               dataSource: DataSource,
               isFirstResource: Boolean,
            ) = try {
               val image = (resource as BitmapDrawable).bitmap.asImageBitmap()
               val ratio = image.width.toFloat() / image.height.toFloat()
               if(ratio !in minRatio..maxRatio) {
                  background = image
                  scale = if (ratio < 0.4) ContentScale.FillHeight
                  else ContentScale.FillWidth
               }
               false
            } catch (e: Exception) {
               background = null
               scale = contentScale
               true
            }
         })
      }
   }
}