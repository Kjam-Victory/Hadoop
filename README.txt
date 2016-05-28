enter root dir
type: eval $(docker-machine env hadoopdev)
enter docker: ./start-build-env.sh
build: mvn clean package -Pdist -DskipTests -Dtar -Dmaven.javadoc.skip=true



git instructions:
git init
git remote add cs219 https://xxxx.xxx 
git pull cs219 master:origin
xxxx modify code
git status
git commit -m "Commit Message"
git push cs219 origin master:[Your Name]
