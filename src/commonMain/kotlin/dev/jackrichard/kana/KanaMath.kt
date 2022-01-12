package dev.jackrichard.kana

import kotlin.reflect.KClass

open class KanaShaderDataType

/*
    Vector
 */
fun vec2(a: Float, b: Float) = Vec2(a, b)
class Vec2 internal constructor(val a: Float, val b: Float) : KanaShaderDataType() {
    operator fun plus(c: Float) : Vec3 = Vec3(a, b, c)
    operator fun plus(cd: Vec2) : Vec4 = Vec4(a, b, cd.a, cd.b)
}

fun vec3(a: Float, b: Float, c: Float) = Vec3(a, b, c)
class Vec3 internal constructor(val a: Float, val b: Float, val c: Float) : KanaShaderDataType() { operator fun plus(d: Float) : Vec4 = Vec4(a, b, c, d) }
operator fun Float.plus(b: Vec2) = Vec3(this, b.a, b.b)

fun vec4(a: Float, b: Float, c: Float, d: Float) = Vec4(a, b, c, d)
class Vec4 internal constructor(val a: Float, val b: Float, val c: Float, val d: Float) : KanaShaderDataType()
operator fun Float.plus(b: Vec3) = Vec4(this, b.a, b.b, b.c)


/*
    Matrix
 */
fun mat2(a: Float, b: Float, c: Float, d: Float) = Mat2(a, b, c, d)
class Mat2 internal constructor(
    val a: Float, val b: Float,
    val c: Float, val d: Float
) {
    operator fun times(e: Float) : Mat2 = mat2(
        a * e, b * e,
        c * e, d * e
    )

    operator fun times(e: Mat2) : Mat2 = mat2(
        (a * e.a) + (b * e.c), (a * e.b) + (b * e.d),
        (c * e.a) + (d * e.c), (c * e.b) + (d * e.d)
    )

    companion object {
        val identity: Mat2 = mat2(
            1F, 0F,
            0F, 1F
        )
    }
}

fun mat3(a: Float, b: Float, c: Float, d: Float, e: Float, f: Float, g: Float, h: Float, i: Float) = Mat3(a, b, c, d, e, f, g, h, i)
class Mat3 internal constructor(
    val a: Float, val b: Float, val c: Float,
    val d: Float, val e: Float, val f: Float,
    val g: Float, val h: Float, val i: Float
) {
    operator fun times(j: Float) : Mat3 = mat3(
        a * j, b * j, c * j,
        d * j, e * j, f * j,
        g * j, h * j, i * j
    )

    // TODO: matrix 3x3 multiplied by matrix 3x3

    companion object {
        val identity: Mat3 = mat3(
            1F, 0F, 0F,
            0F, 1F, 0F,
            0F, 0F, 1F
        )
    }
}

fun mat4(a: Float, b: Float, c: Float, d: Float, e: Float, f: Float, g: Float, h: Float, i: Float, j: Float, k: Float, l: Float, m: Float, n: Float, o: Float, p: Float) = Mat4(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
class Mat4 internal constructor(
    val a: Float, val b: Float, val c: Float, val d: Float,
    val e: Float, val f: Float, val g: Float, val h: Float,
    val i: Float, val j: Float, val k: Float, val l: Float,
    val m: Float, val n: Float, val o: Float, val p: Float
) {
    operator fun times(q: Float) : Mat4 = mat4(
            this.a * q, this.b * q, this.c * q, this.d * q,
            this.e * q, this.f * q, this.g * q, this.h * q,
            this.i * q, this.j * q, this.k * q, this.l * q,
           this.m * q, this.n * q, this.o * q, this.p * q
    )

    operator fun times(q: Mat4) : Mat4 = mat4(
        (this.a * q.a) + (this.b * q.e) + (this.c * q.i) + (this.d * q.m), (this.a * q.b) + (this.b * q.f) + (this.c * q.j) + (this.d * q.n), (this.a * q.c) + (this.b * q.g) + (this.c * q.k) + (this.d * q.o), (this.a * q.d) + (this.b * q.h) + (this.c * q.l) + (this.d * q.p),
        (this.e * q.a) + (this.f * q.e) + (this.g * q.i) + (this.h * q.m), (this.e * q.b) + (this.f * q.f) + (this.g * q.j) + (this.h * q.n), (this.e * q.c) + (this.f * q.g) + (this.g * q.k) + (this.h * q.o), (this.e * q.d) + (this.f * q.h) + (this.g * q.l) + (this.h * q.p),
        (this.i * q.a) + (this.j * q.e) + (this.k * q.i) + (this.l * q.m), (this.i * q.b) + (this.j * q.f) + (this.k * q.j) + (this.l * q.n), (this.i * q.c) + (this.j * q.g) + (this.k * q.k) + (this.l * q.o), (this.i * q.d) + (this.j * q.h) + (this.k * q.l) + (this.l * q.p),
        (this.m * q.a) + (this.n * q.e) + (this.o * q.i) + (this.p * q.m), (this.m * q.b) + (this.n * q.f) + (this.o * q.j) + (this.p * q.n), (this.m * q.c) + (this.n * q.g) + (this.o * q.k) + (this.p * q.o), (this.m * q.d) + (this.n * q.h) + (this.o * q.l) + (this.p * q.p)
    )

    fun translate(vec: Vec3) : Mat4 =
        mat4(
            a, b, c, vec.a + d,
            e, f, g, vec.b + h,
            i, j, k, vec.c + l,
            m, n, o, p
        )

    fun scale(vec: Vec3) : Mat4 =
        mat4(
            vec.a * a, b, c, d,
            e, vec.b * b, g, h,
            i, j, vec.c * c, l,
            m, n, o, p
        )

    companion object {
        val identity: Mat4 = mat4(
            1F, 0F, 0F, 0F,
            0F, 1F, 0F, 0F,
            0F, 0F, 1F, 0F,
            0F, 0F, 0F, 1F
        )
    }
}

infix fun Number.v(n: Number) : Vec2 = vec2(this.toFloat(), n.toFloat())
infix fun Vec2.v(n: Number) : Vec3 = vec3(this.a, this.b, n.toFloat())

/*
    Descriptors
 */

class VertexDescriptorElement(n: String, s: Int, t: KClass<*>) {
    companion object {
        inline fun <reified T : KanaShaderDataType> createNew(n: String, s: Int) : VertexDescriptorElement = VertexDescriptorElement(n, s, T::class)
    }

    val type: KClass<*> = t
    val name: String = n
    val size: Int = s
}

class VertexDescriptor {
    internal val elements: MutableList<VertexDescriptorElement> = mutableListOf()

    infix fun vec2(name: String) = elements.add(VertexDescriptorElement.createNew<Vec2>(name, 2))
    infix fun vec3(name: String) = elements.add(VertexDescriptorElement.createNew<Vec3>(name, 3))
    infix fun vec4(name: String) = elements.add(VertexDescriptorElement.createNew<Vec4>(name, 4))
}

fun defineDescriptor(block: VertexDescriptor.() -> Unit) : VertexDescriptor = VertexDescriptor().also(block)

/*
    Floats and buffers
 */

expect class BufferedData
expect fun FloatArray.buffered() : BufferedData