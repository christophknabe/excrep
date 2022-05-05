#Runs the rest of the command line with JAVA_HOME defined using one of the
#JDKs distributed by https://sdkman.io/

#Test usage: 
# $ ./javafx8.sh env | grep JAVA_HOME
# JAVA_HOME=/home/knabe/.sdkman/candidates/java/8.0.332.fx-librca

#Example Usage: ./javafx8.sh ./mvnw clean test

#On Linux only the Liberica distribution of Java 8 contains JavaFX:
jdkVersion=8.0.332.fx-librca
#jdkVersion=11.0.11.hs-adpt
JAVA_HOME=$HOME/.sdkman/candidates/java/$jdkVersion "$@"

