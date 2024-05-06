main:
	kotlinc miximg.kt -include-runtime -d build/miximg.jar

native:
	native-image -Djava.awt.headless=false -jar build/miximg.jar build/miximg
