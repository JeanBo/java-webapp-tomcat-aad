class testdemo1::somedirs {

     exec { "usermgmt_by_puppet":
      command => "useradd cvugrinec -d /home/cvugrinec -m",
      path => '/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/root/bin',
      returns => [0, 9],
     }->
     exec { "usermgmt_by_puppet_hack":
      command => "echo \"cvugrinec:Welcome123!\" | chpasswd",
      path => '/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/root/bin',
      returns => [0, 9],
     }->
     file { ["/opt/testdemo1","/var/log/testdemo1"]:
      recurse => true,
      ensure => 'directory',
      owner => 'cvugrinec',
      mode => '0750',
    }

}
