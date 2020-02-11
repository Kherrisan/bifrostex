allprojects {
    group = "cn.kherrisan.bifrostex"
    version = "1.0-SNAPSHOT"
}

tasks.create("doc") {

}.doFirst {
    println("Start to generate doc")
}.doLast {
    println("Generated doc")
}