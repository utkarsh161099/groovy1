job("ak1_groovy"){
	   description("devtask6.1")
	  scm{
	     github('https://github.com/utkarsh161099/groovy' , 'master')
	}
	steps{
	  shell('sudo  cp  -rvf  *  /dev6')
	}
	triggers{
	   gitHubPushTrigger()
	}

}




job("devtask6.2"){
  steps{
   shell('''
     if   sudo  ls  /dev6  |  grep  html
    then    
             if  sudo kubectl  get pods --selector  "app in (apache)"  |  grep  apache-pod
             then
                   POD1=$(sudo  kubectl get pods  -l app=apache  -o jsonpath="{.items[0].metadata.name}")
                   echo  $POD1
                  sudo   kubectl  cp   /dev6/index.html  $POD1:/var/www/html
                  sudo   kubectl  cp   /dev6/vish.html  $POD1:/var/www/html
          else
                  sudo  kubectl   apply  -f  /dev6/apachepod.yml
                   POD1=$(sudo  kubectl get pods  -l app=apache  -o jsonpath="{.items[0].metadata.name}")
                   echo  $POD1
                  sudo   kubectl  cp   /dev6/index.html  $POD1:/var/www/html
                  sudo   kubectl  cp   /dev6/vish.html  $POD1:/var/www/html
         fi
    else  
          echo  "no html file" 
    fi
    if   sudo  ls  /dev6  |  grep  php
    then    
             if  sudo kubectl  get pods --selector  "app in (php)"  |  grep  php-pod
             then
                   POD2=$(sudo  kubectl get pods  -l app=php  -o jsonpath="{.items[0].metadata.name}")
                   echo  $POD2
                  sudo   kubectl  cp   /dev6/vishal.php  $POD2:/var/www/html
          else
                  sudo  kubectl   apply  -f  /dev6/phpod.yml
                   POD2=$(sudo  kubectl get pods  -l app=php  -o jsonpath="{.items[0].metadata.name}")
                   echo  $POD2
                  sudo   kubectl  cp   /dev6/vishal.php  $POD2:/var/www/html
         fi
    else  
          echo  "no html file" 
    fi
''')
}
  triggers{
    upstream('devtask6.1' , 'SUCCESS')
}
}


job("devtask6.3"){
	   steps{
	         shell('''
	          status=$(curl  -o  /dev/null  -s  -w  "%{http_code}"   http://192.168.99.101:31000)
	          if  [[ $status ==  200 ]]
	          then
	               echo  "apache html is running"
	               exit  0
	         else
	              exit 1
	         fi
	
	         status=$(curl  -o  /dev/null  -s  -w  "%{http_code}"   http://192.168.99.103:32000)
	          if  [[ $status ==  200 ]]
	          then
	               echo  "apache php is running"
	               exit  0
	         else
	              exit 1
	         fi
	''')
	}
	  triggers{
	    upstream('devtask6.2' , 'SUCCESS')
	}
	  publishers {
	    extendedEmail {
	        recipientList('utkarsh161099@gmail.com')
	        defaultSubject('Job status')
	               attachBuildLog(attachBuildLog = true)
	        defaultContent('Status Report')
	        contentType('text/html')
	       triggers {
	            always {
	            subject('build Status')
	            content('Body')
	           sendTo{
	              developers()
	             recipientList()
	            }
	        }
	    }
	 }
	}
}
