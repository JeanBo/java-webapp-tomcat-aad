FROM cvugrinec/ubuntu-azure-powershellandcli
MAINTAINER chvugrin@microsoft.com
COPY create-kubecluster.sh /opt
RUN apt-get update && \
  apt-get install -y git && \
  apt-get install -y jq && \
  apt-get install -y uuid && \
  cd $HOME && \
  git clone https://github.com/cvugrinec/microsoft.git && \
  cd /opt && \
  #git clone https://github.com/Azure/acs-engine.git && \
  wget https://storage.googleapis.com/golang/go1.7.3.linux-amd64.tar.gz && \
  cd /usr/local && \
  tar -zxvf /opt/go1.7.3.linux-amd64.tar.gz && \
  cd /usr/local/bin && \
  curl -O https://storage.googleapis.com/kubernetes-release/release/v1.4.3/bin/linux/amd64/kubectl && \
  chmod 750 kubectl && \
  rm -f /opt/go1.7.3.linux-amd64.tar.gz && \
  mkdir ~/.ssh && \
  mkdir $HOME/gopath && \
  ssh-keygen -t rsa -f  ~/.ssh/id_rsa -P ""
#CMD ["yum","-y","git"]
