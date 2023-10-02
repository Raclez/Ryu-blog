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

        stage('把jar包构建为docker镜像') {
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
        stage('pull镜像到阿里云') {
            parallel {
                stage('pull ryu-gateway镜像') {
                    steps {
                        sh '''
           echo "475118582rl" | docker login --username=ryucay registry.cn-hangzhou.aliyuncs.com --password-stdin
 docker tag ryu-gateway:latest registry.cn-hangzhou.aliyuncs.com/ryu_blog/ryu-gateway:latest
 docker push registry.cn-hangzhou.aliyuncs.com/ryu_blog/ryu-gateway:latest
            '''
                        echo '运行成功'
                    }
                }
//                stage('启动admin镜像') {
//                    steps {
//                        sh '''
//                 echo "475118582rl" | docker login --username=ryucay registry.cn-hangzhou.aliyuncs.com --password-stdin
//    docker tag ryu-admin:latest registry.cn-hangzhou.aliyuncs.com/ryu_blog/ryu-admin:latest
//             docker push registry.cn-hangzhou.aliyuncs.com/ryu_blog/ryu-admin:latest
//            '''
//                        echo '运行成功'
//                    }
//                }
//                stage('启动picture镜像') {
//                    steps {
//                        sh '''
//            docker run   -d --name ryu-picture -p 8602:8602 ryu-picture:latest
//                        '''
//                        echo '运行成功'
//                    }
//
//                }
//                stage('启动search镜像') {
//                    steps {
//                        sh "docker run   -d --name ryu-search -p 8605:8605 ryu-search:latest"
//
//                        echo '运行成功'
//                    }
//                }
//                stage('启动spider镜像') {
//                    steps {
//                        sh '''
//            docker run   -d --name ryu-spider -p 8608:8608 ryu-spider:latest
//            '''
//                        echo '运行成功'
//                    }
//                }
//                stage('启动web镜像') {
//                    steps {
//                        sh '''
//            docker run   -d --name ryu-web -p 8603:8603 ryu-web:latest
//            '''
//                        echo '运行成功'
//                    }
//                }
//                stage('启动sms镜像') {
//                    steps {
//                        sh '''
//            docker run   -d --name ryu-sms -p 8604:8604 ryu-sms:latest
//            '''
//                        echo '运行成功'
//                    }
//                }
//                stage('启动monitor镜像') {
//                    steps {
//                        sh '''
//            docker run   -d --name ryu-monitor -p 8606:8606 ryu-monitor:latest
//            '''
//                        echo '运行成功'
//                    }
//                }
            }
        }
        stage('运行镜像') {
            parallel {
                stage('启动gateway镜像') {
                    steps {
                        sh '''
            docker run   -d --name ryu-gateway -p 8607:8607 ryu-gateway:latest
            '''
                        echo '运行成功'
                    }
                }
                stage('启动admin镜像') {
                    steps {
                        sh '''
            docker run   -d --name ryu-admin -p 8601:8601 ryu-admin:latest'''
                        echo '运行成功'
                    }
                }
                stage('启动picture镜像') {
                    steps {
                        sh '''
            docker run   -d --name ryu-picture -p 8602:8602 ryu-picture:latest
                        '''
                        echo '运行成功'
                    }

                }
                stage('启动search镜像') {
                    steps {
                        sh "docker run   -d --name ryu-search -p 8605:8605 ryu-search:latest"

                        echo '运行成功'
                    }
                }
                stage('启动spider镜像') {
                    steps {
                        sh '''
            docker run   -d --name ryu-spider -p 8608:8608 ryu-spider:latest
            '''
                        echo '运行成功'
                    }
                }
                stage('启动web镜像') {
                    steps {
                        sh '''
            docker run   -d --name ryu-web -p 8603:8603 ryu-web:latest
            '''
                        echo '运行成功'
                    }
                }
                stage('启动sms镜像') {
                    steps {
                        sh '''
            docker run   -d --name ryu-sms -p 8604:8604 ryu-sms:latest
            '''
                        echo '运行成功'
                    }
                }
                stage('启动monitor镜像') {
                    steps {
                        sh '''
            docker run   -d --name ryu-monitor -p 8606:8606 ryu-monitor:latest
            '''
                        echo '运行成功'
                    }
                }
            }
        }


    }
}