GROUPID=$(mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.groupId -q -DforceStdout)
ARTIFACTID=$(mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.artifactId -q -DforceStdout)
VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -q -DforceStdout)

mvn verify && \
mvn assembly:assembly \
    -DdescriptorId=jar-with-dependencies && \
mvn install:install-file \
    -Dfile=./target/$ARTIFACTID-$VERSION.jar \
    -DgroupId=$GROUPID \
    -DartifactId=$ARTIFACTID \
    -Dversion=$VERSION

