package exercise1

class CashRegister(
    private val prices: Map<String, Double>,
    private val promotions: Map<String, String>
) {

    fun checkout(products: List<String>): Double {
        var total = 0.0

        val productCounts: Map<String, Int> = products.groupingBy { it }.eachCount()

        for ((productName, productCount) in productCounts) {
            val price = prices[productName] ?: continue

            val promotion = promotions[productName]
            if (promotion != null) {
                val (buyQty, payQty) = promotion.split("x").map { it.toInt() }

                val promotionCount = productCount / buyQty
                val remainder = productCount % buyQty

                val totalCount = promotionCount * payQty + remainder
                total += totalCount * price
            } else {
                total += productCount * price
            }
        }

        return total
    }
}