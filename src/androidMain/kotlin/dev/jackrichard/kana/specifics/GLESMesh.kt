package dev.jackrichard.kana.specifics

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class GLESMesh(context: Context, file: String) {
    private var inputStream: BufferedReader? = null
    val numFaces: Int
    val normals: FloatArray
    val textureCoordinates: FloatArray
    val positions: FloatArray

    init {
        val vertices = Vector<Float>()
        val normals = Vector<Float>()
        val textures = Vector<Float>()
        val faces = Vector<String>()

        try {
            inputStream = context.assets.open(file).bufferedReader()

            var line: String?
            while (inputStream!!.readLine().also { line = it } != null) {
                val parts = line!!.split(" ".toRegex()).toTypedArray()
                when (parts[0]) {
                    "v" -> {
                        vertices.add(java.lang.Float.valueOf(parts[1]))
                        vertices.add(java.lang.Float.valueOf(parts[2]))
                        vertices.add(java.lang.Float.valueOf(parts[3]))
                    }
                    "vt" -> {
                        textures.add(java.lang.Float.valueOf(parts[1]))
                        textures.add(java.lang.Float.valueOf(parts[2]))
                    }
                    "vn" -> {
                        normals.add(java.lang.Float.valueOf(parts[1]))
                        normals.add(java.lang.Float.valueOf(parts[2]))
                        normals.add(java.lang.Float.valueOf(parts[3]))
                    }
                    "f" -> {
                        faces.add(parts[1] + "/" + parts[2] + "/" + parts[3])
                    }
                }
                print("e")
            }
        } catch (e: IOException) {
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

        numFaces = faces.size.also { println(it) }
        this.normals = FloatArray(numFaces * 3)
        textureCoordinates = FloatArray(numFaces * 2)
        positions = FloatArray(numFaces * 3)

        var positionIndex = 0
        var normalIndex = 0
        var textureIndex = 0

        for (face in faces) {
            val parts = face.split("/")
            var index = 3 * (parts[0].toShort() - 1)

            positions[positionIndex++] = vertices[index++]
            positions[positionIndex++] = vertices[index++]
            positions[positionIndex++] = vertices[index]
            index = 2 * (parts[1].toShort() - 1)

            textureCoordinates[normalIndex++] = textures[index++]
            textureCoordinates[normalIndex++] = 1 - textures[index]
            index = 3 * (parts[2].toShort() - 1)

            this.normals[textureIndex++] = normals[index++]
            this.normals[textureIndex++] = normals[index++]
            this.normals[textureIndex++] = normals[index]
        }
    }
}