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
  apply(plugin = "maven-publish")
  apply(plugin = "com.diffplug.spotless")

  repositories {
    mavenLocal()
    mavenCentral()
  }

  java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_17
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

  configure<PublishingExtension> {
    publications {
      create<MavenPublication>("main") {
        from(components["java"])
      }
    }
  }

  tasks.test { useJUnitPlatform() }
}
