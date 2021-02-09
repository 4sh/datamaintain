package datamaintain.core.step

enum class Step(val executionOrder: Int) {
    SCAN(1),
    FILTER(2),
    SORT(3),
    PRUNE(4),
    CHECK(5),
    EXECUTE(6);

    fun isSameStepOrExecutedBefore(otherStep: Step): Boolean {
        return otherStep.executionOrder >= this.executionOrder
    }
}
