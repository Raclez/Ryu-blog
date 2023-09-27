pipeline {
    agent any
    stages {
        stage('拉取代码') {
            steps {
               git credentialsId: 'a1dbebb9-2d4b-426e-804e-42e17be64446', url: 'https://gitee.com/Ryucal/Ryu.blog.git'
               echo '拉取成功'
            }
        }

        stage('执行构建') {
            steps {
                sh "mvn clean package -Dmaven.test.skip=true"
                echo '构建完成'
            }
        }

        stage('把jar包构建为docker镜像并运行') {
            parallel {
               stage('构建gateway镜像') {
                steps {
            sh '''cd Ryu_gateway/target
            docker build -t ryu-gateway:latest -f ../src/main/resources/Dockerfile .'''
                echo '运行成功'
            }
}
               stage('构建admin镜像') {
                steps {
            sh '''cd Ryu_admin/target
            docker build -t ryu-admin:latest -f ../src/main/resources/Dockerfile .'''
                echo '运行成功'
            }
}
               stage('构建picture镜像') {
                steps {
            sh '''cd Ryu_picture/target
            docker build -t ryu-picture:latest -f ../src/main/resources/Dockerfile .'''
                echo '运行成功'
            }
}
               stage('构建search镜像') {
                steps {
            sh '''cd Ryu_search/target
            docker build -t ryu-search:latest -f ../src/main/resources/Dockerfile .'''
                echo '运行成功'
            }
}
               stage('构建spider镜像') {
                steps {
            sh '''cd Ryu_spider/target
            docker build -t ryu-spider:latest -f ../src/main/resources/Dockerfile .'''
                echo '运行成功'
            }
}
               stage('构建web镜像') {
                steps {
            sh '''cd Ryu_web/target
            docker build -t ryu-web:latest -f ../src/main/resources/Dockerfile .'''
                echo '运行成功'
            }
}
               stage('构建sms镜像') {
                steps {
            sh '''cd Ryu_sms/target
            docker build -t ryu-sms:latest -f ../src/main/resources/Dockerfile .'''
                echo '运行成功'
            }
}
               stage('构建monitor镜像') {
                steps {
            sh '''cd Ryu_monitor/target
            docker build -t ryu-monitor:latest -f ../src/main/resources/Dockerfile .'''
                echo '运行成功'
            }
}
            }
        }
    }
}
