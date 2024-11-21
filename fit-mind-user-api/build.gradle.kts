plugins {
  java
}

repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  implementation("com.bits.ss.fitmind:fit-mind-commons-api:0.0.1-SNAPSHOT")

  implementation(libs.jackson.annotations)
  implementation(libs.apache.commons.lang3)
}