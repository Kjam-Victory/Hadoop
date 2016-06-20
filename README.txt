Git Instructions:
enter root dir
type: eval $(docker-machine env hadoopdev)
enter docker: ./start-build-env.sh
build: mvn clean package -Pdist -DskipTests -Dtar -Dmaven.javadoc.skip=true



git instructions:
git init
git remote add cs219 https://xxxx.xxx 
git pull cs219 master:origin
xxxx modify code
git add -A
git status
git commit -m "Commit Message"
git push cs219 origin master:[Your Name]


On android:
1. install sqlite3, rsync, java8(JAVA_HOME)
2. mkdir /hadoop, /hadoop-conf, /hadoop-bin
3. copy hadoop-conf.tar.gz, hadoop-bin.tar.gz to /, and decompress them
4. configure localhost ssh
5. run hadoop-conf/hadoop_start.sh
