import java.awt.*
import java.awt.AlphaComposite.*
import java.awt.image.*
import java.awt.image.BufferedImage.*
import java.awt.image.Raster.createRaster
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO
import kotlin.math.pow

fun main(args: Array<String>) {
    val images = args.map(::File).map(ImageIO::read).map { it.resetColor() }
    val result = images.firstOrNull()?.let { BufferedImage(it.width, it.height, TYPE_INT_ARGB) }
    val shift = AtomicInteger()
    result
        ?.createGraphics()
        ?.also { it.color = Color.WHITE }
        ?.also { it.fillRect(0, 0, result.width, result.height) }
        ?.also { images.forEach { image -> it.render(image, shift.getAndIncrement(), args.size) }}
        ?.also { ImageIO.write(result, "png", System.out) }
}

fun Graphics2D.render(image: BufferedImage, layer: Int, totalLayer: Int) {
    composite = getInstance(SRC_OVER, .5f.pow(layer))
    drawImage(image.shiftHue(layer.toFloat() / totalLayer), 0, 0, null)
}

fun BufferedImage.shiftHue(offset: Float) :BufferedImage {
    val pixels = (raster.dataBuffer as DataBufferInt).data
        .map { it.shiftHue(offset) }
        .toIntArray()
    return BufferedImage(width, height, TYPE_INT_RGB)
        .also { it.data = createRaster(sampleModel, DataBufferInt(pixels, pixels.size), null) }
}

fun Int.shiftHue(offset: Float) = Color
    .RGBtoHSB(this ushr 16 and 0xFF, this ushr 8 and 0xFF, this and 0xFF, null)
    .also { it[0] += offset }
    .let { Color.HSBtoRGB(it[0], it[1], it[2]) }

fun BufferedImage.resetColor() = BufferedImage(width, height, TYPE_INT_RGB)
    .also { it.graphics.drawImage(this, 0, 0, null) }
