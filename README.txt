enter root dir
type: eval $(docker-machine env hadoopdev)
enter docker: ./start-build-env.sh
build: mvn clean package -Pdist -DskipTests -Dtar -Dmaven.javadoc.skip=true
