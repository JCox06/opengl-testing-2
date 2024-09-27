package uk.co.jcox.gl

class Engine {
    private val windowManager: WindowManager = WindowManager()
    private val renderer: Renderer = Renderer()
    private var isRunning: Boolean = false

    fun start() {
        windowManager.init(3, 3)
        renderer.setupRendering(false)
        renderer.setupDefaultProgram()
        isRunning = true
        loop()
        end()
    }

    private fun loop() {
        while (isRunning) {

            gameRender()
            imGuiRender()
            update()

            renderBuffer()
            if (windowManager.shouldClose()) {
                isRunning = false
            }
        }
    }

    private fun end() {
        renderer.close()
        windowManager.close()
    }

    private fun renderBuffer() {
        windowManager.swapBuffers()
        windowManager.pollEvents()
    }

    private fun gameRender() {

    }

    private fun imGuiRender() {

    }

    private fun update() {

    }
}