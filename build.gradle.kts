plugins {
  java
  id("com.diffplug.spotless") version "6.25.0"
}

allprojects {
  group = "com.bits.ss.fitmind"
  version = "0.0.1-SNAPSHOT"
}

subprojects {
  apply(plugin = "java")
  apply(plugin = "com.diffplug.spotless")

  repositories {
    mavenLocal()
    mavenCentral()
  }

  spotless {
    format("misc") {
      target("*.md")
      trimTrailingWhitespace()
      endWithNewline()
    }
    java {
      toggleOffOn()
      googleJavaFormat().reflowLongStrings()
    }
    kotlinGradle { ktfmt().googleStyle() }
  }

  tasks.test { useJUnitPlatform() }
}
