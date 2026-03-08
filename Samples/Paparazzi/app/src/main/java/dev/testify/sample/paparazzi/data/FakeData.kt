package dev.testify.sample.paparazzi.data

val categories = listOf("All", "Coffee", "Tea", "Pastry", "Seasonal")

val products = listOf(
    Product(
        id = 1,
        name = "Espresso",
        description = "A concentrated shot of coffee brewed by forcing hot water under pressure through finely ground beans. Bold, rich, and full-bodied.",
        category = "Coffee",
        price = 3.50,
        rating = 4.5f,
        imageUrl = "https://example.com/espresso.jpg"
    ),
    Product(
        id = 2,
        name = "Cappuccino",
        description = "Equal parts espresso, steamed milk, and milk foam. A classic Italian coffee drink with a velvety texture.",
        category = "Coffee",
        price = 4.75,
        rating = 4.0f,
        imageUrl = "https://example.com/cappuccino.jpg"
    ),
    Product(
        id = 3,
        name = "Matcha Latte",
        description = "Premium ceremonial-grade matcha whisked with steamed milk. Earthy, smooth, and naturally energizing.",
        category = "Tea",
        price = 5.25,
        rating = 4.5f,
        imageUrl = "https://example.com/matcha.jpg"
    ),
    Product(
        id = 4,
        name = "Chai Spice Tea",
        description = "A warming blend of black tea, cinnamon, cardamom, ginger, and cloves. Served with steamed milk.",
        category = "Tea",
        price = 4.50,
        rating = 4.0f,
        imageUrl = "https://example.com/chai.jpg"
    ),
    Product(
        id = 5,
        name = "Almond Croissant",
        description = "Flaky butter croissant filled with almond cream and topped with sliced almonds and powdered sugar.",
        category = "Pastry",
        price = 4.25,
        rating = 5.0f,
        imageUrl = "https://example.com/croissant.jpg"
    ),
    Product(
        id = 6,
        name = "Blueberry Muffin",
        description = "A moist and tender muffin loaded with fresh blueberries and topped with a crunchy streusel.",
        category = "Pastry",
        price = 3.75,
        rating = 3.5f,
        imageUrl = "https://example.com/muffin.jpg"
    ),
    Product(
        id = 7,
        name = "Pumpkin Spice Latte",
        description = "Espresso with steamed milk, pumpkin puree, and warm spices topped with whipped cream. A seasonal favorite.",
        category = "Seasonal",
        price = 5.75,
        rating = 4.5f,
        imageUrl = "https://example.com/psl.jpg"
    ),
    Product(
        id = 8,
        name = "Peppermint Mocha",
        description = "Rich chocolate and espresso blended with peppermint syrup and steamed milk. Finished with whipped cream.",
        category = "Seasonal",
        price = 5.50,
        rating = 4.0f,
        imageUrl = "https://example.com/peppermint.jpg"
    )
)

val reviews = listOf(
    Review(
        reviewerName = "Alice M.",
        comment = "Absolutely love the espresso here. Best in town!",
        rating = 5.0f
    ),
    Review(
        reviewerName = "Bob K.",
        comment = "Great atmosphere and the pastries are always fresh.",
        rating = 4.0f
    ),
    Review(
        reviewerName = "Carol S.",
        comment = "The matcha latte is perfectly balanced. Will be back!",
        rating = 4.5f
    ),
    Review(
        reviewerName = "David R.",
        comment = "Good coffee but the seasonal drinks could use more flavor.",
        rating = 3.5f
    )
)
