import org.jline.console.Printer

class CustomPrinter : Printer {
    private val stringBuilder = StringBuilder()

    override fun println(p0: Any?) {
        if (p0 != null) {
            stringBuilder.append(p0.toString())
            stringBuilder.append("\n")
        }
    }

    override fun println(options: MutableMap<String, Any>?, `object`: Any?) {
        // You can customize this method based on the options provided
        println(`object`)
    }

    override fun refresh(): Boolean {
        // Return true if the refresh operation is successful, false otherwise
        // In this example, we don't need to do anything, so we return true
        return true
    }

    fun getOutput(): String {
        return stringBuilder.toString()
    }

    fun clearOutput() {
        stringBuilder.clear()
    }
}
