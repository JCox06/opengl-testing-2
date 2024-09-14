package uk.co.jcox.gl

import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.plus
import org.joml.times

class CameraBasic3D (
    val position: Vector3f = Vector3f(0.0f, 0.0f, 10.0f),
    val forwardDirection: Vector3f = Vector3f(0.0f, 0.0f, -1.0f),
    val up: Vector3f = Vector3f(0.0f, 1.0f, 0.0f),
    private val zNear: Float = 0.1f,
    private val zFar: Float = 100.0f,
) {

    var sideDirection = Vector3f()
        private set

    var cameraFov = 45.0
        set(value) {
            if (value > 90.0) {
                field = 90.0
            }
            if (value < 45.0) {
                field = 45.0
            }
        }

    fun updateCameraCoordinateSystem() {
        //To update positiveX direction
        //Cross WorldUP with CameraNZ
        forwardDirection.normalize()
        sideDirection = forwardDirection.cross(up, Vector3f()).normalize()
    }


    fun calculateCamMatrix(aspectRatio: Float): Matrix4f {
        val viewMatrix = Matrix4f().lookAt(position, position + forwardDirection, up)
        val projection = Matrix4f().perspective(Math.toRadians(cameraFov).toFloat(), aspectRatio, zNear, zFar)
        return projection * viewMatrix
    }
}