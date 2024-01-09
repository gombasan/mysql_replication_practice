## MySQL Database 이중화 구성 방법

### Docker 를 활용하여 MySQL 이중화를 진행합니다. <br>

1. Docker mysql 이미지를 다운로드 합니다.
```bash
docker pull mysql:latest
```

2. Docker network 를 생성합니다.
```bash
docker network create --driver bridge mybridge
```

3. Master/Slave 를 구성하기 위한 cnf 파일을 HOME 디렉토리에 생성합니다. (프로젝트 내에 .mysql 디렉토리를 참고하여 생성합니다.)
```bash
mkdir -p $HOME/mysql/{master,slave}/{conf,log,data}

# Master cnf 파일
cat <<EOF > $HOME/mysql/master/conf/my.cnf
[mysqld]
log_bin                     = mysql-bin
binlog_format               = ROW
gtid_mode                   = ON
enforce-gtid-consistency    = true
server-id                   = 1
log_slave_updates
datadir                     = /var/lib/mysql
socket                      = /var/lib/mysql/mysql.sock

symbolic-links              = 0

log-error                   = /var/log/mysql/mysqld.log
pid-file                    = /var/run/mysqld/mysqld.pid

report_host                 = master-db

[mysqld_safe]
pid-file                    = /var/run/mysqld/mysqld.pid
socket                      = /var/lib/mysql/mysql.sock
nice                        = 0
EOF

# Slave cnf 파일
cat <<EOF > $HOME/mysql/slave/conf/my.cnf
[mysqld]
log_bin                     = mysql-bin
binlog_format               = ROW
gtid_mode                   = ON
enforce-gtid-consistency    = true
server-id                   = 2
log_slave_updates
datadir                     = /var/lib/mysql
socket                      = /var/lib/mysql/mysql.sock
read_only

# Disabling symbolic-links is recommended to prevent assorted security risks
symbolic-links              = 0

log-error                   = /var/log/mysql/mysqld.log
pid-file                    = /var/run/mysqld/mysqld.pid

report_host                 = slave-db

[mysqld_safe]
pid-file                    = /var/run/mysqld/mysqld.pid
socket                      = /var/lib/mysql/mysql.sock
nice                        = 0
EOF
```
4. Docker mysql 컨테이너를 생성합니다.
```bash
# Master DB
docker run -i -t --name master-db -h master-db --net mybridge --net-alias=master-db -p 3306:3306 \
-v $HOME/mysql/master/conf:/etc/mysql/conf.d \
-v $HOME/mysql/master/log:/var/log/mysql \
-v $HOME/mysql/master/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD="test" -d mysql:latest

# Slave DB
docker run -i -t --name slave-db -h slave-db --net mybridge --net-alias=slave-db -p 3307:3306 \
-v $HOME/mysql/slave/conf:/etc/mysql/conf.d \
-v $HOME/mysql/slave/log:/var/log/mysql \
-v $HOME/mysql/slave/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD="test" -d mysql:latest
```

5. Master DB 에서 Slave DB 로 Replication 을 위한 계정을 생성합니다.
```mysql
CREATE USER 'replicator'@'%' IDENTIFIED WITH mysql_native_password BY 'test';
```

6. Master DB 에서 Slave DB 로 Replication 을 위한 권한을 부여합니다.
```mysql
GRANT REPLICATION SLAVE ON *.* TO 'replicator'@'%';
```

7. Slave DB 에서 Master DB Replication 을 위한 설정을 진행합니다.
```mysql
CHANGE MASTER TO MASTER_HOST = 'master-db', MASTER_USER = 'replicator', MASTER_PASSWORD = 'test', MASTER_AUTO_POSITION = 1;

START SLAVE;
```

8. Slave DB 에서 정상적으로 Master DB 와 연결되었는지 확인합니다.
```mysql
show slave status;

# 결과 중 아래 두개가 Yes 로 나오면 정상적으로 연결된 것입니다.
# Slave_IO_Running: Yes
# Slave_SQL_Running: Yes
```

