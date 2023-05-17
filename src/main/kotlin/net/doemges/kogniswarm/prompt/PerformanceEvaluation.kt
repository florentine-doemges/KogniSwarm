package net.doemges.kogniswarm.prompt

data class PerformanceEvaluation(val text: String){
    override fun toString(): String = text
}

@Suppress("MemberVisibilityCanBePrivate")
class PerformanceEvaluations private constructor() {
    companion object {

        fun continuousReview() =
            PerformanceEvaluation("Continuously review and analyze your actions to ensure you are performing to the best of your abilities.")


        fun constructiveSelfCriticism() =
            PerformanceEvaluation("Constructively self-criticize your big-picture behavior constantly.")


        fun smartAndEfficient() =
            PerformanceEvaluation("Every command has a cost, so be smart and efficient. Aim to complete tasks in the least number of steps.")

        fun reflectPastDecisions(): PerformanceEvaluation =
            PerformanceEvaluation("Reflect on past decisions and strategies to refine your approach.")

        fun writeAllCodeToFile(): PerformanceEvaluation = PerformanceEvaluation("Write all code to a file.")

        fun standardPerformanceEvaluations(): List<PerformanceEvaluation> = listOf(
            continuousReview(),
            constructiveSelfCriticism(),
            reflectPastDecisions(),
            smartAndEfficient(),
            writeAllCodeToFile()
        )
    }
}