package dev.jackrichard.kana.specifics

import android.content.Context
import java.io.BufferedReader
import java.io.IOException

class GLESMesh(context: Context, file: String) {
    private var inputStream: BufferedReader? = null

    val faceCount: Int

    val normals: FloatArray
    val textureCoordinates: FloatArray
    val positions: FloatArray

    var hasTextures: Boolean = false

    init {
        val vertices = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
        val textures = mutableListOf<Float>()
        val faces = mutableListOf<Short>()

        try {
            inputStream = context.assets.open(file).bufferedReader()
            inputStream!!.readLines().forEach { line ->
                val parts = line.trim().split("\\s+".toRegex())
                when (parts[0]) {
                    "v" -> { vertices.addAll(parts.drop(1).map { it.toFloat() }) }
                    "vt" -> { textures.addAll(parts.drop(1).map { it.toFloat() }); hasTextures = true }
                    "vn" -> { normals.addAll(parts.drop(1).map { it.toFloat() }) }
                    "f" -> { faces.addAll(parts.drop(1).map { it.toShort() }) }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw Exception("Could not load model!")
        } finally {
            if (inputStream != null) {
                try {
                    inputStream!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                    throw Exception("Could not load model!")
                }
            }
        }

        this.faceCount = faces.size

        this.normals = FloatArray(faceCount * 3)
        this.textureCoordinates = FloatArray(faceCount * 2)
        this.positions = FloatArray(faceCount * 3)

        var positionIndex = 0
        var normalIndex = 0
        var textureIndex = 0

        for (parts in faces.chunked(3)) {
            var index = 3 * (parts[0] - 1)

            this.positions[positionIndex++] = vertices[index++]
            this.positions[positionIndex++] = vertices[index++]
            this.positions[positionIndex++] = vertices[index]
            index = 2 * (parts[1] - 1)

            if (hasTextures) {
                this.textureCoordinates[normalIndex++] = textures[index++]
                this.textureCoordinates[normalIndex++] = 1 - textures[index]
            }
            index = 3 * (parts[2] - 1)

            this.normals[textureIndex++] = normals[index++]
            this.normals[textureIndex++] = normals[index++]
            this.normals[textureIndex++] = normals[index]
        }
    }
}