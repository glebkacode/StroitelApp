plugins {
    id("org.jetbrains.kotlinx.kover")
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "*Graph*",
                    "*Factory*",
                    "*_*",
                    "*Component*Impl*Factory*"
                )
                packages(
                    "*.di",
                    "*.generated"
                )
            }
        }
    }
}
